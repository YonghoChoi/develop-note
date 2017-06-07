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





## 참고

* https://access.redhat.com/documentation/en-US/Red_Hat_Enterprise_Linux/7/html/System_Administrators_Guide/sect-Managing_Services_with_systemd-Unit_Files.html