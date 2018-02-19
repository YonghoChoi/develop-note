# Ansible

## Test 시나리오

* Vagrant를 이용하여 Mock 테스트 환경 구성
* 테스트 환경이 linux/mac/windows 등 다양한 환경이었기 때문에 Vagrant 사용



## Ansible이란?

* ssh 기반
  * puppet과 chef는 agent 기반
* json으로 통신
* Ad-hoc 지원
* 병렬 배포 지원
* 멱등성
  * 여러번 적용해도 결과는 바뀌지 않는다.
  * 바뀌는 것이 없으면 배포되어도 바뀌지 않음.
  * 바뀌는 부분만 반영
  * 멱등성을 제공하지 않는 경우 존재. 신경써서 개발해야함.
    * shell, command, file module
* 할 수 있는 일
  * 설치 : apt-get ...
  * 환경 설정 파일 및 스크립트 배포 : copy, ...
  * 다운로드 : subversion, git, ...
  * 실행 : shell ...
* YAML, Jinja2 사용
* role : structure 기본 단위로서 설치, 사용이 가능
* local 동작 
  * --connection=local 옵션 지정



## 참고

* http://docs.ansible.com​