# Spinnaker on Kubernetes

* 해외에서 스피너커로 많이 발음
* Spinnaker
  * 넷플리에서 시작
  * 구글과 함께 오픈소스로 공개 후 관리
* 클라우드에서 Spinnaker
  * Inventory + Pipelines



## 핵심 기능

* Cluster
  * 쿠버네티스의 클러스터가 아닌 어플리케이션의 집합
* Deployment
  * 배포
* 멀티 클라우드
  * 대부분의 Public Cloud, Private Cloud 지원
    * 오픈스택은 V3 API 지원
  * 테라폼도 지원 예정
* 알림 기능
* 승인 기능
  * 관리자 판단 하에 수동으로 배포 진행할 수 있는 단계
* 딜리버리 영역의 대부분의 작업을 스피너커가 대신 해줌
* Chaos Monkey 오픈소스가 내장되어 있어서 카오스엔지니어링 지원

* 배포 전략
  * 블루/그린
  * 카나리
  * 롤링



## 커뮤니티

* Community.spinnaker.io가 활발



## 주요 마이크로서비스

* Clouddriver : 멀티 클라우드 관련
* Kayenta : 카나리 배포 분석 관련
  * 카나리 배포 시 배포 내용은 분석하는 것을 Spinnaker에서 제공
* Orca : 오케스트레이션 관련



## CLI 도구

* Halyard
  * CLI 도구
  * kubectl과 같이 Spinnaker를 관리하기 위한 도구



## Spinnaker on kubernetes

* Legacy(V1) 버전과 Maifest(V2) 버전이 지원됨
  * V1에서는 Spinnaker와 Kubernetes의 중복 용어들이 존재하여 혼란이 있었음
  * V2에서 개선
    * 파일로 형상 관리
    * 외부 저장소를 사용하여 관리 가능
    * 대부분 V2를 사용



## 젠킨스와 비교

* 젠킨스는 CI 도구
* Spinnaker는 클라우드 API를 직접 호출하기 떄문에 스크립팅 요소가 적음
* 빌드가 필요한 경우 Spinnaker에서 젠킨스를 호출해서 사용할 수 있음



##  Native 쿠버네티스와 비교

* 다양한 배포 전략
  * 승인 기능
  * 인프라 관리
  * 빠른 롤백 등
* Spinnaker를 관리해야하는 관리비용이 있음



##  파이프라인

* Stage : 워크플로우 단위
* 코드 커밋 -> docker hub에서 트리거 -> Spinnaker 웹 훅 -> 스테이징에 배포 -> QA 담당자가 테스트 후 승인 -> 라이브 배포
* 모니터링은 프로메테우스로



## 정리

* 단일 소스를 대상으로 신뢰성있는 배포를 위한 좋은 전략
* 실행에 대한 감사 기능
* 코드와 컨테이너 이미지 검증
* 베스트 전략 : Spinnaker + Jenkins + Packer + Helm + Terraform
* Redis에 성능저하가 발생하면 Spinnaker 전반적인 성능에 영향
  * 튜닝 포인트
* 모니터링에는 Datadog, Prometheus, Stackdriver
* 노드 로깅은 fluentd, ElasticStack
* 개발팀은 골든 이미지 생성, 운영팀이 Spinnaker를 통한 배포면 좋지 않을까