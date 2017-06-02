## AWS에서 swarm을 사용하여 Hive 서비스

### 서비스 구동 절차

1. AMI를 사용하여 EC2 생성

   * manager 노드는 docker-swarm-manager AMI 사용

     * manager AMI에는 docker swarm init 명령을 통해 swarm 구성이 되어있다.

       ```shell
       $ docker swarm init --advertise-addr <manager 인스턴스의 내부 IP>
       ```

     * 실서비스에는 기본 3개로 지정

   * worker 노드는 docker-swarm-for-aws AMI 사용

     * 오토스케일링으로 확장될 요소들

     * manager 노드가 정상적으로 구동된 후에 생성해야 함. (join해야 하므로)

     * worker launch 시에는 UserData를 통해 manager에 join 수행

       ```shell
       #!/bin/bash
       $(aws ecr get-login --region ap-northeast-1)
       docker swarm join --token SWMTKN-1-19qqs4w5nf1t59rnebr1x3xu82kzrzk0m4454o4m4ezmtmotl5-6l0w196grl7vr2pehh294dwmm <manager의 내부 IP>:2377
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
   $ sudo docker service create --name hive-server --publish 8080:8080 --mount type=bind,src=/home/ubuntu/logs,dst=/usr/local/tomcat/logs --reserve-cpu 1 --with-registry-auth 138011803946.dkr.ecr.ap-northeast-1.amazonaws.com/hive-tomcat:latest
   ```

   * ECR에서 이미지를 가져와서 서비스를 생성하기 위해 —with-registry-auty 옵션을 주어야 한다.
     * 이 옵션은 registry에 대한 인증 정보를 에이전트들에게 전달한다.

   * 처음부터 여러개의 컨테이너를 생성할 경우 —replicas 옵션을 주어야 한다.

     ```shell
     --replicas 3
     ```

   * 볼륨 지정을 하려면 각 머신에 볼륨으로 지정할 디렉토리가 생성되어 있어야 한다.

      ```shell
      --mount type=bind,src=<HOST_PATH>,dst=<CONTAINER-PATH>
      ```

   * 한 머신에 너무 많은 컨테이너가 구동되지 않도록 제한을 주기 위해 —reserve-cpu 옵션 사용

     * CPU 하나 당 컨테이너 하나

       ```shell
       --reserve-cpu 1
       ```

     * **이를 사용하면 오토스케일링이 가능해짐.**

     * 미리 scale을 넉넉히 늘려 놓은 후 EC2의 오토스케일링을 통해 worker가 매니저로 join하게 되면 늘어난 CPU 수 만큼 서비스의 개수도 늘어나게 된다.

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




### Swarm Volume Mount

* docker run과 동일하게 호스트의 볼륨과 마운트

  ```shell
  $ docker service create --name test-tomcat --publish 8082:8080 --mount type=bind,src=`pwd`/tomcat,dst=/usr/local/tomcat/logs tomcat
  ```

  * src에 해당하는 위치에 해당 디렉토리가 존재해야함

* nfs 사용

  * 방법1 : 미리 특정 디렉토리를 NFS를 통해 연결 후 위와 동일하게 해당 디렉토리 마운트

  * 방법2 : docker service create에 mount type을 nfs로 지정

    ```shell
    $ docker volume create --driver local \
        --opt type=nfs \
        --opt o=addr=192.168.1.1,rw \
        --opt device=:/path/to/dir \
        foo
    ```




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




### 로그 파일 저장 정책

#### 고려 사항

1. EFS를 사용하여 EC2 인스턴스 간 공유 디렉토리 사용
   - EFS는 서울, 도쿄 리전 서비스 없음
   - 가격이 EBS보다 세배 가량 비쌈
2. EC2 인스턴스 간 NFS를 통해 파일 공유
   - EC2 인스턴스 하나가 NFS Server가 되므로 해당 인스턴스에 장애가 생기면 모든 서버에 문제가 발생할 수 있음
3. 각 EC2 인스턴스에 구동되고 있는 컨테이너에 대한 로그 저장



#### 각 EC2 인스턴스에 구동되고 있는 컨테이너에 로그 저장

* 위 세가지 고려사항 중 3번으로 결정
  * swarm의 service를 수행하는 각 머신 별로 로그를 남긴다.

- swarm의 각 컨테이너별로 볼륨을 지정할 수는 없고 서비스 단위로 볼륨이 정해지기 때문에 특정 디렉토리를 mount하면 동일한 이름의 로그인 경우 덮어써짐.

  - tomcat의 로그와 logback을 통한 로그 모두 파일 명에 hostname을 포함하는 방식으로 개별 파일이 생성되도록 해야함.

- tomcat 로그 파일명 변경 방법

  - tomcat의 conf 디렉토리에 있는 logging.properties파일은 글로벌 설정이므로 등록된 모든 어플리케이션에 적용된다.

  - 개별 어플리케이션의 WEB-INF/classes/ 하위에 logging.properties를 두고 설정하면 해당 어플리케이션에만 설정이 적용된다.

  - 이 때 글로벌과 중복되게 설정을 하는 경우 로그 파일이 두개가 생성되므로 글로벌 설정을 수정해주어야 한다.

    - 글로벌 설정에서 각 파일 핸들러 설정을 제거하여 로그 파일이 생성되지 않도록 설정

  - tomcat/conf/logging.properties

    ```properties
    handlers = java.util.logging.ConsoleHandler
    .handlers = java.util.logging.ConsoleHandler

    java.util.logging.ConsoleHandler.level = FINE
    java.util.logging.ConsoleHandler.formatter = org.apache.juli.OneLineFormatter
    ```

  - 개별 어플리케이션의 WEB-INF/classes/logging.properties

    ```properties
    handlers = 1catalina.org.apache.juli.AsyncFileHandler, 2localhost.org.apache.juli.AsyncFileHandler, 3manager.org.apache.juli.AsyncFileHandler, 4host-manager.org.apache.juli.AsyncFileHandler, java.util.logging.ConsoleHandler

    .handlers = 1catalina.org.apache.juli.AsyncFileHandler, java.util.logging.ConsoleHandler

    ############################################################
    # Handler specific properties.
    # Describes specific configuration info for Handlers.
    ############################################################

    1catalina.org.apache.juli.AsyncFileHandler.level = FINE
    1catalina.org.apache.juli.AsyncFileHandler.directory = ${catalina.base}/logs
    1catalina.org.apache.juli.AsyncFileHandler.prefix = catalina.${classloader.hostName}.

    2localhost.org.apache.juli.AsyncFileHandler.level = FINE
    2localhost.org.apache.juli.AsyncFileHandler.directory = ${catalina.base}/logs
    2localhost.org.apache.juli.AsyncFileHandler.prefix = localhost.${classloader.hostName}.

    3manager.org.apache.juli.AsyncFileHandler.level = FINE
    3manager.org.apache.juli.AsyncFileHandler.directory = ${catalina.base}/logs
    3manager.org.apache.juli.AsyncFileHandler.prefix = manager.${classloader.hostName}.

    4host-manager.org.apache.juli.AsyncFileHandler.level = FINE
    4host-manager.org.apache.juli.AsyncFileHandler.directory = ${catalina.base}/logs
    4host-manager.org.apache.juli.AsyncFileHandler.prefix = host-manager.${classloader.hostName}.

    java.util.logging.ConsoleHandler.level = FINE
    java.util.logging.ConsoleHandler.formatter = org.apache.juli.OneLineFormatter
    ```


    ############################################################
    # Facility specific properties.
    # Provides extra control for each logger.
    ############################################################
    
    org.apache.catalina.core.ContainerBase.[Catalina].[localhost].level = INFO
    org.apache.catalina.core.ContainerBase.[Catalina].[localhost].handlers = 2localhost.org.apache.juli.AsyncFileHandler
    
    org.apache.catalina.core.ContainerBase.[Catalina].[localhost].[/manager].level = INFO
    org.apache.catalina.core.ContainerBase.[Catalina].[localhost].[/manager].handlers = 3manager.org.apache.juli.AsyncFileHandler
    
    org.apache.catalina.core.ContainerBase.[Catalina].[localhost].[/host-manager].level = INFO
    org.apache.catalina.core.ContainerBase.[Catalina].[localhost].[/host-manager].handlers = 4host-manager.org.apache.juli.AsyncFileHandler
    ​```
    
    * swarm을 통해 같은 볼륨에 로그가 쌓일 경우 파일이 덮어쓰여지지 않도록 파일명 prefix에 호스트명 추가
      * catalina.${classloader.hostName}.

-   하지만 이 방법대로 했더니만 파일명은 제대로 바뀌는데 아래와 같은 TLD 관련 메시지가 남으면서 애플리케이션의 로그가 남지 않는 문제가 발생. 

    ```
    30-May-2017 10:46:46.735 정보 [localhost-startStop-1] org.apache.jasper.servlet.TldScanner.scanJars At least one JAR was scanned for TLDs yet contained no TLDs. Enable debug logging for this logger for a complete list of JARs that were scanned but no TLDs were found in them. Skipping unneeded JARs during scanning can improve startup time and JSP compilation time.
    ```

- 그래서 톰캣 구동 시 인자로 호스트명을 전달 받아서 logging.properties에서 인자를 참조하여 파일명을 변경하는 방식으로 결정

    - bin/catalina.sh 파일을 수정하여 JAVA_OPTS에 호스트명 전달

      ```
      -Dcustom.hostname=$HOSTNAME
      ```

    - conf/logging.properties 파일을 수정하여 각 로그 파일 경로의 호스트명으로 된 디렉토리 안에 분류 보관

      ```
      1catalina.org.apache.juli.AsyncFileHandler.directory = ${catalina.base}/logs/${custom.hostname}.
      ```

    - 그러면 야래와 같이 호스트명의 디렉토리에 로그 파일이 생성됨.

      ```
      /usr/local/tomcat/logs/17b163c2c43c/catalina.2017-05-30.log
      ```

- logback 로그 파일명 변경 방법

  - Servlet 초기화 시 호스트명과 현재 날짜 기록

    ```java
    try {
        System.setProperty("hostName", InetAddress.getLocalHost().getHostName());
    } catch (UnknownHostException e) {
        System.out.println("Error Message : " + e.getMessage());
        e.printStackTrace();
    }
    ```

  - logback.xml 파일에 파일 패턴 설정

    ```xml
    <appender name="DEBUG" class="ch.qos.logback.core.rolling.RollingFileAppender">
        ...
        <file>/usr/local/tomcat/logs/${hostName}/debug.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <!-- rollover daily -->
            <fileNamePattern>/usr/local/tomcat/logs/${hostName}/debug-%d{yyyy-MM-dd_HH-mm-ss}.%i.txt</fileNamePattern>
            ...
        </rollingPolicy>
        ...
    </appender>
    ```

    * ${hostName}은 위 자바 코드에서 System Property에 저장한 값




## 이슈

### webapps 하위의 deploy된 디렉토리가 제거되는 문제

* 문제 발생
  * Hive.war 파일을 webapps 디렉토리 하위에 넣어두면 autoDeploy 기능으로 인해 자동으로 압축이 해제되고 Hive 디렉토리가 생성된다.
  * 이 때 Hive.war 파일을 제거하고 tomcat을 재시작하면 Hive 디렉토리가 제거된다.

* 원인

  * autoDeploy 기능으로 인해 생성된 디렉토리는 내부적으로 war파일과 연결된 확장 디렉토리로써 관리가 된다. 
  * WAR 파일을 삭제하면 연결된 확장 디렉토리, 컨텍스트 파일 및 작업 디렉토리가 제거 된 상태에서 응용 프로그램의 배포가 해제되고, 현재 사용자 세션 또한 유지되지 않는다.

* 해결

  * conf/server.xml에서 autoDeploy 속성을 false로 지정하여 수동으로 배포하도록 설정

    ```xml
    <Host name="localhost"  appBase="webapps"
                unpackWARs="true" autoDeploy="false">
    ```

  * webapps 디렉토리 하위에 unzip을 통해 war 파일을 배포

    ```shell
    $ unzip -d /usr/local/tomcat/webapps/Hive Hive.war
    ```

* 참고

  * https://serverfault.com/questions/192784/why-does-tomcat-like-deleting-my-context-xml-file
  * http://tomcat.apache.org/tomcat-6.0-doc/config/host.html#Automatic%20Application%20Deployment



### 추가로 해야되는 작업

- 오토스케일링 그룹 지정
- 오토 스케일링 시 해당 인스턴스 ELB에 자동 추가
- 컨테이너 축소 시 종료 스크립트 수행 가능 여부 찾아보기
  - 가능할 경우 filebeat 전송 완료되면 종료되도록 설정
- ~~컨테이너 구동 시 filebeat 시작되도록 설정~~
- swarm에서 특정 노드의 컨테이너 종료 가능한지 검토
- 컨테이너 종료 시 스크립트 수행가능한지 검토
  - filebeat 정상 종료 되도록 스크립트로 체크
- 이미지 업데이트 과정 jenkins로 자동화
- EC2 인스턴스 terminate 시 스크립트 실행 가능 여부 검토
  - linux의 shutdown 시 script 실행으로 찾아보는 것이 나을 듯
- swarm에서 볼륨 지정하여 사용하는 방법 숙지
- ~~swarm에서 로그 파일 저장 정책 결정~~

