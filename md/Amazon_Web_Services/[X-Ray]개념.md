# X-Ray

## 용어

![](images/xray_1.png)

* Trace : 클라이언트부터 request 시작부터 서버의 동작 전체적인 flow
* Segments : 각각의 서비스 별로 진행되는 과정들
* Sub-segments : 서비스 내의 데이터 처리



## Java에서의 사용

* [AWSXRayServletFilter를 서블릿 필터로 추가](http://docs.aws.amazon.com/ko_kr/xray/latest/devguide/xray-sdk-java-filters.html)

* [디펜던시](http://docs.aws.amazon.com/ko_kr/xray/latest/devguide/xray-sdk-java.html#xray-sdk-java-dependencies)

* SQL 쿼리를 계측하려면 [SDK의 인터셉터를 데이터 원본에 추가](http://docs.aws.amazon.com/ko_kr/xray/latest/devguide/xray-sdk-java-sqlclients.html)

* Elastic Beanstalk를 사용하는 경우 [Beanstalk의 기본 인스턴스 프로파일](http://docs.aws.amazon.com/ko_kr/elasticbeanstalk/latest/dg/concepts-roles.html#concepts-roles-instance)에 Xray에 대한 권한이 포함되어 있음.

* 로컬에서 실행하는 경우 awscli를 통해 credentials 설정이 되어 있어야함.

  ```
  [default]
  aws_access_key_id=AKIAIOSFODNN7EXAMPLE
  aws_secret_access_key=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
  ```

  ​



## 참고

* http://docs.aws.amazon.com/ko_kr/xray/latest/devguide/xray-sdk-java.html