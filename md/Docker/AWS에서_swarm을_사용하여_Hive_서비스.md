## AWS에서 swarm을 사용하여 Hive 서비스

### 서비스 구동 절차

1. AMI를 사용하여 EC2 생성

   * manager 노드는 docker-swarm-manager AMI 사용

     * 실서비스에는 기본 3개로 지정

   * worker 노드는 docker-swarm-for-aws AMI 사용

     * 오토스케일링으로 확장될 요소들

     * manager 노드가 정상적으로 구동된 후에 생성해야 함. (join해야 하므로)

     * worker launch 시에는 UserData를 통해 manager에 join 수행

       ```shell
       #!/bin/bash
       docker swarm join --token SWMTKN-1-42hrtbs9zwam7n3oez6dy5wmrnywjmm0ndfxtppk67xalchwwq-0yhly80f0rhfqbch6rqm15hwn <manager의 내부 IP>:2377
       ```

       ​

2. manager 인스턴스로 접속해서 worker와 manager가 정상적으로 등록되었는지 확인

   ```shell
   $ sudo docker node ls
   ```

3. 인스턴스들을 ELB에 연결

   * ELB는 Classic Load Balancer 사용
   * 인스턴스 탭에서 Edit Instance 선택
   * 사용되는 인스턴스 리스트들 선택
   * Listener 탭에서 포워딩 규칙 지정
     * tomcat으로 연결할 80:8080
     * visualizer로 연결할 5000:5000
   * 포워딩이 추가로 필요한 경우 보안 그룹에도 추가해줘야함.

4. 노드 확인을 위해 visualizer 구동

   ```shell
   $ sudo docker service create --name=viz --publish=5000:8080/tcp --constraint=node.role==manager --mount=type=bind,src=/var/run/docker.sock,dst=/var/run/docker.sock dockersamples/visualizer
   ```

5. manager 노드에서 tomcat 서비스 생성

   ```shell
   $ sudo docker service create --name hive-server --publish 8080:8080 --with-registry-auth 138011803946.dkr.ecr.ap-northeast-1.amazonaws.com/hive-tomcat:latest
   ```

   * ECR에서 이미지를 가져와서 서비스를 생성하기 위해 —with-registry-auty 옵션을 주어야 한다.
     * 이 옵션은 registry에 대한 인증 정보를 에이전트들에게 전달한다.

   * 처음부터 여러개의 컨테이너를 생성할 경우 —replicas 옵션을 주어야 한다.

     ```shell
     --replicas 3
     ```

6. scale in/out 테스트

   ```shell
   $ sudo docker service scale hive-server=4
   ```

   * scale out은 현재 컨테이너 수보다 많이 지정
   * scale in은 현재 컨테이너 수보다 적게 지정

7. 서비스 update 테스트

   ```shell
   $ sudo docker service update --image 138011803946.dkr.ecr.ap-northeast-1.amazonaws.com/hive-tomcat:latest hive-server
   ```




## 이미지 업데이트 절차

1. 호스트에서 업데이트할 tomcat 컨테이너 구동

2. 구동된 tomcat 컨테이너에 업데이트 내용 적용

3. logs 디렉토리의 내용 모두 제거

4. docker commit

   ```shell
   $ docker commit <컨테이너ID> 138011803946.dkr.ecr.ap-northeast-1.amazonaws.com/hive-tomcat:<버젼>
   ```

   * 버젼은 redmine의 wiki 페이지 참조

5. docker push

   ```shell
   $ docker push 138011803946.dkr.ecr.ap-northeast-1.amazonaws.com/hive-tomcat:<버젼>
   ```

6. latest 갱신

   ```shell
   $ docker tag 138011803946.dkr.ecr.ap-northeast-1.amazonaws.com/hive-tomcat:<버젼> 138011803946.dkr.ecr.ap-northeast-1.amazonaws.com/hive-tomcat:latest
   $ docker push 138011803946.dkr.ecr.ap-northeast-1.amazonaws.com/hive-tomcat:latest
   ```

7. AWS의 manager EC2 인스턴스로 접속한 후 update 명령

   ```shell
   $ sudo docker service update --image 138011803946.dkr.ecr.ap-northeast-1.amazonaws.com/hive-tomcat:latest hive-server
   ```

   * 업데이트 시 latest보다 해당 버젼으로 명시하면 visualizer에서 현재 버젼을 확인할 수 있어서 좋음.



### 추가로 해야되는 작업

* 오토스케일링 그룹 지정
* 오토 스케일링 시 해당 인스턴스 ELB에 자동 추가
* 컨테이너 축소 시 종료 스크립트 수행 가능 여부 찾아보기
  * 가능할 경우 filebeat 전송 완료되면 종료되도록 설정
* ~~컨테이너 구동 시 filebeat 시작되도록 설정~~
* swarm에서 특정 노드의 컨테이너 종료 가능한지 검토
* 컨테이너 종료 시 스크립트 수행가능한지 검토
  * filebeat 정상 종료 되도록 스크립트로 체크
* 이미지 업데이트 과정 jenkins로 자동화
* EC2 인스턴스 terminate 시 스크립트 실행 가능 여부 검토
  * linux의 shutdown 시 script 실행으로 찾아보는 것이 나을 듯





### 오토스케일링 정책

- swarm에서 사용할 이미지들의 registry는 AWS ECR 사용
  - 현재는 Seoul 리전에 ECR 서비스가 없으므로 Tokyo 사용
  - 이 때 ECR에 로그인 하기 위해 awscli가 필요
  - 이를 위해 초기 구성으로 awscli 설치와 ECR 로그인까지 마친 VM을 AMI로 생성
- 오토 스케일링 시 위에서 만든 AMI를 사용하여 EC2 인스턴스를 생성하고 UserData 스크립트를 통해 swarm manager 노드에 join
  - 미리 스크립트에 넣을 수 있도록 join 명령이 필요
  - 이를 위해 manager를 초기화하여 join 이 가능한 AMI를 생성해둠
  - 오토 스케일링 될 worker들은 UserData 스크립트를 통해 manager node에 조인
- 오케스케일링이 완료 되면 scale 명령으로 서비스 확장