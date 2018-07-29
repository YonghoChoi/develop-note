# Why Terraform?

## 발표자

* 신근우
* cypher



## CloudFormation과 비교

* AWS 인프라를 프로비저닝하기 위한 서비스
* 여러개의 인스턴스를 구동시키기 위한 스크립트가 굉장히 복잡.
  * 유지보수도 어려움
  * 재사용 어려움
  * 템플릿이 S3에 올라가야함 (로컬 테스트 시에도)
* 템플릿이 커질 수록 가독성이 떨어짐
* 모듈화 어려움



## Terraform

- building, changing, versioning 도구
- HCL 사용
- 다양한 Provider 지원
- Built-in function이 강력함



### 장점

* 다른 사용자가 만든 모듈을 가져다 사용할 수 있음
* Private Registry는 테라폼 엔터프라이즈를 사용하거나 Registry API를 통해 직접 구성도 가능
* 테라폼에서 State가 핵심기능이라 생각
  * Remote backed를 사용할 수도 있음
  * import 기능으로 다른 인프라 구성을 참조해서 사용 가능
  * State를 Resource로 사용 가능
  * Region/Provider에 상관 없이 State의 ouput을 참조해서 사용 가능
    * 이로 인해 멀티 클라우드간의 연계 구성이 가능
* 심플하고 강력함
* 자사 제품들과 연계가 좋음



### 단점

* AWS만을 사용한다면 CloudFormation에 비해 지원이 완벽하진 않음
* 느린 속도로 인해 모듈화가 반 강제됨
* HCL이 익숙하지 않음
* 사소한 버그들이 있음



## 그외

* 모듈화와 디렉토리 구조를 어떻게 잡을 것인가에 가장 신경써야 함