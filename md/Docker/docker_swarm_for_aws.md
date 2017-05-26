# docker swarm for aws

### awscli 설정

* `~/.aws/credentials`에서 ACCESS_KEY와 SECRET_KEY 설정
* `~/.aws/config`에서 default region 설정

### ec2 인스턴스 생성

* shell 명령 시 사용할 변수 설정

  ```shell
  $ VPC=vpc-25eba94c
  $ REGION=ap-northeast-2
  $ SUBNET=subnet-f391c39a
  $ ZONE=a
  $ SECURITY_GROUP_NAME=swarmTest
  ```

* manager 인스턴스 생성

  ```shell
  $ docker-machine create -d amazonec2 --amazonec2-vpc-id $VPC --amazonec2-region $REGION --amazonec2-zone $ZONE --amazonec2-instance-type t2.micro --amazonec2-subnet-id $SUBNET --amazonec2-security-group $SECURITY_GROUP_NAME test-swarm-manager
  ```

  * docker-machine create 명령을 통해 [드라이버를 amazonec2를 선택하여 EC2 인스턴스를 생성](https://docs.docker.com/machine/drivers/aws/)하는 경우 뒤에 오는 설정들을 적용한 EC2 인스턴스가 만들어지고 기본적으로 docker까지 설치가 된다.

  * 위 명령으로 ec2 instance 를 생성하면 각각의 인스턴스마다 ssh 키가 발급된다. 

    * ~/.docker/machine/machines/\<ec2인스턴스명\> 위치에 키 생성

  * 같은 키를 통해 인스턴스를 생성하려면 위 명령에서 아래 옵션 추가

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
  $ docker-machine create -d amazonec2 --amazonec2-vpc-id $VPC --amazonec2-region $REGION --amazonec2-zone $ZONE --amazonec2-instance-type t2.micro --amazonec2-subnet-id $SUBNET --amazonec2-security-group $SECURITY_GROUP_NAME test-swarm-node
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
  * 이 swarm join 명령을 잘 복사해둔다.

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
  $ aws ec2 authorize-security-group-ingress --group-id $SECURITY_GROUP_ID --protocol tcp --port 2377 --source-group $SECURITY_GROUP_ID
  $ aws ec2 authorize-security-group-ingress --group-id $SECURITY_GROUP_ID --protocol tcp --port 7946 --source-group $SECURITY_GROUP_ID
  $ aws ec2 authorize-security-group-ingress --group-id $SECURITY_GROUP_ID --protocol udp --port 7946 --source-group $SECURITY_GROUP_ID
  $ aws ec2 authorize-security-group-ingress --group-id $SECURITY_GROUP_ID --protocol tcp --port 4789 --source-group $SECURITY_GROUP_ID
  $ aws ec2 authorize-security-group-ingress --group-id $SECURITY_GROUP_ID --protocol udp --port 4789 --source-group $SECURITY_GROUP_ID
  ```

  * authorize-security-group-ingress 명령을 사용하면 보안 그룹에 수신 규칙을 추가한다.
  * 뒤에 `—source-group`을 지정하는 이유는 접근 권한을 동일한 보안 그룹에 속한 인스턴스들만 허용하도록 하기 위함.

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