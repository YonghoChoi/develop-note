# docker swarm for aws

### awscli 설정

AWS의 각 리소스들에 접근하고 사용을 하기 위해서는 awscli가 설치되어 있어야 하고, 사용해야 하는 리소스들에 접근이 가능한 계정의 ACCESS_KEY와 SECRET_KEY가 필요하다.



#### awscli 설치

- pip 설치

  ```shell
  $ sudo apt-get install python-pip python-dev build-essential
  ```

- awscli 설치

  ```shell
  $ pip install --upgrade --user awscli
  ```



#### aws 설정

* AWS 콘솔에서 ACCESS_KEY와 SECRET_KEY 발급

  * IAM > Users > 사용자 선택 > Security Credentials 탭 > Create access key

    ![](images/swarm_for_aws_1.png)

  * 해당 키의 SECRET_KEY는 발급 시에만 확인 가능하므로 잘 저장해둬야 한다.

    ![](images/swarm_for_aws_2.png)

    * Show를 클릭하여 확인 가능하고, csv로 다운로드도 가능


* `~/.aws/credentials`에서 ACCESS_KEY와 SECRET_KEY 입력
* `~/.aws/config`에서 default region 설정
  * 추후 사용되는 aws 명령은 기본적으로 default region에 설정한 국가의 리소스에 접근한다.
  * --region 옵션을 통해 특정 리전을 지전 하는 것도 가능.



### ec2 인스턴스 생성

* shell 명령 시 사용할 변수 설정

  ```shell
  $ VPC=vpc-25eba94c
  $ REGION=ap-northeast-2
  $ SUBNET=subnet-f391c39a
  $ ZONE=a
  $ SECURITY_GROUP_NAME=swarmTest
  ```

  * 옵션에 직접 입력해도 상관 없지만 나중에 변경이 필요한 경우 관리를 편하게 하기 위해 변수에 지정한다.
  * export 명령을 통해 환경 변수로 등록하여 사용해도 된다.

* manager 인스턴스 생성

  ```shell
  $ docker-machine create -d amazonec2 --amazonec2-vpc-id \
    $VPC --amazonec2-region $REGION --amazonec2-zone $ZONE \
    --amazonec2-instance-type t2.micro --amazonec2-subnet-id $SUBNET \
    --amazonec2-security-group $SECURITY_GROUP_NAME test-swarm-manager
  ```

  * docker-machine create 명령을 통해 [드라이버를 amazonec2를 선택하여 EC2 인스턴스를 생성](https://docs.docker.com/machine/drivers/aws/)하는 경우 뒤에 오는 설정들을 적용한 EC2 인스턴스가 만들어지고 **기본적으로 docker까지 설치**가 된다.

  * 위 명령으로 ec2 instance 를 생성하면 각각의 인스턴스마다 ssh 키가 발급된다. 

    * ~/.docker/machine/machines/\<ec2인스턴스명\> 위치에 키 생성

  * 이 후에 같은 키를 통해 EC2 인스턴스를 생성하려면 위 명령에서 아래 옵션 추가

    ```shell
    --amazonec2-ssh-keypath ~/.docker/machine/machines/<ec2인스턴스명>/id_rsa
    ```

  * 미리 생성해 둔 AMI를 사용하여 인스턴스를 생성하려면 아래 옵션 추가

    ```shell
    --amazonec2-ami <AMI ID>
    ```

  * 아래와 같은 생성 절차를 수행하기 때문에 꽤 오랜 시간이 소요된다.

    ```
    Running pre-create checks...
    Creating machine...
    (test-swarm-manager) Launching instance...
    Waiting for machine to be running, this may take a few minutes...
    Detecting operating system of created instance...
    Waiting for SSH to be available...
    Detecting the provisioner...
    Provisioning with ubuntu(systemd)...
    Installing Docker...
    Copying certs to the local machine directory...
    Copying certs to the remote machine...
    Setting Docker configuration on the remote daemon...
    Checking connection to Docker...
    Docker is up and running!
    ```


* worker node 인스턴스 생성

  ```shell
  $ docker-machine create -d amazonec2 --amazonec2-vpc-id $VPC \
    --amazonec2-region $REGION --amazonec2-zone $ZONE \
    --amazonec2-instance-type t2.micro --amazonec2-subnet-id $SUBNET \
    --amazonec2-security-group $SECURITY_GROUP_NAME test-swarm-node
  ```




### swarm 구성

* swarm manager의 ip 주소 확인

  ```shell
  $ docker-machine ssh test-swarm-manager ifconfig eth0
  ```

* `docker-machine env test-swarm-manager` 명령을 수행하면 매니저 노드로 docker 명령어를 수행할 수 있도록 하기 위한 환경 변수 설정 정보가 출력된다. 하단에 명시된 명령어를 수행하면 호스트의 docker 관련 환경 변수값이 설정된다.

* 내 호스트에서 해당 머신(EC2 인스턴스)으로 docker 명령을 할 수 있도록 접속

  ```shell
  $ eval $(docker-machine env test-swarm-manager)
  ```

* swarm mode 초기화

  ```shell
  $ docker swarm init --advertise-addr <manager 인스턴스의 내부 IP>
  ```

  * 명령을 수행하면 worker node들이 접속할 수 있는 swarm join 명령을 반환한다. 
  * 반환된 명령문에는 토큰 값이 포함되어 있으므로 텍스트 모두 잘 복사해둔다.

* swarm manager로 join하기 위해 보안그룹의 정책을 수정해야 한다. 먼저 보안 그룹의 내용 확인

  ```shell
  $ aws ec2 describe-security-groups --filter "Name=group-name,Values=swarmTest"
  ```

  * 어떠한 보안 그룹을 선택할지 정하기 위해 —filter 옵션을 사용하여 group-name이 swarmTest인 보안 그룹 선택

  * default region이 아닌 다른 region의 정보를 가져오려면 aws 옵션으로 `—region`을 명시

    ```shell
    $ aws --region ap-northeast-1 ec2 describe-security-groups --filter "Name=group-name,Values=swarmTest"
    ```

* 위에서 얻은 정보에서 보안 그룹 고유 ID를 획득한 후 아래 명령 수행

  ```shell
  $ SECURITY_GROUP_ID=sg-?? # 위에서 획득한 보안 그룹 고유 ID
  $ aws ec2 authorize-security-group-ingress --group-id $SECURITY_GROUP_ID \
    --protocol tcp --port 2377 --source-group $SECURITY_GROUP_ID
  $ aws ec2 authorize-security-group-ingress --group-id $SECURITY_GROUP_ID \
    --protocol tcp --port 7946 --source-group $SECURITY_GROUP_ID
  $ aws ec2 authorize-security-group-ingress --group-id $SECURITY_GROUP_ID \
    --protocol udp --port 7946 --source-group $SECURITY_GROUP_ID
  $ aws ec2 authorize-security-group-ingress --group-id $SECURITY_GROUP_ID \
    --protocol tcp --port 4789 --source-group $SECURITY_GROUP_ID
  $ aws ec2 authorize-security-group-ingress --group-id $SECURITY_GROUP_ID \
    --protocol udp --port 4789 --source-group $SECURITY_GROUP_ID
  ```

  * authorize-security-group-ingress 명령을 사용하면 보안 그룹에 수신 규칙을 추가한다.
  * 뒤에 `—source-group`을 지정하는 이유는 접근 권한을 동일한 보안 그룹에 속한 인스턴스들만 허용하도록 하기 위함.
  * swarm은 내부적으로 노드들 간의 통신을 위해 2377, 7946, 4789 포트를 사용하고 있으므로 같은 보안그룹 내의 인스턴스들 간에는 통신이 가능하도록 보안 그룹을 설정한다.
    * 2377 : manager 노드에서 기본적으로 listen하고 있는 포트
    * 7946 : 컨테이너 네트워크 검색을 위한 TCP/UDP 포트
    * 4789 : VXLAN 오버레이 네트워크 트래픽 용 포트

* 이제 worker node에 접속하여 manager node로 join

  ```json
  $ eval $(docker-machine env test-swarm-node)
  $ docker swarm join  --token <TOKEN> <manager 노드의 내부 IP>:2377
  ```

  * 위에서 복사해두었던 join 명령을 사용한다.

* 정상적으로 구성이 되었는지 확인하기 위해 다시 manager 노드로 돌아온 후 노드 리스트 확인

  ```shell
  $ eval $(docker-machine env demo-swarm-node)
  $ docker node ls
  ```

* 아래와 같이 node 리스트가 구성되었다면 성공!

  ```
  ID                           HOSTNAME             STATUS  AVAILABILITY  MANAGER STATUS
  777i9r3g0dy9b0ozwyovhjwm1 *  test-swarm-manager   Ready   Active        Leader
  p8qqhwexf409eq51s3zxdkvi4    test-swarm-node      Ready   Active
  ```



### 진행 중인 프로젝트에서의 활용

현재 진행 중인 프로젝트에서는 위의 EC2 인스턴스 생성 명령을 통해 docker가 설치된 EC2 인스턴스를 생성 한 후 해당 인스턴스에 추가적으로 awscli를 설치하였다. 그리고 도커 레지스트리로 Amazon ECR을 사용하고 있으므로(현시점에 서울 리전에는 서비스되지 않음. 도쿄 리전으로 사용) 레지스트리에 로그인까지 해놓았다. 

여기서 AMI를 두가지 버전으로 생성을 했는데, 하나는 이렇게 **docker + awscli + ECR 로그인 까지 구성된 버전**이고, 또 하나는 이에 더해 **swarm 초기화 작업을 진행 하고 join 명령까지 발급 받은 상태**로 생성을 하였다. 

오토 스케일링 구성을 위해서 AMI의 첫번째 버전인 docker + awscli + ECR 로그인 구성에서 EC2 인스턴스가 생성될 때 **UserData를 통해 실행될 script에 manager에 join을 하기 위한 join 명령을 넣어서 구동 시 자동으로 swarm 클러스터에 join** 할 수 있도록 구성하였다. 



#### 다음 예정 포스팅

* swarm이 떠 있는 EC2 인스턴스에 트래픽을 밸런싱할 ELB 설정
* swarm을 통해 스케일 아웃을 진행
* 컨테이너에 쌓이게 될 로그들을 위한 볼륨지정
* 그 외 겪었던 여러 이슈들과 시행착오들



### 참고

* [AWS Swarm cluster](https://gist.github.com/ghoranyi/f2970d6ab2408a8a37dbe8d42af4f0a5)