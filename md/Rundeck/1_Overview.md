# 필수 개념

* 역할 기반 액세스 제어 정책 (Role-based Access Control Policies) : 사용자 또는 사용자 그룹에 프로젝트, 잡, 노드, 커맨드, API와 같은 리소스들에 대한 권한을 부여
  * 액세스 제어 정책 가이드 : http://rundeck.org/docs/administration/access-control-policy.html
* 프로젝트 (Projects) : 모든 런덱의 작업은 프로젝트 컨텍스트 내에서 발생한다. Rundeck 서버에서 다수의 프로젝트를 관리 할 수 있다.
  * 프로젝트 가이드 : http://rundeck.org/docs/administration/project-setup.html#project-readme.md-and-motd.md
* 잡 (Job) : 잡은 순차적으로 실행되는 각 스탭, 작업 옵션, 노드를 캡슐화한다.
  * 잡 가이드 : http://rundeck.org/docs/manual/jobs.html
* 노드 (Nodes) : 네트워크 엑세스가 가능한 실제 호스트나 가상 인스턴스를 의미한다. Rundeck에서 resource model이라 불리는 것은 프로젝트에 속한 노드들을 의미한다.
  * 노드 가이드 : http://rundeck.org/docs/manual/nodes.html
* 명령 (Commands) : 노드에서 실행되는 한줄의 커맨드 문자열이다. Rundeck은 커맨드 문자열을 평가하고 대상 노드에서 명령을 실행한다.
  * 명령 가이드 : http://rundeck.org/docs/manual/commands.html
* 실행 (Executions) : 실행 중이거나 완료된 명령 또는 잡의 활동을 나타낸다. 실행 데이터를 통해 잡이나 명령의 진행 상황을 모니터링하고 실행 내역을 리포팅한다. 
  * 실행 가이드 : http://rundeck.org/docs/manual/executions.html
* 플러그인 (Plugins) : 플러그인은 노드에서 명령을 실행하고, 잡의 각 단계를 수행하고, 작업 상태에 대한 알림을 보내고, 네트워크 호스트에 대한 정보를 수집하고, 원격 서버에 파일을 복사하고, 로그를 저장 및 스트리밍하거나, 사용자 디렉토리와 상호작용하는데 사용된다.
  * 플러그인 가이드 : http://rundeck.org/docs/plugins-user-guide/index.html



## 디렉토리 구조

* /etc/rundeck : Rundeck 서버의 설정 파일들
  * 인증
  * 프레임워크
  * 서버 설정 등
* /var/rundeck : Rundeck 프로젝트와 관련된 설정 파일들
  * 프로젝트 자체의 설정 파일
  * 노드 설정 파일
  * 액세스 정책 등
* /var/lib/rundeck : rundeck 웹 페이지를 구성하기 위한 파일들
  * 정적 파일
  * rundeck 웹을 통해 생성되는 각종 파일들 (job, command, log 등)



## Rundeck 동작

* 가장 큰 단위가 되는 프로젝트를 생성
* 프로젝트 내에는 노드, 잡, 커맨드를 생성할 수 있는데, 원격 호스트가 노드에 해당하고 이 노드에서 실행할 명령이 잡과 커맨드로 구분된다.
  * 이 때 노드는 GUI로 추가가 불가능하고 설정 파일을 수정해야한다.
* 잡과 커맨드의 차이는 커맨드는 단일 실행 명령을 의미하고, 잡은 이러한 커맨드를 포함해서 다양한 유형의 동작을 스텝으로 구분해서 실행할 수 있다. 즉, 워크플로 설정이 가능하다.
* 실행 순서를 정리해보면 먼저 프로젝트를 생성하고 노드를 추가한 후 노드에서 실행할 잡이나 커맨드를 생성한다.



## 인증

* Rundeck의 사용자 계정 관리는 `/etc/rundeck/realm.properties`에 정의

* 사용자 계정 입력 형식

  ```
  admin:admin,user,admin
  ```

  * `<username>: <password>[,<rolename> ...]`
  * 위의 경우 사용자 계정은 admin이고 패스워드는 admin, 계정에 대한 role은 user와 admin이다.

* 패스워드를 암호화해서 입력하려면 `jetty-all-9.0.7.v20131107.jar` 를 실행해서 암호화된 문자열 생성

  ```
  $ java -cp /var/lib/rundeck/bootstrap/jetty-all-9.0.7.v20131107.jar org.eclipse.jetty.util.security.Password <username> <password>
  ```

  ```
  OBF:1xfd1zt11uha1ugg1zsp1xfp
  MD5:a029d0df84eb5549c641e04a9ef389e5
  CRYPT:jsnDAc2Xk4W4o
  ```

* 위에서 생성한 암호 문자열을 사용하여 계정에 패스워드 지정

  ```
  <username>: MD5:a029d0df84eb5549c641e04a9ef389e5,user,admin
  ```

  