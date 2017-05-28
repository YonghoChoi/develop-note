# 로깅

## log4j와 slf4j의 차이

* log4j의 경우 실제 로그를 찍지 않는 레벨을 설정하더라도 문자열 연산 작업을 수행한다.

* 이를 방지 하기 위해서는 아래와 같이 조건 체크를 직접 해줘야 함.

  ```java
  if(logger.isDebugEnabled()) {
    logger.debug("msg : " + msg);
  }
  ```

* slf4j는 facade 패턴을 사용하여 성능 개선



## slf4j

* Facade 패턴 구현
* slf4j의 API를 사용하면 구현체의 종류와 상관없이 일관된 로깅 코드를 작성할 수 있다.



## Logback

###  log4j 보다 logback을 사용해야 하는 이유

* log4j에 비해 속도나 메모리 사용면에서 개선
  * 오랫동안 검증된 log4j 아키텍처 기반으로 재작성
  * 성능은 약 10배정도 빠르게 개선
  * 메모리 점유도 적게 사용
* slf4j의 인터페이스를 직접 구현
* 설정 파일의 Dynamic Reloading 지원
* 설정 파일의 조건부 처리 기능
* 로그 파일에 대한 자동 압축, 자동 삭제 기능 제공
* 런타임에 설정한 값에 따라 로그를 분리하여 처리할 수 있는 SiftingAppender 제공
* groovy 언어로 설정 파일 작성 기능
* FileAppender 사용 시 다수의 JVM이 동시에 하나의 파일에 로그를 남길 수 있는 prudent mode를 지원
* 다양한 조건에 따른 로깅 처리 여부를 결정할 수 있는 Filter 제공
* 출저 : http://blog.cjred.net/240


* Logback은 Log4j와 Slf4j를 만든 사람이 개발.
* 오픈 소스이며 상세한 레퍼런스 매뉴얼 제공



### 주요 구성요소

* Logger : 실제 로깅을 수행하는 구성요소
  * Level 속성을 통해서 출력할 로그의 레벨 조절
* Appender : 로그 메시지가 출력할 대상을 결정하는 요소
* Encoder : Appender에 포함되어 사용자가 지정한 형식으로 표현 될 로그메시지를 변환하는 역할을 담당하는 요소
* 컴포넌트 구성
  * logback-core : 핵심 코어 컴포넌트
  * logback-classic : slf4j에서 사용 가능하도록 만든 플러그인 컴포넌트
  * logback-access : 웹 어플리케이션일 경우 HTTP 요청에 대한 강력한 디버깅 기능 제공.



### 설정파일

* XML을 이용한 설정 : logback.xml로 설정 파일 작성 후 클래스 패스에 위치.
* Groovy 언어를 이용한 설정 : Logback.groovy로 설정 파일 작성 후 클래스 패스에 위치.
* log4j.properties를 logback.xml로 자동 변환해주는 변환기
  * http://logback.qos.ch/translator



### 기능

* Dynamic Reloading 지원

  * 설정을 바꾸면 주기적으로 변경사항이 있는지 체크하여 자동으로 설정파일의 내용을 reloading 하여 변경

    ```xml
    <configuration scan="true" scanPeriod="30 seconds">
      ...
    </configuration>
    ```

    * 30초 단위로 설정파일 스캔

  * 내부 스캐닝하는 별도의 쓰레드가 존재

    * 메모리에 대한 점유율을 최소화
    * 100개의 스레드가 초당 백만 invocation을 발생해도 크게 무리를 주지 않음.

* 로그 파일에 대한 자동 압축, 자동 삭제 기능

  ```xml
  <appender name="file" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
      <fileNamePattern>someFileName.log.zip(or gz)</fileNamePattern>
      <maxHistory>90</maxHistory>
      ...
  </appender>
  ```

  * fileNamePattern : 마지막 확장자를 zip이나 gz로 지정하면 자동으로 압축
    * 작업 수행 시 어플리케이션 block을 발생시키지 않음.
  * maxHistory : 남겨놓을 로그 파일의 갯수

* I/O 작업 실패에 대한 복구 지원

  * 서버 중지 없이 이전 시점부터 복구를 graceful하게 지원

* Lilith

  * 현재 발생하는 로그 이벤트에 대한 상태 정보를 볼수 있도록 지원하는 뷰어
  * log4j의 chainsaw와 비슷한 기능
    * 차이점으로 Large data 지원

* config 파일들에 대한 분기

  * JSTL 같은 분기 스크립트(<if>, <then> and <else>)를 제공해서 하나의 파일에서 다양한 빌드 환경을 제공하도록 지원.

* Filter 기능

  * 로깅을 남길지 말지를 핸들링 할 수 있는 기능.
  * 특정 사용자에 대한 로그 레벨을 변경하여 확인 가능.

* SiftingAppender

  * Filter와 유사
  * 로그파일을 특정 주제별로 분류
    * ex ) HTTP Session 별 로그 파일 저장, 사용자별 로그 파일 저장.

* 라이브러리에 대한 스택 트레이스

  * 디버깅 하기 힘든 것 중 하나인 라이브러리에 의한 Exception을 위해 Exception 발생 시 참조했던 외부 라이브러리들을 출력.

* 참고

  * [logback을 사용해야 하는 이유](https://beyondj2ee.wordpress.com/2012/11/09/logback-%EC%82%AC%EC%9A%A9%ED%95%B4%EC%95%BC-%ED%95%98%EB%8A%94-%EC%9D%B4%EC%9C%A0-reasons-to-prefer-logback-over-log4j/)
  * [Logback을 활용한 Remote Logging](http://www.nextree.co.kr/p5584/)



## log4j2

* API Separation (API의 분리)

* Improved Performance (향상된 수행력)

* Support for multiple APIs (다양한 API들을 위한 지원)

* Automatic Reloading of Configurations (환경설정의 자동적 재호출)

* Advanced Filtering (플러그인 구조)

* Property Support (속성 지원)

* Java 6 이상 버전에서 동작

* 참고

  * http://logging.apache.org/log4j/2.x/index.html
  * http://happy2v.egloos.com/viewer/887190
  * [Log4j 개발자들이 말하는 Log4j2를 쓰길 바라는 이유](http://logging.apache.org/log4j/2.x/manual/index.html)
  * [성능 문제(비동기식 logger들을 사용하는 경우에만 성능 향상)](http://logging.apache.org/log4j/2.x/performance.html)
  * [성능 문제(비동기식 logger들을 사용하는 경우에만 성능 향상2)](http://logging.apache.org/log4j/2.x/manual/async.html#Performance)

  ​
