### Elastic Stack

* Elasticsearch 소개
* Logstash 소개
* Kibana 소개




### 설치 및 실행

* 가상머신(Virtualbox, Vmware 등)을 사용하여 Linux(Centos or Ubuntu) 구동
* 특정 버전의 ELK 다운로드 및 설치
* syslog를 활용한 ELK 구동 
  * logstash input으로 /var/log/syslog
    1. 콘솔창에 출력
    2. elasticsearch로 전달
    3. kibana로 확인




### 시스템 구조

* logstash 구조
* kibana 구조




### 데이터 수집 및 색인

* Logstash 파이프라인
  * Index doc 설정
  * Grok 패턴 적용
  * 형식지정
  * Filter기능




### 데이터 시각화 (가장 비중이 클 듯)

* Kibana 통계/지표
  * 샘플 데이터 로드
  * Discover 활용
  * Visualize
    * 어그리게이션
  * Dashboard
  * timelion
  * Management




### 플러그인

* Logstash 플러그인
  * 입력/필터/출력
* Kibana 플러그인
  * Logtrail
  * Searchguard
  * 그 외 ...




### 데모 (위에서 배운 내용 활용)

* 샘플 데이터 로드 (Kaggle 또는 자체 제작)
  * Kaggle의 데이터를 이용하는 경우 단순히 파일 다운로드 후 logstash를 사용하여 데이터 수집
  * 자체 제작하는 경우 웹 사이트를 제공하여 기능 사용 시 해당 로그를 logstash로 수집
* 수집한 데이터로 Kibana 시각화






## 계획

* 파트 구성
  1. 소개 파트
  2. Beats 파트
  3. 플러그인 파트
  4. 데모 구성 파트
  5. 설정 파트
* 최소 두명 이상의 인원이 참여
  * 1명은 1,2번 파트
    * 이론적인 부분
  * 1명은 3,4,5번 파트
    * 실습 부분





## 보완

* 어그리게이션 노하우
  * 차트별로 데이터를 어떻게 남겨야 잘 어울리는지
* kaggle
  * 대용량 데이터 예제들 많음.
* 다음주까지 목차 fix
  * 타이틀 구성
  * 서브 타이틀 구성

