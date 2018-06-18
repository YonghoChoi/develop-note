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



