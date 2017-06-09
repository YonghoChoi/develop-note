# systemd unit files

* /etc/systemd/system 디렉토리 하위에 unit file들 정의

* `unit_name.type_extension` 형식으로 파일명을 지정

* Unit 섹션에서 사용할 수 있는 옵션

  | 옵션            | 내용                                       |
  | ------------- | ---------------------------------------- |
  | Description   | Unit에 대한 설명. 이는 systemctl status 명령으로 출력됨 |
  | Documentation | Unit에 대한 문서를 참조하는 URI 목록 제공              |
  | After         | Unit이 시작되는 순서를 정의. 지정된 Unit After가 활성화 된 후에만 시작됨 |
  | Requires      | 다른 Unit에 대한 종속성을 구성. 필요한 Unit이 하나라도 활성화 되지 못하면 해당 Unit은 수행되지 않음 |
  | Wants         | Requires보다는 약한 의존성을 구성. 나열된 Unit들 중 하나라도 성공적으로 시작되면 수행됨. |
  | Conflicts     | 네거티브 의존성을 구성. Requires와 반대. 나열된 Unit 중 하나라도 활성화가 되면 수행되지 않음. |

* Service 섹션에서 사용할 수 있는 옵션

  | 옵션              | 내용                                       |
  | --------------- | ---------------------------------------- |
  | Type            | ExecStart 및 관련 옵션에 영향을 주는 Unit 프로세스 시작 유형을 구성. |
  | ExecStart       | unit이 시작 될 때 실행될 명령 또는 스크립트를 지정          |
  | ExecStop        | unit이 종료 될 때 실행될 명령 또는 스크립트를 지정          |
  | Restart         | systemctl 명령에 의한 완전 중지를 제외하고 프로세스가 종료 된 후 서비스가 다시 시작됨 |
  | RemainAfterExit | True로 설정하면 모든 프로세스가 종료 되더라도 서비스가 활성으로 간주됨. |

  * Type의 설정 값
    * simple : 기본값. 시작된 ExecStart 프로세스는 서비스의 주요 프로세스
    * forking : 시작된 ExecStart 프로세스는 서비스의 주요 프로세스가 되는 하위 프로세스를 생성. 시작이 완료되면 상위 프로세스는 종료됨
    * oneshot : simple과 유사하지만 결과 unit을 시작하기 전에 프로세스가 종료됨
    * dbus : simple과 유사하지만 주 프로세스가 D-Bus 이름을 얻은 후에만 후속 unit이 시작됨
    * notify : simple과 유사하지만 결과 unit은 sd_notify() 함수를 통해 통지 메시지가 전송 된 후에만 시작됨
    * idle : simpe과 유사하지만 서비스 바이너리의 실제 실행은 모든 작업이 완료 될때까지 지연되므로 상태 출력을 서비스의 쉘 출력과 혼합하지 않음.

* install 섹션에서 사용할 수 있는 옵션

  | 옵션              | 내용                             |
  | --------------- | ------------------------------ |
  | Alias           | unit에 대한 별칭                    |
  | RequiredBy      | unit에 의존하는 unit 리스트            |
  | WantedBy        | unit에 약하게 의존하고 있는 unit 리스트     |
  | Also            | unit과 함께 설치 또는 제거될 unit 리스트 지정 |
  | DefaultInstance | unit이 사용 가능한 기본 인스턴스를 지정       |




## Amazon Linux 인스턴스에 shutdown script 추가

- Amazon Linux는 Amazon Linux는 Fedora RHEL(Red Hat Enterprise Linux) 기반

  ```shell
  $ grep . /etc/*-release
  /etc/os-release:NAME="Amazon Linux AMI"
  /etc/os-release:VERSION="2017.03"
  /etc/os-release:ID="amzn"
  /etc/os-release:ID_LIKE="rhel fedora"
  /etc/os-release:VERSION_ID="2017.03"
  /etc/os-release:PRETTY_NAME="Amazon Linux AMI 2017.03"
  /etc/os-release:ANSI_COLOR="0;33"
  /etc/os-release:CPE_NAME="cpe:/o:amazon:linux:2017.03:ga"
  /etc/os-release:HOME_URL="http://aws.amazon.com/amazon-linux-ami/"
  /etc/system-release:Amazon Linux AMI release 2017.03
  ```

  - RHEL은 systemd init 시스템을 사용. 
    - 다른 init 시스템으로는 SysVinit 과 Upstart가 있음
    - systemd는 init 시스템 중에서 가장 복잡한 대신 뛰어난 유연성이 장점
    - 서비스의 시작 등 관련 기능을 제공하면서 소켓, 장치, 마운트포인트, 스왑영역, 다른 유닛 종류 등도 관리
  - 구버전에서는 systemctl 사용 불가능. chkconfig 명령 사용

- RHEL 기반에서의 서비스 동작

  - 서비스 상태 : chkconfig —list
  - 서비스 시작 : service <서비스명> start
  - 부트 시 자동 시작 등록 : chkconfig <서비스명> on

- 서비스를 사용하여 종료 시 script를 수행하려면 다음과 같은 절차를 수행한다.

  1. `/etc/rc.d/init.d`에 서비스로 등록할 스크립트 작성

     * 이 스크립트에 start, stop, restart 시의 동작을 정의한다.

       ```shell
       #!/bin/bash
       # chkconfig: 2345 90 90
       # description: shutdown file for filebeat

       start() {
         echo "start2!!!"
         touch /var/lock/subsys/shutdown-filebeat
         echo "test" > /home/ec2-user/start_"$(date +%y%m%d_%H%M%S)".log
       }

       stop() {
         echo "stop2!!!"
         rm -f /var/lock/subsys/shutdown-filebeat
         echo "test" > /home/ec2-user/stop_"$(date +%y%m%d_%H%M%S)".log
       }

       restart() {
         echo "restart!!"
         echo "test" > /home/ec2-user/restart_"$(date +%y%m%d_%H%M%S)".log
       }

       case "$1" in
           start)
               start
           ;;
           stop)
               stop
           ;;
           restart)
               restart
           ;;
           *)
               echo $"Usage: $0 {start|stop|restart}"
               exit 1
       esac
       ```

       ​

  2. `/etc/systemd/system` 하위에 스크립트 명과 동일한 이름의 `.service`파일 생성

     * `/etc/rc.d/init.d`의 스크립트와 한 쌍
     * `/etc/rc.d/init.d`의 스크립트, 즉 이 서비스를 관리하기 위한 설정파일이다.

  3. `.service`파일에 종료 시 실행할 스크립트를 지정

     * 왜인지는 모르겠지만 스크립트가 수행되지 않음.

  4. `chkconfig <서비스 명> on` 명령을 통해 부트 시 자동 시작되도록 설정

     * 부트 시 위에서 설정한 스크립트의 start가 호출됨

  5. 여기서 시스템 종료를 하면 시작시 스크립트는 수행되는데 종료 시 스크립트는 수행되지 않는다.

     * `/etc/rc3.d/` 하위에 종료시 stop 명령이 호출되도록 설정이 필요

     * `K<종료우선순위><서비스명>`으로 심볼릭 링크를 만들어준다.

     * 예를 들어 종료 우선순위가 90이고 서비스명이 shutdown-filebeat라면 `K90shutdown-filebeat`로 생성

       ```shell
       $ ln -s ../init.d/shutdown-filebeat K90shutdown-filebeat
       ```

     * 그리고 `/etc/rc.d/init.d`의 스크립트에서 start 구문에 `/var/lock/subsys/<서비스명>`으로 lock 파일을 생성해야 종료 시 스크립트를 수행할때까지 시스템이 종료되지 않고 락킹된다.

  6. 이제 시스템을 종료하게 되면 `/etc/systemd/system/<서비스명>.service`에 지정된 스크립트가 수행됨



### chkconfig 등록

- chkconfig 명령을 통해 서비스를 추가하려면 `/etc/rc.d/init.d` 디렉토리에 스크립트가 존재해야 한다.

  - 각 서비스마다 이 디렉토리에 스크립트를 하나씩 가지고 있다.
  - 서비스 중지용, 시작용 스크립트가 따로 있는 것이 아니라 init 데몬이 자신에게 어떤 파라미터를 전달하느냐에 따라 서비스를 중지하기도 하고 시작하기도 한다.
  - `/etc/rc.d/init.d` 디렉토리의 스크립트들은 서버의 특정 서비스를 시작하고 중지하기 위한 모든 것을 관장한다.

- 해당 스크립트는 chkconfig를 위한 설정이 포함되어 있어야 한다.

  - httpd의 예

    ```shell
    # chkconfig: 2345 90 90
    # description: init file for Apache server daemon
    # processname: /usr/local/server/apache/bin/apachectl
    # config: /usr/local/server/apache/conf/httpd.conf
    # pidfile: /usr/local/server/apache/logs/httpd.pid
    ```

    - chkconfig의 앞 2345는 부팅레벨을 의미
    - 다음에 오는 90은 시작 우선 순위
    - 그 다음 90은 종료 우선 순위
    - 우선순위 값은 100이하의 수로, 숫자가 작을 수록 우선순위가 높다.

- 아래 명령으로 chkconfig 등록

  ```shell
  $ chkconfig --add <파일명>
  ```

  ​

### chkconfig on

* script를 추하기 위해 systems unit file 사용

* systemd의 주요 유닛으로는 service 유닛과 target 유닛이 있다.

  * service 유닛 : 리눅스 서버의 데몬을 관리하는데 사용
    * 파일명이 `.service`로 끝난다.
  * target 유닛 : 단순하게 다른 유닛을 일컫는 말
    * 파일명이 `.target`으로 끝난다.

* 리눅스 시스템의 유닛 설정 파일은 /lib/systemd/system 디렉토리와 /etc/systemd/system 디렉토리에 존재

* `/etc/systemd/system`의 하위에 `.service` 확장자를 가진 파일에 아래 내용 작성

  ```shell
  [Unit]
  Description=Run Scripts at Shutdown

  [Service]
  Type=oneshot
  RemainAfterExit=true
  ExecStart=/bin/true
  ExecStop=<스크립트 파일 경로>

  [Install]
  WantedBy=multi-user.target
  ```

* chkconfig 명령을 통해 활성화

  ```shell
  $ chkconfig <서비스명> on
  ```




## 참고

* https://access.redhat.com/documentation/en-US/Red_Hat_Enterprise_Linux/7/html/System_Administrators_Guide/sect-Managing_Services_with_systemd-Unit_Files.html