# IAM

* AWS 계정과 관련된 권한 제어
* 사용자 관리
  * 이 사용자는 AWS를 이용하는 사용자. 즉, 사내 개발자.
  * AWS에 서비스를 구성하면 
  * 비용이 과금되기 때문에 이를 위해 사내 직원들에게 권한을 부여하고, 히스토리를 관리하기 위함.
* CloudTrail : IAM에 등록된 User가 AWS의 서비스를 이용한 히스토리를 기록할 수 있음.
  * S3 버킷에 저장.
  * AWS 계정 단위의 API 호출 데이터 수집이 목적
* Config : AWS 계정 내에서 생성/변경/삭제된 각종 리소스 내역을 단위 시간별로 기록하고, 변경사항 및 리소스간 매핑 내역을 손쉽게 확인할 수 있도록 가시성을 제공
  * S3 버킷에 저장.
  * AWS 리소스간의 연관관계 및 히스토리 출력이 목적



# S3

* 객체 스토리지
  * 기본적으로 내부 복제를 전제로 함.
  * 하나의 단위 객체가 업로드 되면 자동적으로 내부의 여러 위치에 복제본 생성
  * S3는 동일 Region 내의 여러 AZ에 걸쳐 복제본 생성. 내구성 향상
  * 다운로드 시 여러 복제본을 사용할 수 있으므로 가용성 향상
* 객체 생성 및 삭제만 지원. 수정 안됨
  * 덮어 쓰기도 결국 삭제 후 생성
* 데이터 입출력이 빈번한 형태보다는 객체 단위로 데이터를 한번에 저장하고 이 후 다운로드가 많은 형태에 적합
* 객체란?
  * 파일(Data) 및 파일 정보(Metadata)로 구성된 저장 단위
* 버킷이란?
  * 다수의 객체를 통합하여 저장/관리/제어하는 일종의 비구니(Container)
  * 버킷의 이름은 전세계적으로 고유




# VPC

* Virtual Private Cloud
* 논리적으로 격리된 가상의 네트워크 공간을 제공하는 서비스
* ​




# Amazon Aurora



## 참고

* [최소한의 다운타임으로 아마존 RDS Aurora DB로 이전하기](https://blog.sendbird.com/ko/%ec%b5%9c%ec%86%8c%ed%95%9c%ec%9d%98-%eb%8b%a4%ec%9a%b4%ed%83%80%ec%9e%84%ec%9c%bc%eb%a1%9c-%ec%95%84%eb%a7%88%ec%a1%b4-rds-aurora-db%eb%a1%9c-%ec%9d%b4%ec%a0%84%ed%95%98%ea%b8%b0/)
* [Amazon Aurora FAQ](https://aws.amazon.com/ko/rds/aurora/faqs/)
* [MSSQL vs Aurora vs DynamoDB vs ElastiCache](http://www.omidmufeed.com/nosql-vs-microsoft-sql/)





# AWS 키워드

- 온디맨드(on demand) : 사용자의 요구사항이나 수요에 대응하는 주문형 서비스
- ​





# AWS 비용

- 데이터를 주고 받는 객체가 동일 AZ / Region / 인터넷 구간 / Edge 구간 인지에 따라 비용 산정이 다름
- EC2와 같이 AZ 단위 객체의 과금 구조가 가장 복잡
- Region 단위 객체인 S3의 경우 내부 트래픽은 무료
- ​