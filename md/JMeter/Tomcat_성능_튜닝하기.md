### Tomcat 성능 튜닝하기

[http://www.techbrainwave.com/?p=836](http://www.techbrainwave.com/?p=836)
성능이 떨어지는(느리고 답답한) 애플리케이션을 좋아하는 사람은 없음. Tomcat은 성능이 점점 좋아지므로 항상 최신 버전을 사용할 것.
**1. JVM heap 메모리 크기 변경**
\- $CATALINA_HOME/bin/catalina.sh 파일에 아래 내용을 추가한다.
`JAVA_OPTS="-Djava.awt.headless=true -Dfile.encoding=UTF-8 -server -Xms1024m -Xmx1024m -XX:NewSize=512m -XX:MaxNewSize=512m -XX:PermSize=512m -XX:MaxPermSize=512m -XX:+DisableExplicitGC"`(*) JVM의 기본 heap 메모리 사이즈는 64MB 이므로 최소한 -server, -Xms, -Xmx 옵션은 설정해야 한다.
(*) JVM 옵션 목록: [http://blogs.sun.com/watt/resource/jvm-options-list.html](http://blogs.sun.com/watt/resource/jvm-options-list.html)
(*) [Blog2Book, 자바 성능을 결정짓는 코딩 습관과 튜닝 이야기](http://www.hanb.co.kr/book/look.html?isbn=978-89-7914-522-9) 추천함.
**2. JRE 메모리 누수 처리**
\- Tomcat 6.0.26부터 메모리 누수를 탐지하는 기능이 추가되었다.
\- $CATALINA_HOME/conf/server.xml 파일에서 아래 코드를 찾아 주석을 해제한다.
`<Listener className="org.apache.catalina.core.JreMemoryLeakPreventionListener" />`
**3. 스레드 풀(thread pool) 설정**
\- 클라이언트의 요청을 처리하는 스레드 수 설정. 기본 값은 200이며 그 이상의 요청이 있을 경우 "connection refused" 메시지를 리턴한다.
\- $CATALINA_HOME/conf/server.xml 파일에서 <Connector />의 속성을 수정한다.
`<Connector port="8080" address="localhost" maxThreads="250" maxHttpHeaderSize="8192" emptySessionPath="true" protocol="HTTP/1.1" enableLookups="false" redirectPort="8181" acceptCount="100" connectionTimeout="20000" disableUploadTimeout="true" />`- 만약 maxThreads 값이 최대 값인 750을 넘을 경우 두 대의 Tomcat을 이용해 클러스터링 구성을 하는 것이 좋다. 하나의 Tomcat에 maxThreads=1000을 설정하지 말고 두 개의 Tomcat에 각각 maxThreads=500 설정을 하라는 뜻이다.
(*) 잘 모르겠으면 대충 설정하지 말고 Tomcat을 기본값으로 운영하면서 숫자를 조금씩 조정해야 함.
**4. 압축**
\- 특정 mime-type에 대해 데이터를 압축하여 전송할 수 있다.
`<Connector port="8080" protocol="HTTP/1.1" connectionTimeout="20000" redirectPort="8181" compression="500" compressableMimeType="text/html,text/xml,text/plain,application/octet-stream" />`- compression="500"은 500 bytes 이상의 파일만 압축하라는 뜻이다.
\- 닥치고 압축하려면 compression="on"으로 설정한다.
(*) [HTTP compression](http://en.wikipedia.org/wiki/HTTP_compression): 서버에서 파일을 압축한 뒤 클라이언트로 전송하는 기능임. Tomcat의 고유한 기능이 아니라 HTTP 스펙에 있는 기능임.
**5. 데이터베이스 성능 튜닝**
\- NamedQuery를 쓴다면 애플리케이션 초기화할 때 모두 로드할 것.
(*) JDBC를 사용한다면 try...catch...finally를 사용하여 반드시 DB connection을 close 할 것.
**6. Tomcat Native Library**
\- Apache Portable Runtime(APR)이라는 게 있는데 이게 성능 향상이 많은 도움을 줌.
\- 설치방법: [http://www.techbrainwave.com/?p=1017](http://www.techbrainwave.com/?p=1017)(영어)
(*) 설치방법: [Tomcat Native Library – Apache Portable Runtime(APR) 설치](http://maxnim.blogspot.com/2011/02/tomcat-native-library-apache-portable.html)(한글)
(*) 굳이 Apache httpd와 연동할 필요가 없는 이유임.
(*) [진실 혹은 거짓: Tomcat과 Apache httpd를 연동하는게 항상 성능을 향상시키는가?](http://maxnim.blogspot.com/2011/01/tomcat-apache-httpd.html)
**7. 기타**
\- 웹 브라우저 캐시 사용함으로 설정할 것.
\- 서버 재시작시 반드시 자동으로 Tomcat이 재시작 되도록 설정할 것.
\- HTTPS가 HTTP에 비해 약간 느리긴 하지만 보안이 중요하다면 HTTPS를 사용할 것.

출처 : [http://maxnim.blogspot.com/2011/02/tomcat.html](http://maxnim.blogspot.com/2011/02/tomcat.html)

기본적으로 Tomcat 또는 Java 실행시 메모리가 상당히 중요한데요.

 

어떠한 목적을 위해 사용하느냐에 따라서 Tomcat (Java) 의 메모리 설정이 중요합니다.

 

저 역시.. 프로젝트를 위해 테스트 하던도중 Tomcat 메모리 설정을 이리저리 변경해보고

추후에 참고하고자 이 글을 작성하게 되었네요. 참 .. CentOS 기준입니다.

 

우선 Tomcat 의 경우 실행할때에 옵션을 줄수있습니다.

다만 이 옵션이 Tomcat 을 실행할때 다른 프로세스와 다르게 CATALINA_OPTS 라는 변수로 추가를 해줘야 합니다.

방법은 정해져 있지 않습니다. Tomcat 설치 디렉토리의 bin 폴더 밑에 catalina.sh 에

추가해주거나 접속한 계정의 홈 디렉토리에 있는 .bash_profile 이나 /etc/profile 에 추가해줘도 전혀 문제 없습니다.

(Tomcat 실행시 CATALINA_OPTS 라는 변수를 참고해서 실행하기에 그렇습니다.)

 

우선 Tomcat 의 CATALINA 옵션에 대해 알아보겠습니다.

 

- **server** : Server HotSpot JVM을 사용하는 옵션입니다. Server HotSpot JVM은 Desktop용 Appkication을 구동하는데 유리하고,

​                최적화(Optimization)에 필요한 모든 과정을 최대한으로 수행합니다. Application의 시작시간은 느리지만, 일정 시간이 흐르면 Client HotSpot JVM에

​              비해 훨씬 뛰어난 성능을 보장합니다.
   **※** Jdk 1.5부터는 Server-Class머신인 경우에는 -server 옵션이 기본값이며, Server-Class머신이란 2장 이상의 CPU와 2G이상의 메모리를 갖춘 머신을 의미합니다.
**- Xms<size>**

 : Java Heap의 최소 크기값을 지정하는 부분입니다. Java Heap Size는 -Xms 옵션으로 지정한 크기로 시작하며 최대 -Xmx옵션으로 지정한 크기만큼 증가합니다.
**- Xmx<size>**

 : Java Heap의 최대 크기값을 지정하는 부분입니다. -Xms 옵션으로 지정한 크기로 시작하며 최대 -Xmx옵션으로 지정한 크기만큼 증가합니다. 
   **※** Sun HotSpt JVM 계열에서는 최초 크기와 최대 크기를 동일하게 부여할 것을 권장하고 있으며, 크기의 동적인 변경에 의한 오버 헤들를 최소화하기 위해서입니다.
**- XX:NewSize=<Value>**

 : New Generation의 시작 크기를 지정값 입니다.
**- XX:MaxNewSize=<value>**

 : New Generation의 최대 크기를 지정값 입니다.
   **※** New Generation의 크기는 NewSize옵션값과 MaxNewSize옵션값에 의해 결정됩니다
**- XX:PermSize=<size>**

 : Permanent Generation의 최초 크기를 지정하는 값입니다. 
**- XX:MaxPermSize=<size>**

 : Permanent Generation의 최대 크기를 지정하는 값입니다.
  **※** 많은 수의 Class를 사용하는 Application들은 Permanent Generation의 크기가 작을 경우 Out of Memory Error가 발생하며 Class를 로딩하지 못하거나

​      사용중 다운되는 경우 때문에 초기 Permanent Generation의 값을 메모리에 여유가 있다면 넉넉하게 주는 것이 좋습니다.
**- XX:NewRatio=<value>**

 : New Generation과 Old Generation의 비율을 결정합니다.
  **※** 자세하게 안내되어 있는 블로그 링크를 남김니다.  [http:⁄⁄helloworld.naver.com⁄helloworld⁄184615](http://mindpower.kr/admin/entry/post/%E2%81%84%E2%81%84helloworld.naver.com%E2%81%84helloworld%E2%81%84184615)
**- XX:SurvivorRatio=<value>**

 : Survivor Space와 Eden Space의 비율을 지정하는 값입니다. 만일 이 값이 6이면, To Survivor Ratio:From Survivor Ratio:Eden Space = 1:1:6 이 됩니다.
   즉, 하나의 Survivor Space의 크기가 New Generation의 1⁄8 이 된다. Survivor Space의 크기가 크면 Tenured Generation으로 옮겨가기 전의 중간 버퍼 영역이

   커지는 게 됩니다. 따라서 Full GC의 빈도를 줄이는 역할을 할 수 있는 반면 Eden Space의 크기가 줄어들므로 Mirror GC가 자주 발생하게 될 가능성이

   있습니다.
**- XX:ReservedCodeCacheSize=<value>**

 : Code Cache의 최대 사이즈의 크기(byte) 설정값 입니다.
**- XX:+DisableExplicitGC**

 : System.gc 호출에 의한 Explicit GC를 비활성화하며, RMI에 의한 Explicit GC나 Applicaton에서의 Explicit GC를 원천적으로 방지하고자 할 경우에 사용됩니다.
**- XX:+UseConcMarkSweepGC**

 : CMS Collector를 사용할 지의 여부를 지정하는 옵션이며, GC Pause에 의한 사용자 응답 시간 저하 현상을 줄이고자 할 경우에 사용이 권장됩니다.
**- XX:+AggressiveOpts**

 : 최신 HotSpot VM 성능을 최적화하는 옵션입니다.
**- Djava.net.preferIPv4Stack**

 : IPv4인식하기 위해 사용합니다.
**- Djava.awt.headless**

 : 비윈도우 환경에서 GUI 클래스를 사용할 수 있게 하는 옵션입니다.

 

 

 

해서.. 저의 경우 /etc/profile 에 JAVA_HOME 과 같이 CATALINA_OPTS 변수를 추가해서 아래의 처럼 사용 중입니다.

 

 

[?](http://soul.tistory.com/63#)

 

이상입니다.

 

출처 - http://soul.tistory.com/63

## [Apache Tomcat Tuning (아파치 톰캣 튜닝 가이드)](http://bcho.tistory.com/788)

이번에는 톰캣 서버에 대한 튜닝 옵션에 대해서 한번 알아보자.

애플리케이션 관점에서의 튜닝도 중요하지만, 각 솔루션에 대한 특성을 업무 시나리오에 맞춰서 튜닝하는 것도 못지 않게 중요하다. 여기서 톰캣 튜닝을 설명하는 것은 톰캣 자체에 대한 튜닝 옵션을 소개하는 것도 목적이 있지만, 그보다 업무형태에 따라서 어떠한 접근을 해서 톰캣을 튜닝하는지를 소개하기 위함이다.

 

가정

여기서 튜닝 하는 톰캣은 HTTP/JSON형태의 REST 형태로 서비스를 제공하는 API 서버의 형태이다. 여러대의 톰캣을 이용하여 REST 서비스를 제공하며, 앞단에는 L4 스위치를 둬서 부하를 분산하며, 서비스는 stateless 서비스로 공유되는 상태 정보가 없다. 

## server.xml 튜닝

톰캣의 대부분 튜닝 패러미터는 ${Tomcat_HOME}/conf/server.xml 파일에 정의된다.

몇몇 parameter를 살펴보도록 하자.

 

### Listener 설정

 *<Listener className="org.apache.catalina.security.SecurityListener" checkedOsUsers="root" /> *

이 옵션은 tomcat이 기동할 때, root 사용자이면 기동을 하지 못하게 하는 옵션이다. 서버를 운영해본 사람이라면 종종 겪었을 실수중의 하나가 application server를 root 권한으로 띄웠다가 다음번에 다시 실행하려고 하면 permission 에러가 나는 시나리오 이다. root 권한으로 서버가 실행되었기 때문에, 각종config 파일이나 log 파일들의 permission이 모두 root로 바뀌어 버리기 때문에, 일반 계정으로 다시 재 기동하려고 시도하면, config 파일이나 log file들의permission 이 바뀌어서 파일을 읽어나 쓰는데 실패하게 되고 결국 서버 기동이 불가능한 경우가 있다. 이 옵션은 이러한 실수를 막아 줄 수 있다.

 

### Connector 설정

** **

**protocol="org.apache.coyote.http11.Http11Protocol"******

먼저 protocol setting인데, Tomcat은 네트워크 통신하는 부분에 대해서 3가지 정도의 옵션을 제공한다. BIO,NIO,APR 3 가지이다. NIO는 Java의 NIO 라이브러리를 사용하는 모듈이고, APR은 Apache Web Server의 io module을 사용한다. 그래서 C라이브러리를 JNI 인터페이스를 통해서 로딩해서 사용하는데, 속도는 APR이 가장 빠른것으로 알려져 있지만, JNI를 사용하는 특성상, JNI 코드 쪽에서 문제가 생기면, 자바 프로세스 자체가 core dump를 내면서 죽어 버리기 때문에 안정성 측면에서는 BIO나 NIO보다 낮다. BIO는 일반적인 Java Socket IO 모듈을 사용하는데, 이론적으로 보면 APR > NIO > BIO 순으로 성능이 빠르지만,실제 테스트 해보면 OS 설정이나 자바 버전에 따라서 차이가 있다. Default는 BIO이다.

 

**acceptCount="10"**

이 옵션은 request Queue의 길이를 정의한다. HTTP request가 들어왔을때, idle thread가 없으면 queue에서 idle thread가 생길때 까지 요청을 대기하는queue의 길이이다. 보통 queue에 메세지가 쌓였다는 것은 해당 톰캣 인스턴스에 처리할 수 있는 쓰레드가 없다는 이야기이고, 모든 쓰레드를 사용해도 요청을 처리를 못한다는 것은 이미 장애 상태일 가능성이 높다.

그래서 큐의 길이를 길게 주는 것 보다는, 짧게 줘서, 요청을 처리할 수 없는 상황이면 빨리 에러 코드를 클라이언트에게 보내서 에러처리를 하도록 하는 것이 좋다. Queue의 길이가 길면, 대기 하는 시간이 길어지기 때문에 장애 상황에서도 계속 응답을 대기를 하다가 다른 장애로 전파 되는 경우가 있다.

순간적인 과부하 상황에 대비 하기 위해서 큐의 길이를 0 보다는 10내외 정도로 짧게 주는 것이 좋다.

 

**enableLookups="false"**

톰캣에서 실행되는 Servlet/JSP 코드 중에서 들어오는 http request에 대한 ip를 조회 하는 명령등이 있을 경우, 톰캣은 yahoo.com과 같은 DNS 이름을 IP주소로 바뀌기 위해서 DNS 서버에 look up 요청을 보낸다. 이것이 http request 처리 중에 일어나는데, 다른 서버로 DNS 쿼리를 보낸다는 소리이다. 그만큼의 서버간의 round trip 시간이 발생하는데, 이 옵션을 false로 해놓으면 dns lookup 없이 그냥 dns 명을 리턴하기 때문에, round trip 발생을 막을 수 있다.

 

**compression="off"**

HTTP message body를 gzip 형태로 압축해서 리턴한다. 업무 시나리오가 이미지나 파일을 response 하는 경우에는  compression을 적용함으로써 네트워크 대역폭을 절약하는 효과가 있겠지만, 이 업무 시스템의 가정은, JSON 기반의 REST 통신이기 때문에, 굳이 compression을 사용할 필요가 없으며, compression에 사용되는 CPU를 차라리 비지니스 로직 처리에 사용하는 것이 더 효율적이다.

 

**maxConnection="8192"**

하나의 톰캣인스턴스가 유지할 수 있는 Connection의 수를 정의 한다.

이 때 주의해야 할 점은 이 수는 현재 연결되어 있는 실제 Connection의 수가 아니라 현재 사용중인 socket fd (file descriptor)의 수 이다. 무슨 말인가 하면 TCP Connection은 특성상 Connection 이 끊난 후에도 바로 socket이 close 되는 것이 아니라 FIN 신호를 보내고, TIME_WAIT 시간을 거쳐서 connection을 정리한다. 실제 톰캣 인스턴스가 100개의 Connection 으로 부터 들어오는 요청을 동시 처리할 수 있다하더라도, 요청을 처리하고 socket이 close 되면TIME_WAIT에 머물러 있는 Connection 수가 많기 때문에, 단시간내에 많은 요청을 처리하게 되면 이 TIME_WAIT가 사용하는 fd 수 때문에, maxConnection이 모자를 수 있다. 그래서 maxConnection은 넉넉하게 주는 것이 좋다.

이외에도 HTTP 1.1 Keep Alive를 사용하게 되면 요청을 처리 하지 않는 Connection도 계속 유지 되기 때문에, 요청 처리 수 보다, 실제 연결되어 있는Connection 수가 높게 된다.

그리고, process당 열 수 있는 fd수는 ulimit -f 를 통해서 설정이 된다. maxConnection을 8192로 주더라도, ulimit -f에서 fd 수를 적게 해놓으면 소용이 없기 때문에 반드시 ulimit -f 로 최대 물리 Connection 수를 설정해놔야 한다.

 

**maxKeepAliveRequest="1"**

HTTP 1.1 Keep Alive Connection을 사용할 때, 최대 유지할 Connection 수를 결정하는 옵션이다. 본 시나리오에서는 REST 방식으로 Connectionless 형태로 서비스를 진행할 예정이기 때문에, Kepp Alive를 사용하지 않기 위해서 값을 1로 준다.

만약에 KeepAlive를 사용할 예정이면, maxConnection과 같이 ulimit에서 fd수를 충분히 지정해줘야 하낟.

 

**maxThread="100"**

사실상 이 옵션이 가장 중요한 옵션이 아닌가 싶다. 톰캣내의 쓰레드 수를 결정 하는 옵션이다. 쓰레드수는 실제 Active User 수를 뜻한다. 즉 순간 처리 가능한Transaction 수를 의미한다.

일반적으로 100 내외가 가장 적절하고, 트렌젝션의 무게에 따라 50~500 개 정도로 설정하는 게 일반적이다. 이 값은 성능 테스트를 통해서 튜닝을 하면서 조정해 나가는 것이 좋다.

 

**tcpNoDelay="true"**

TCP 프로토콜은 기본적으로 패킷을 보낼때 바로 보내지 않는다. 작은 패킷들을 모아서 버퍼 사이즈가 다 차면 모아서 보내는 로직을 사용한다. 그래서 버퍼가 4K라고 가정할때, 보내고자 하는 패킷이 1K이면 3K가 찰 때 까지 기다리기 때문에, 바로바로 전송이 되지 않고 대기가 발생한다.

tcpNoDelay 옵션을 사용하면, 버퍼가 차기전에라도 바로 전송이 되기 때문에, 전송 속도가 빨라진다. 반대로, 작은 패킷을 여러번 보내기 때문에 전체적인 네트워크 트래픽은 증가한다. (예전에야 대역폭이 낮아서 한꺼번에 보내는 방식이 선호되었지만 요즘은 망 속도가 워낙 좋아서 tcpNoDelay를 사용해도 대역폭에 대한 문제가 그리 크지 않다.)

 

## Tomcat Lib 세팅

다음으로 자바 애플리케이션에서 사용하는 라이브러리에 대한 메모리 사용률을 줄이는 방법인데, 일반적으로 배포를 할때 사용되는 라이브러리(jar)를 *.war 패키지 내의 WEB-INF/jar 디렉토리에 넣어서 배포 하는 것이 일반적이다. 보통 하나의 war를 하나의 톰캣에 배포할 때는 큰 문제가 없는데, 하나의 톰캣에 여러개의 war 파일을 동시 배포 하게 되면, 같은 라이브러리가 각각 다른 클래스 로더로 배포가 되기 때문에, 메모리 효율성이 떨어진다.

그래서 이런 경우는 ${TOMCAT_HOME}/lib 디렉토리에 배포를 하고 war 파일에서 빼면 모든 war가 공통 적으로 같은 라이브러리를 사용하기 때문에 메모리 사용이 효율적이고, 또한 시스템 운영 관점에서도 개발팀이 잘못된 jar 버전을 패키징해서 배포하였다 하더라도, lib 디렉토리의 라이브러리가 우선 적용되기 때문에, 관리가 편하다.

반대로 war의 경우, war만 운영중에 재배포를 하면 반영이 가능하지만, lib 디렉토리의 jar 파일들은 반드시 톰캣 인스턴스를 재기동해야 반영되기 때문에, 이 부분은 주의해야 한다.

 

## JVM Tuning

Java Virtual Machine 튜닝은 java 기반 애플리케이션에서는 거의 필수 요소이다.

### -server

제일 먼저 해야할일은 JVM 모드를 server 모드로 전환하는 것이다. JVM 내의 hotspot 컴파일러도 클라이언트 애플리케이션이나 서버 애플리케이션이냐 에 따라서 최적화 되는 방법이 다르다.

그리고 메모리 배치 역시 클라이언트 애플리케이션(MS 워드와같은)의 경우 버튼이나 메뉴는 한번 메모리에 로드 되면, 애플리케이션이 끝날 때 까지 메모리에 잔존하기 때문에 Old 영역이 커야 하지만, 서버의 경우 request를 받아서 처리하고 응답을 주고 빠져서 소멸되는 객체들이 대부분이기 때문에, New 영역이 커야 한다.

이런 서버나 클라이언트냐에 대한 최적화 옵션이 이 옵션 하나로 상당 부분 자동으로 적용되기 때문에, 반드시 적용하기를 바란다.

 

### 메모리 옵션

앞에서도 설명하였듯이 JVM 튜닝의 대부분의 메모리 튜닝이고 그중에서도 JVM 메모리 튜닝은 매우 중요하다. 결국 Full GC 시간을 줄이는 것이 관건인데, 큰 요구 사항만 없다면, 전체 Heap Size는 1G 정도가 적당하다. 그리고 New대 Old의 비율은 서버 애플리케이션의 경우 1:2 비율이 가장 적절하다. 그리고PermSize는 class가 로딩되는 공간인데, 배포하고자 하는 애플리케이션이 아주 크지 않다면 128m 정도면 적당하다. (보통 256m를 넘지 않는다. 256m가 넘는다면 몬가 애플린케이션 배포나 패키징에 문제가 있다고 봐야 한다.)

그리고 heap size는 JVM에서 자동으로 늘리거나 줄일 수 가 있다. 그래서 -Xms와 -Xmx로 최소,최대 heap size를 정할 수 있는데, Server 시스템의 경우 항상 최대 사용 메모리로 잡아 놓는 것이 좋다. 메모리가 늘어난다는 것은 부하가 늘어난다는 것이고, 부하가 늘어날때 메모리를 늘리는 작업 자체가 새로운 부하가 될 수 있기 때문에, 같은 값을 사용하는 것이 좋다.

이렇게 JVM 메모리를 튜닝하면 다음과 같은 옵션이 된다.

*-Xmx1024m **–Xms1024m -XX:MaxNewSize=384m -XX:MaxPermSize=128m*

이렇게 하면 전체 메모리 사용량은 heap 1024m (이중에서 new가 384m) 그리고 perm이 128m 가 되고, JVM 자체가 사용하는 메모리가 보통 300~500m 내외가 되서 java process가 사용하는 메모리 량은 대략 1024+128+300~500 = 대략 1.5G 정도가 된다.

 

32 bit JVM의 경우 process가 사용할 수 있는 공간은 4G가 되는데, 이중 2G는 시스템(OS)이 사용하고 2G가 사용자가 사용할 수 있다. 그래서 위의 설정을 사용하면 32bit JVM에서도 잘 동작한다.

64 bit JVM의 경우 더 큰 메모리 영역을 사용할 수 있는데, 일반적으로 2G를 안 넘는 것이 좋다.(최대 3G), 2G가 넘어서면 Full GC 시간이 많이 걸리기 시작하기 때문에, 그다지 권장하지 않는다. 시스템의 가용 메모리가 많다면 Heap을 넉넉히 잡는 것보다는 톰캣 인스턴스를 여러개 띄워서 클러스터링이나 로드밸런서로 묶는 방법을 권장한다.

 

### OutOfMemory

자바 애플리케이션에서 주로 문제가 되는 것중 하나가 Out Of Memory 에러이다. JVM이 메모리를 자동으로 관리해줌에도 불구하고, 이런 문제가 발생하는 원인은 사용이 끝낸 객체를 release 하지 않는 경우이다. 예를 들어 static 변수를 통해서 대규모 array나 hashmap을 reference 하고 있으면, GC가 되지 않고 계속 메모리를 점유해서 결과적으로 Out Of Memory 에러를 만들어낸다.

Out Of Memory 에러를 추적하기 위해서는 그 순간의 메모리 레이아웃인 Heap Dump가 필요한데, 이 옵션을 적용해놓으면, Out Of Memory가 나올때, 순간적으로 Heap Dump를 떠서 파일로 저장해놓기 때문에, 장애 발생시 추적이 용이하다.

*-XX:-HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./java_pid<pid>.hprof*

 

### GC 옵션

다음은 GC 옵션이다. Memory 옵션 만큼이나 중요한 옵션인데, Parallel GC + Concurrent GC는 요즘은 거의 공식처럼 사용된다고 보면 된다. 이때 Parallel GC에 대한 Thread 수를 정해야 하는데, 이 Thread수는 전체 CPU Core수 보다 적어야 하고, 2~4개 정도가 적당하다.

*-XX:ParallelGCThreads=2 -XX:-UseConcMarkSweepGC*

GC 로그 옵션

그리고 마지막으로 GC Log 옵션이다. 서버와 JVM이 건강한지 메모리상 문제는 없는지 GC 상황은 어떻게 디는지를 추적하려면 GC 로그는 되도록 자세하게 추출할 필요가 있다. GC로그를 상세하게 걸어도 성능 저하는 거의 없다.

*-XX:-PrintGC -XX:-PrintGCDetails -XX:-PrintGCTimeStamps -XX:-TraceClassUnloading -XX:-TraceClassLoading*

 

마지막에 적용된 TraceClassLoading은 클래스가 로딩되는 순간에 로그를 남겨준다. 일반적으로는 사용하지 않아도 되나, OutOfMemory 에러 발생시 Object가 아니라 class에서 발생하는 경우는 Heap dump로는 분석이 불가능 하기 때문에, Out Of Memory 에러시 같이 사용하면 좋다.

 

지금까지 간략하게 나마 톰켓 솔루션에 대한 튜닝 parameter 에 대해서 알아보았다. 사실 이러한 튜닝은 일반적인 개발자에게는 힘든 일이다. 해당 솔루션에 대한 많은 경험이 있어야 하기 때문에, 이런 parameter는 vendor의 기술 지원 엔지니어를 통해서 가이드를 받고, 성능 테스트 과정에서 최적화를 하고 표준화된parameter를 정해서 사용하는 것이 좋다. Apache Tomcat의 경우에도 오픈소스이기는 하지만, Redhat등에서 기술 지원을 제공한다.

출처 - http://bcho.tistory.com/788

[Tomcat JVM heap memory set]

 

]# /etc/profile

export CATALINA_OPTS="-server -Xms256M -Xmx384M"

 

// 정말로 이옵션이 꼭 필요하지 않는이상 적용하지 말아야 합니다.

이 옵션은 힙사이즈를 늘려주는대신 GC 시간을 잡아먹기때문에

사용자수가 유독 많은 웹사이트의 경우 절대 추천하지 않습니다.
톰캣에 무거운 웹 어플리케이션을 돌리면서 오토 리로드 기능을 사용하다 보면 
2~3번은 정상적으로 되지만 그 이후부턴 PermGen space 라는 에러문구와 함께 톰캣이 뻗어버리는 현상이 발생한다.
그것을 예방하기 위해서는 톰캣폴더 하위에 있는 bin 폴더 밑에 catalina.sh 최상단에 아래 문구를 추가해주면 된다. 

\# vi ./catalina.sh

\------------------------------------------------

JAVA_OPTS="-Djava.awt.headless=true -server -Xms512m -Xmx1024m -XX:NewSize=256m -XX:MaxNewSize=256m -XX:PermSize=256m -XX:MaxPermSize=256m -XX:+DisableExplicitGC"

\------------------------------------------------

  출처 - [http://msgzoro.egloos.com/3264728](http://msgzoro.egloos.com/3264728)

Solr 1.4 Enterprise Search Server 에서는 solr 실행시 JVM의 memory 설정을 변경하기 위해서 다음과 같이 한다.
   "java -Xms512M -Xmx1024M -server -jar start.jar"
하지만 sevlet container로 Tomcat을 사용하는 경우
solr의 실행을 위와 같이 하지 않고, solr.war를 webapp에 넣은후 Tomcat을 start하면 자동으로 solr가 실행된다.
따라서 JVM의 설정의 위와같이 변경할 수 없다.
이런 경우에는 다음과 같이 변경하면 된다.
[방법1]
   1. $CATALINA_HOME/bin/catalina.bat 파일을 오픈한다.
   2. JAVA_OPTS를 찾는다.
   3. -Xms512m -Xmx1024m을 추가한다.
예를 들면, 다음과 같다.
set JAVA_OPTS=%JAVA_OPTS% -Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager -Djava.util.logging.config.file="%CATALINA_BASE%\conf\logging.properties" 
-Xms512M -Xmx1024M
[방법2]
Tomcat을 시작하기 전에 CATALINA_OPTS 환경변수를 수정하는 것이다.
Unix의 경우에는 startup.sh scrip에 다음을 추가하면 된다.
export CATALINA_OPTS=-Xms512m -Xmx1024m;
\* startup.sh은 catalina.sh를 호출한다.
[Reference]
[http://www.coderanch.com/t/421341/Tomcat/change-JVM-setting-Xms-Xmx](http://www.coderanch.com/t/421341/Tomcat/change-JVM-setting-Xms-Xmx)
[http://www.informix-zone.com/node/46](http://www.informix-zone.com/node/46)

### Tomcat 메모리 설정

![img](http://cfile9.uf.tistory.com/image/242EB842562BB748082357)

설정시 JAVA_OPTS와  CATALINA_OPTS가 있는데 둘중 아무것에나 설정을 해주면된다.
차이점이라면  JVM runtime 옵션은 같으나 JAVA_OPTS의 경우JVM stop 명령일 때도 동작을 한다.
자바관련 메모리 옵션

![img](http://cfile6.uf.tistory.com/image/2627C341562BB7570860DF)

[설정 관련 한글 위키](http://wiki.ex-em.com/index.php/JVM_Options)

# JVM Options

## [[편집](http://wiki.ex-em.com/index.php?title=JVM_Options&action=edit&section=1)] 개요

### [[편집](http://wiki.ex-em.com/index.php?title=JVM_Options&action=edit&section=2)] 왜 JVM Option을 알아야 하는가

### [[편집](http://wiki.ex-em.com/index.php?title=JVM_Options&action=edit&section=3)] Standard vs. Non-Standard Option

- JVM 구동에 필요한 설정값(Configuration Value)를 지정할 수 있다.
- 성능 개선에 필요한 Parameter 값을 지정할 수 있다.
- Bug나 오동작에 의한 Workaround로 활용할 수 있다.


- **-X Option**: 일반적인 Non-Standard Option이다. Macro한 측면에서의 JVM 제어 기능을 제공한다. -X Option은 JVM마다 다르지만 일부 Option들은 마치 Standard Option처럼 사용된다.
- **-XX Option**: -X Option보다 보다 세밀한 제어 기능을 제공한다. Micro한 측면에서의 JVM 기능을 제공한다. 세밀한 성능 튜닝이나 버그에 대한 Workaround를 위해서 주로 활용된다. -XX Option은 JVM 종류에 따라 완전히 다르다.

### [[편집](http://wiki.ex-em.com/index.php?title=JVM_Options&action=edit&section=4)] Option 지정하기

JVM Option

1. 단일값: *-client* Option과 같이 옵션을 지정하면 그 자체로 의미를 지닌다.
2. 크기(Size): *-Xmx1024m*과 같이 크기(K,M,G)를 지정한다.
3. 숫자(Int): *-XX:SurviorRatio=10*과 같이 숫자값을 지정한다.
4. 문자열(String): *-agentlib:hprof=cpu=samples*과 같이 문자열을 지정한다.
5. Boolean: *-XX:+PrintGCDetails* 혹은 *-XX:-PrintGCDetails*와 같이 +/-를 이용해서 활성화/비활성 여부를 지정한다.

## [[편집](http://wiki.ex-em.com/index.php?title=JVM_Options&action=edit&section=5)] Sun HotSpot JVM (1.5 기준)

### [[편집](http://wiki.ex-em.com/index.php?title=JVM_Options&action=edit&section=6)] Standard Options

| **Option**                            | **Description**                          |
| ------------------------------------- | ---------------------------------------- |
| **-client**                           | Client HotSpot JVM을 사용한다. Client HotSpot JVM은 Desktop용 Application을 구동하는데 유리하다. 성능 최적화(Optimization) 과정을 간략화함으로써 Application의 시작 시간을 최소화한다. |
| **-server**                           | Server HotSpot JVM을 사용한다. Server HotSpot JVM은 Server용 Application을 구동하는데 유리하다. 성능 최적화(Optimization)에 필요한 모든 과정을 최대한으로 수행한다. Application의 시작 시간은 느리지만, 일정 시간이 흐르면 Client HotSpot JVM에 비해 훨씬 뛰어난 성능을 보장한다. **(참고)**Jdk 1.5부터는 Server-Class 머신인 경우에는 -server 옵션이 기본 적용된다. Server-Class 머신이란 2장 이상의 CPU와 2G 이상의 메모리를 갖춘 머신을 의미한다. |
| **-d32**                              | 32bit JVM을 사용한다. 32bit JVM은 메모리를 최대 2G까지만 사용할 수 있다. 반면 일반적인 수행 성능 64bit JVM에 비해 뛰어난 경우가 많다. 따라서 큰 크기의 Java Heap을 사용하지 않는 경우에는 비록 64bit 머신이라고 하더라도 32bit JVM을 사용하는 것이 권장된다. |
| **-d64**                              | 64bit JVM을 사용한다. 64bit JVM에서 사용가능한 메모리의 크기에는 사실상 제한이 없다. 대형 Application들의 경우 수G ~ 수십G의 Java Heap을 사용하는 경우가 많다. |
| **-agentlib:<libname>[=<options>]**   | Native Agent Library를 로딩한다. Native Agent Library란 [JVMPI](http://wiki.ex-em.com/index.php?title=JVMPI&action=edit)/[JVMTI](http://wiki.ex-em.com/index.php?title=JVMTI&action=edit)를 구현한 Library를 의미하며 C/C++로 구현된다. JVM은 Unix/Linux에서는 lib<libname>.so 파일이, Windows에서는 <libname>.dll 파일을 탐색한다. 해당 파일은 현재 Directory나 PATH 변수에 등록된 Directory에 존재해야 한다. **(참조)** JDK 1.4까지는 [HProf](http://wiki.ex-em.com/index.php/HProf)를 실행시키기 위해 *-Xrunhprof:option=value* 옵션을 사용한다. JDK 1.5부터는 *-Xagentlib:hprof=option=value* 옵션이 권장된다. *-Xrunhprof* 옵션은 차후 없어질 수 있다. |
| **-agentpath:<pathname>[=<options>]** | *-agentlib* 옵션과 동일한 기능이다. Library 이름 대신 Full Path 명을 준다. |
| **-javaagent:<jarpath>[=<options>]**  | Java Agent Library를 로딩한다. Java Agent는 Native Agent가 C/C++로 구현되는 것과 달리 Java로 구현된다. [java.lang.instrument](http://java.sun.com/j2se/1.5.0/docs/api/java/lang/instrument/package-summary.html) Package를 통해 Java Agent를 구현하는 필요한 Interface가 제공된다. Java Agent는 [BCI](http://wiki.ex-em.com/index.php/BCI)를 통해 Runtime에 Class들의 Bytecode를 변경하는 방법을 통해 작업을 수행한다. |

### [[편집](http://wiki.ex-em.com/index.php?title=JVM_Options&action=edit&section=7)] Non-Standard Options (-X)

| **Option**                               | **Description**                          |
| ---------------------------------------- | ---------------------------------------- |
| **-Xbootclasspath[/a\|/p]:<path>**       | Boot class path를 제어한다. /a 옵션은 Boot class path의 제일 뒤에 Append, /p 옵션은 제일 앞에 Prepend한다. 일반적인 환경에서는 Boot class path를 제어할 필요가 없다. Java Core Library(rt.jar 등)등에 대해 Reverse Engineering을 수행하고 행동 방식을 변경하고자 할 경우에 주로 활용된다. |
| **-xcheck:jni**                          |                                          |
| **-Xint**                                | Intepreter 모드로만 ByteCode를 실행한다. Interpreter 모드로 실행될 경우에는 JIT Compile 기능이 동작하지 않는다. 이 옵션을 활성화하면 실행 속도이 다소 저하될 수 있다. 따라서 HotSpot Compiler의 버그로 문제가 생길 때만 사용이 권장된다. |
| **-Xnoclassgc**                          | Class Garbage Collection을 수행하지 않는다.      |
| **-Xloggc:<file>**                       | GC Log를 기록할 파일명을 지정한다. 파일명을 지정하지 않으면 Standard Out이나 Standard Error 콘솔에 출력된다. 주로 -XX:+PrintGCTimeStamps, -XX:+PrintGCDetails 옵션과 같이 사용된다. |
| **-Xmixed**                              | Mixed 모드로 ByteCode를 실행한다. HotSpot JVM의 Default 동작 방식이며, -Xint 옵션과 상호 배타적인 옵션이다. |
| **-Xmn<size>**                           | Young Generation이 거주하는 New Space의 크기를 지정한다. 대개의 경우 이 옵션보다는 -XX:NewRatio 옵션이나 -XX:NewSize 옵션을 많이 사용한다. |
| **-Xms<size>**                           | [Java Heap](http://wiki.ex-em.com/index.php?title=Java_Heap&action=edit)의 최초 크기(Start Size)를 지정한다. Java Heap은 -Xms 옵션으로 지정한 크기로 시작하며 최대 -Xmx 옵션으로 지정한 크기만큼 커진다. Sun HotSpt JVM 계열에서는 최초 크기와 최대 크기를 동일하게 부여할 것을 권장한다. 크기의 동적인 변경에 의한 오버 헤드를 최소화하기 위해서이다. |
| **-Xmx<size>**                           | [Java Heap](http://wiki.ex-em.com/index.php?title=Java_Heap&action=edit)의 최대 크기(Maximum Size)를 지정한다. Java Heap은 -Xms 옵션으로 지정한 크기로 시작하며 최대 -Xmx 옵션으로 지정한 크기만큼 커진다. Sun HotSpt JVM 계열에서는 최초 크기와 최대 크기를 동일하게 부여할 것을 권장한다. 크기의 동적인 변경에 의한 오버 헤드를 최소화하기 위해서이다. |
| **-Xrunhprof[:help][:option=value,...]** | [HProf](http://wiki.ex-em.com/index.php/HProf)([Heap and CPU Profiling Agent](http://wiki.ex-em.com/index.php?title=Heap_and_CPU_Profiling_Agent&action=edit))를 실행한다. HProf는 [JVMPI](http://wiki.ex-em.com/index.php?title=JVMPI&action=edit)/[JVMTI](http://wiki.ex-em.com/index.php?title=JVMTI&action=edit)를 이용해 구현된 Sample Profiler이다. 비록 Sample에 불과하지만, 많은 경우 HProf만으로도 상당히 유용한 정보들을 얻을 수 있다. |
| **-Xrs**                                 | OS Signal사용을 최소화한다. 가령 이 옵션을 켜면 kill -3 [pid] 명령을 수행해도 Thread dump가 발생하지 않는다. |
| **-Xss<size>**                           | 개별 Thread의 Stack Size를 지정한다. 예를 들어 Thread Stack Size가 1M이고, Thread가 최대 100개 활성화된다면, 최대 100M의 메모리를 사용하게 된다. 대부분의 경우 기본값(Default)을 그대로 사용하는 것이 바람직하다. 많은 수의 Thread를 사용하는 Application의 경우 Thread Stack에 의한 메모리 요구량이 높아지며 이로 인해 [Out Of Memory Error](http://wiki.ex-em.com/index.php?title=OOEM&action=edit)가 발생할 수 있다. 이런 경우에는 -Xss 옵션을 이용해 Thread Stack Size를 줄여주어야 한다. |

### [[편집](http://wiki.ex-em.com/index.php?title=JVM_Options&action=edit&section=8)] Non-Standard Options (-XX)

+

\-

| **Option**                               | **Default**          | **Description**                          |
| ---------------------------------------- | -------------------- | ---------------------------------------- |
| **-XX:+AggressiveHeap**                  | False                | 말 그대로 Heap을 Aggressive(공격적)하게 사용하는 옵션이다. 이 옵션이 활성화되면 JVM은 현재 Application을 Memory-Intensive한 것으로 간주하고 Heap 공간을 최대한 사용하게끔 관련된 다른 옵션 값들을 결정한다. 가령 [UseParallelGC](http://wiki.ex-em.com/index.php/Jvm_options#UseParallelGC) 옵션을 활성화시키고, Java Heap의 크기를 Physical Memory의 최대치에 가깝게 설정한다. 이 옵션은 비록 사용하기는 간편하지만, 일반적으로 잘 사용되지는 않는다. 대부분의 경우, 개별적인 옵션들을 이용해 좀 더 세밀한 튜닝을 시도한다. |
| **-XX:+CMSClassUnloadingEnabled**        | False                | [CMS Collector](http://wiki.ex-em.com/index.php/CMS_Collector)는 [Permanent Generation](http://wiki.ex-em.com/index.php?title=Permanent_Generation&action=edit)에 대해 GC 작업을 수행하지 않으며, Class 메타데이터에 대한 Unloading 작업 또한 수행하지 않는다. 따라서 Application의 특성상 많은 수의 Class를 동적으로 생성하고 Loading하는 경우에는 [Permanent Generation](http://wiki.ex-em.com/index.php?title=Permanent_Generation&action=edit)에서 [Out Of Memory Error](http://wiki.ex-em.com/index.php/Out_Of_Memory_Error)가 발생할 수 있다. 이런 경우에는 이 옵션과 함께 [CMSPermGenSweepingEnabled](http://wiki.ex-em.com/index.php/Jvm_options#CMSPermGenSweepingEnabled)옵션을 사용해서 [Permanent Generation](http://wiki.ex-em.com/index.php?title=Permanent_Generation&action=edit)에 대한 GC 작업과 Class Unloading 작업을 활성화한다. JDK 1.5까지는 이 두 옵션을 모두 활성화해야 Class Unloading이 이루어진다. JDK 1.6부터는 [CMSPermGenSweepingEnabled](http://wiki.ex-em.com/index.php/Jvm_options#CMSPermGenSweepingEnabled) 옵션을 활성화하지 않아도 이 옵션이 작동한다. |
| **-XX:CMSFullGCsBeforeCompaction=<value>** | -1                   | [CMS Collector](http://wiki.ex-em.com/index.php/CMS_Collector)에서 [Compaction](http://wiki.ex-em.com/index.php?title=Compaction&action=edit)(압축)을 수행하기 전에 [Full GC](http://wiki.ex-em.com/index.php?title=Full_GC&action=edit)를 수행할 회수를 지정한다. 일반적인 Full GC는 Compaction 작업을 수반한다. 반면에 [CMS Collector](http://wiki.ex-em.com/index.php/CMS_Collector)의 [Full GC](http://wiki.ex-em.com/index.php?title=Full_GC&action=edit)는 Compaction을 수행하지 않는다. 이로 인해 Heap의[Fragmentation](http://wiki.ex-em.com/index.php?title=Fragmentation&action=edit)이 발생할 수 있지만, [Full GC](http://wiki.ex-em.com/index.php?title=Full_GC&action=edit)에 의한 [Pause Time](http://wiki.ex-em.com/index.php?title=Pause_Time&action=edit)을 최소화할 수 있다는 장점이 있다.이 옵션은 Compaction의 발생 시점을 제어하는 역할을 한다. 예를 들어 이 값이 "1"인 경우, Concurrent Full GC가 아직 종료되지 않은 시점에 새로운 Concurrent Full GC 작업이 시작되면(1), Compaction이 수반된다. 만일 이 값이 "0"인 경우에는 Concurrent Full GC는 "항상" Compaction을 수반한다. 따라서 [CMS Collector](http://wiki.ex-em.com/index.php/CMS_Collector)를 사용하는 환경에서 [Heap Fragmentation](http://wiki.ex-em.com/index.php?title=Heap_Fragmentation&action=edit)에 의한 문제가 발생하는 경우에는 "0"의 값을 부여하는 것이 Workaround가 될 수 있다. |
| **-XX:+CMSIncrementalMode**              | False                | Full GC 작업을 Incremental하게 진행한다. 일반적으로 [CMS Collector](http://wiki.ex-em.com/index.php/CMS_Collector)는 [Old Generation](http://wiki.ex-em.com/index.php?title=Old_Generation&action=edit)가 어느 정도 이상 점유되면 Concurrent Full GC 작업을 시작한다. 반면 이 옵션이 활성화되면 [Old Generation](http://wiki.ex-em.com/index.php?title=Old_Generation&action=edit)의 사용률과 무관하게 백그라운드에서 점진적으로(Incremental) [Old Generation](http://wiki.ex-em.com/index.php?title=Old_Generation&action=edit)에 대한 GC 작업을 수행한다. 이 옵션을 사용하면 [CMSInitiatingOccupancyFraction](http://wiki.ex-em.com/index.php/Jvm_options#CMSInitiatingOccupancyFraction) 옵션은 무시된다. 이 옵션을 활성화하면 Throughput은 다소 줄어들고, Response Time은 좀 개선되는 경향이 있다. 따라서 GC 작업 Pause를 더 줄이고 싶을 경우에 사용할 수 있다. |
| **-XX:CMSInitiatingOccupancyFraction=<value>** | -1                   | [CMS Collection](http://wiki.ex-em.com/index.php?title=CMS_Collection&action=edit)이 시작되는 임계값을 결정한다. 만일 이 값이 "50"이면 [Old Generation](http://wiki.ex-em.com/index.php?title=Old_Generation&action=edit)이 50% 이상 사용되면 Concurre Full GC가 시작된다. 이 값의 기본값은 "-1"이다. 이 경우에는 [CMSTriggerRatio](http://wiki.ex-em.com/index.php/Jvm_options#CMSTriggerRatio) 옵션과 [MinHeapFreeRatio](http://wiki.ex-em.com/index.php/Jvm_options#MinHeapFreeRatio) 옵션이 임계치로 사용된다. 임계치의 계산 공식은 다음과 같다.*Start Ratio = 100-MinHeapFreeRatio(=40) + MinHeapFreeRatio(=40) \* (CMSTriggerRatio(=80)/100) = 92* 즉, CMSInitiatingOccupancyFraction 옵션이 지정되지 않으면 [Old Generation](http://wiki.ex-em.com/index.php?title=Old_Generation&action=edit)이 92% 정도 사용될 때 [Concurrent Full GC](http://wiki.ex-em.com/index.php/Concurrent_Full_GC)가 시작된다.이 옵션을 지정하면 50%에서 시작하여, 옵션으로 지정된 값까지 점진적으로 임계값을 조정한다. 만일 임계값을 고정하고자 할 경우에는 [UseCMSInitiatingOccupancyOnly](http://wiki.ex-em.com/index.php/Jvm_options#UseCMSInitiatingOccupancyOnly) 옵션을 활성화해야 한다.이 옵션의 값이 작으면 [CMS Collection](http://wiki.ex-em.com/index.php?title=CMS_Collection&action=edit)이 그만큼 빨리 동작하기 때문에 [Promotion Failure](http://wiki.ex-em.com/index.php?title=Promotion_Failure&action=edit)에 의한 [Stop The World](http://wiki.ex-em.com/index.php?title=Stop_The_World&action=edit) GC 작업이 발생할 확률이 그만큼 줄어든다. |
| **-XX:+CMSPermGenSweepingEnabled**       | False                | [CMS Collector](http://wiki.ex-em.com/index.php/CMS_Collector)는 기본적으로 [Permanent Generation](http://wiki.ex-em.com/index.php?title=Permanent_Generation&action=edit)에 대해 Collection을 수행하지 않는다. 따라서 많은 수의 Class를 Loading하는 경우 [Out Of Memory Error](http://wiki.ex-em.com/index.php/Out_Of_Memory_Error)가 발생할 수 있다. 이 옵션을 활성화하면 [Permanent Generation](http://wiki.ex-em.com/index.php?title=Permanent_Generation&action=edit)에 대한 Collection을 수행한다. JDK 1.5까지는 이 옵션과 함께 [CMSClassUnloadingEnabled ](http://wiki.ex-em.com/index.php/Jvm_options#CMSClassUnloadingEnabled)옵션을 활성화해야 동작한다. |
| **-XX:CompilerCommandFile=<file>**       | .hotspot_compiler    | [Compiler Command File](http://wiki.ex-em.com/index.php?title=Compiler_Command_File&action=edit)의 위치를 지정한다. |
| **-XX:+DisableExplicitGC**               | False                | System.gc 호출에 의한 [Explicit GC](http://wiki.ex-em.com/index.php/Explicit_GC)를 비활성화한다. RMI에 의한 [Explicit GC](http://wiki.ex-em.com/index.php/Explicit_GC)나 Application에서의 [Explicit GC](http://wiki.ex-em.com/index.php/Explicit_GC)를 원천적으로 방지하고자 할 경우에 사용된다. |
| **-XX:GCHeapFreeLimit=<Percentage>**     | 5                    | [Parallel Collector](http://wiki.ex-em.com/index.php/Parallel_Collector)를 사용할 때 GC도중 [Out Of Memory Error](http://wiki.ex-em.com/index.php/Out_Of_Memory_Error)의 발생을 방지하는데 도움을 준다. GC로 확보해야할 Free Space의 하한선을 결정한다. 이 값은 Max Heap 크기에 대한 Free 공간 크기의 비율이며 기본값은 "5"이다. 즉 [Parallel Collection](http://wiki.ex-em.com/index.php?title=Parallel_Collection&action=edit) 후 확보해야할 Free 공간 크기가 적어도 Max Heap 크기의 5% 이상이 되도록 보장하는 것이다. |
| **-XX:GCTimeLimit=<Percentage>**         | 90                   | [Parallel Collector](http://wiki.ex-em.com/index.php/Parallel_Collector)를 사용할 때 GC도중 [Out Of Memory Error](http://wiki.ex-em.com/index.php/Out_Of_Memory_Error)의 발생을 방지하는데 도움을 준다. 전체 JVM 수행시간 대비[Parallel Collection](http://wiki.ex-em.com/index.php?title=Parallel_Collection&action=edit) 수행 시간의 상한선를 결정한다. 기본값은 "90"이다. 즉 Parallel Collection이 전체 수행 시간의 90%까지 사용할 수 있게 된다. |
| **-XX:+HeapDumpOnOutOfMemoryError**      | False                | [Out Of Memory Error](http://wiki.ex-em.com/index.php/Out_Of_Memory_Error)가 발생하면 [Heap Dump](http://wiki.ex-em.com/index.php?title=Heap_Dump&action=edit)를 File에 기록한다. JDK 1.6 부터 지원되는 옵션이다. |
| **-XX:MaxGCMinorPauseMillis=<Value>**    | None                 | [Minor GC](http://wiki.ex-em.com/index.php?title=Minor_GC&action=edit)에 의한 Pause Time을 <value>ms 이하가 되게끔 Heap 크기와 기타 옵션들을 자동으로 조정하는 기능을 한다. 이 값은 목표값(Target)이지 고정값이 아니다. [Minor GC](http://wiki.ex-em.com/index.php?title=Minor_GC&action=edit)에 의한 Pause Time이 길 경우에 Workaround로 사용할 수 있다. |
| **-XX:MaxGCPauseMillis=<Value>**         | None                 | [GC](http://wiki.ex-em.com/index.php?title=GC&action=edit)에 의한 Pause Time을 <value>ms 이하가 되게끔 Heap 크기와 기타 옵션들을 자동으로 조정하는 기능을 한다.[MaxGCMinorPauseMillis](http://wiki.ex-em.com/index.php/Jvm_options#MaxGCMinorPauseMillis) 옵션과 마찬가지로 목표값으로서의 역할을 한다. GC에 의한 Pause Time이 길 경우에 Workaround로 사용할 수 있다. |
| **-XX:MaxHeapFreeRatio=<Value>**         | 70                   | [Heap Shrinkage](http://wiki.ex-em.com/index.php?title=Heap_Shrinkage&action=edit)를 수행하는 임계치를 지정한다. 예를 들어 이 값이 70이면 Heap의 Free 공간이 70% 이상이 되면 Heap 크기가 축소된다. MinHeapFreeRatio 옵션과 함께 Heap의 크기 조정을 담당한다. |
| **-XX:MaxNewSize=<Value>**               | None                 | [Young Generation](http://wiki.ex-em.com/index.php?title=Young_Generation&action=edit)의 최대 크기를 지정한다. [Young Generation](http://wiki.ex-em.com/index.php?title=Young_Generation&action=edit)의 시작 크기는 NewSize 옵션에 의해 지정된다. |
| **-XX:MaxPermSize=<Value>**              | None                 | [Permanent Generation](http://wiki.ex-em.com/index.php?title=Permanent_Generation&action=edit)의 최대 크기를 지정한다. [Permanent Generation](http://wiki.ex-em.com/index.php?title=Permanent_Generation&action=edit)의 시작 크기는 PermSize 옵션에 의해 지정된다. 많은 수의 Class를 Loading하는 Application은 [PermSize](http://wiki.ex-em.com/index.php/Jvm_options#PermSize)와 [MaxPermSize](http://wiki.ex-em.com/index.php/Jvm_options#MaxPermSize) 옵션을 이용해 [Permanent Generation](http://wiki.ex-em.com/index.php?title=Permanent_Generation&action=edit)의 크기를 크게 해주는 것이 좋다. [Permanent Generation](http://wiki.ex-em.com/index.php?title=Permanent_Generation&action=edit)의 크기가 작을 경우에는 [Out Of Memory Error](http://wiki.ex-em.com/index.php/Out_Of_Memory_Error)가 발생할 수 있다. |
| **-XX:MinHeapFreeRatio=<Value>**         | 40                   | [Heap Expansion](http://wiki.ex-em.com/index.php?title=Heap_Expansion&action=edit)을 수행하는 임계치를 지정한다. 예를 들어 이 값이 40이면 Heap의 Free 공간이 40% 미만이 되면 Heap 크기가 확대된다. MaxHeapFreeRatio 옵션과 함께 Heap의 크기 조정을 담당한다. |
| **-XX:NewRatio=<Value>**                 | OS/JDK Version마다 다름  | [Young Generation](http://wiki.ex-em.com/index.php?title=Young_Generation&action=edit)과 [Old Generation](http://wiki.ex-em.com/index.php?title=Old_Generation&action=edit)의 비율을 결정한다. 예를 들어 이값이 2이면 Young:Old = 1:2 가 되고, [Young Generation](http://wiki.ex-em.com/index.php?title=Young_Generation&action=edit)의 크기는 전체 Java Heap의 1/3이 된다. |
| **-XX:NewSize=<Value>**                  | OS/JDK Version마다 다름  | [Young Generation](http://wiki.ex-em.com/index.php?title=Young_Generation&action=edit)의 시작 크기를 지정한다. [Young Generation](http://wiki.ex-em.com/index.php?title=Young_Generation&action=edit)의 크기는 [NewSize](http://wiki.ex-em.com/index.php/Jvm_options#NewSize) 옵션(시작 크기)과 [MaxNewSize](http://wiki.ex-em.com/index.php/Jvm_options#MaxNewSize) 옵션(최대 크기)에 의해 결정된다. |
| **-XX:OnError=<Command>**                | None                 | [Fatal Error](http://wiki.ex-em.com/index.php?title=Fatal_Error&action=edit)가 발생할 경우(예: Native Heap에서의 Out Of Memory Error), <Command>로 지정된 OS 명령문을 수행한다. 비정상적인 JVM 장애 현상을 좀 더 자세하게 분석하고자 할 경우에 주로 사용된다.`-XX:OnError="pmap %p"  --> JVM에서 Fatal Error가 발생하면 Process ID에 대해 pmap 명령을 수행한다.` |
| **-XX:OnOutOfMemoryError=<Command>**     | None                 | [Out Of Memory Error](http://wiki.ex-em.com/index.php/Out_Of_Memory_Error)가 발생할 경우, <Command>로 지정된 OS 명령문을 수행한다. JDK 1.6에 추가된 옵션으로, [Out Of Memory Error](http://wiki.ex-em.com/index.php/Out_Of_Memory_Error)가 Java에서 얼마나 보편적으로 발생하는지를 알 수 있다. `-XX:OnOutOfMemoryError="pmap %p"  --> JVM에서 Fatal Error가 발생하면 Process ID에 대해 pmap 명령을 수행한다.` |
| **-XX:ParallelGCThreads=<value>**        | CPU 개수               | [Parallel Collector](http://wiki.ex-em.com/index.php/Parallel_Collector)를 사용할 경우, GC작업을 수행할 Thread의 개수를 지정한다. 기본값은 CPU 개수이다. 즉, Parallel GC 작업을 수행하기 위해 시스템 전체의 CPU를 최대한 활용한다. 하나의 Machine에 여러 개의 JVM을 구동하는 환경이나, 하나의 Machine을 여러 종류의 Application이 공유해서 사용하는 환경에서는 이 옵션의 값을 낮게 설정해서 GC Thread의 개수를 줄임으로써 성능 개선을 꾀할 수 있다. [Context Switching](http://wiki.ex-em.com/index.php?title=Context_Switching&action=edit)에 의한 성능 저하를 막을 수 있기 때문이다. |
| **-XX:PermSize=<size>**                  |                      | [Permanent Generation](http://wiki.ex-em.com/index.php?title=Permanent_Generation&action=edit)의 최초 크기를 지정한다. [Permanent Generation](http://wiki.ex-em.com/index.php?title=Permanent_Generation&action=edit)의 최대 크기는 MaxPermSize 옵션에 의해 지정된다. 많은 수의 Class를 로딩하는 Application은 큰 크기의 [Permanent Generation](http://wiki.ex-em.com/index.php?title=Permanent_Generation&action=edit)을 필요로 한며, [Permanent Generation](http://wiki.ex-em.com/index.php?title=Permanent_Generation&action=edit)의 크기가 작아서 Class를 로딩하는 못하면 [Out Of Memory Error](http://wiki.ex-em.com/index.php/Out_Of_Memory_Error)가 발생한다. |
| **-XX:PretenureSizeThreshold=<value>**   | 0                    | 일반적으로 Object는 [Young Generation](http://wiki.ex-em.com/index.php?title=Young_Generation&action=edit)에 최초 저장된 후 시간이 흐름에 따라 [Tenured Generation](http://wiki.ex-em.com/index.php?title=Tenured_Generation&action=edit)으로 Promotion된다. 하지만, Object의 크기가 [Young Generation](http://wiki.ex-em.com/index.php?title=Young_Generation&action=edit)보다 큰 경우 JVM은 [Old Generation](http://wiki.ex-em.com/index.php?title=Old_Generation&action=edit)에 Object를 직접 저장하기도 한다. PretenuredSizeThreshold 옵션을 이용하면 [Young Generation](http://wiki.ex-em.com/index.php?title=Young_Generation&action=edit)을 거치지 않고 직접 [Old Generation](http://wiki.ex-em.com/index.php?title=Old_Generation&action=edit)에 저장하게끔 할 수 있다. 가령 이 옵션의 값이 1048576(1M)이면, 1M 크기 이상의 오브젝트는 [Old Generation](http://wiki.ex-em.com/index.php?title=Old_Generation&action=edit)에 바로 저장된다. 큰 크기의 오브젝트를 [Old Generation](http://wiki.ex-em.com/index.php?title=Old_Generation&action=edit)에 직접 저장함으로써 불필요한 [Minor GC](http://wiki.ex-em.com/index.php?title=Minor_GC&action=edit)를 줄이고자 하는 목적으로 사용된다. |
| **-XX:+PrintGCApplicationStoppedTime**   | False                | Application에서 Stop이 발생한 경우 소요된 시간 정보를 기록한다. 이 시간은 GC 작업 자체에 의한 Stop 뿐만 아니라 JVM의 내부적인 이유로 Application Thread들을 Stop 시킨 경우를 포함한다. |
| **-XX:+PrintGCDetails**                  | False                | GC 발생시 Heap 영역에 대한 비교적 상세한 정보를 추가적으로 기록한다. 추가적인 정보는 {GC 전 후의 Young/Old Generation의 크기, GC 전 후의 Permanent Generation의 크기, GC 작업에 소요된 시간} 등이다. [Minor GC](http://wiki.ex-em.com/index.php?title=Minor_GC&action=edit)가 발생한 경우 PrintGCDetails 옵션의 적용 예는 아래와 같다. `[GC [DefNew: 64575K->959K(64576K), 0.0457646 secs] 196016K->133633K(261184K), 0.0459067 secs]]`위의 로그가 의미하는 바는 다음과 같다. GC 전의 Young Generation Usage = 64M, Young Generation Size = 64MGC 전의 Total Heap Usage = 196M, Total Heap Size = 260MGC 후의 Young Generation Usage = 9.5MGC 후의 Total Heap Usage = 133MMinor GC 소요 시간 = 0.0457646 초[Major GC](http://wiki.ex-em.com/index.php?title=Major_GC&action=edit)가 발생한 경우 PrintGCDetails 옵션의 적용 예는 아래와 같다. `111.042: [GC 111.042: [DefNew: 8128K->8128K(8128K), 0.0000505 secs]111.042: [Tenured: 18154K->2311K(24576K), 0.1290354 secs] 26282K->2311K(32704K), 0.1293306 secs]`위의 로그는 [Minor GC](http://wiki.ex-em.com/index.php?title=Minor_GC&action=edit) 정보 외에 다음과 같은 [Major GC](http://wiki.ex-em.com/index.php?title=Major_GC&action=edit) 정보를 제공한다. GC 전의 Tenured Generation Usage = 18M, Tenured Generation Size = 24MGC 후의 Tenured Generation Usage = 2.3MMajor GC 소요시간 = 0.12초**(참고)** PrintGCDetails + PrintGCTimeStamps 옵션의 조합이 가장 보편적으로 사용된다. |
| **-XX:+PrintGCTimeStamps**               | False                | GC가 발생한 시간을 JVM의 최초 구동 시간 기준으로 기록한다. **(참고)** PrintGCDetails + PrintGCTimeStamps 옵션의 조합이 가장 보편적으로 사용된다. |
| **-XX:+PrintHeapAtGC**                   | Fasle                | GC 발생 전후의 Heap에 대한 정보를 상세하게 기록한다. PrintHeapAtGC 옵션과 [PrintGCDetails](http://wiki.ex-em.com/index.php/Jvm_options#PrintGCDetails) 옵션을 같이 사용하면 GC에 의한 Heap 변화 양상을 매우 정확하게 파악할 수 있다. 아래에 PrintHeapAtGC 옵션의 적용 예가 있다. `0.548403: [GC {Heap before GC invocations=1:Heappar new generation total 18432K, used 12826K [0xf2800000, 0xf4000000, 0xf4000000]eden space 12288K, 99% used<1> [0xf2800000, 0xf33ff840, 0xf3400000]from space 6144K, 8% used<2> [0xf3a00000, 0xf3a87360, 0xf4000000]to space 6144K, 0% used<3> [0xf3400000, 0xf3400000, 0xf3a00000]concurrent mark-sweep generation total 40960K, used 195K<4>[0xf4000000, 0xf6800000, 0xf6800000]CompactibleFreeListSpace space 40960K, 0% used [0xf4000000, 0xf6800000]concurrent-mark-sweep perm gen total 4096K, used 1158K<5> [0xf6800000, 0xf6c00000, 0xfa800000]CompactibleFreeListSpace space 4096K, 28% used [0xf6800000, 0xf6c00000]0.549364: [ParNew: 12826K<6>->1086K<7>(18432K<8>), 0.02798039 secs] 13022K->1282K(59392K)Heap after GC invocations=2:Heappar new generation total 18432K, used 1086K [0xf2800000, 0xf4000000, 0xf4000000]eden space 12288K, 0% used<10> [0xf2800000, 0xf2800000, 0xf3400000]from space 6144K, 17% used<11> [0xf3400000, 0xf350fbc0, 0xf3a00000]to space 6144K, 0% used<12> [0xf3a00000, 0xf3a00000, 0xf4000000]concurrent mark-sweep generation total 40960K, used 195K<13> [0xf4000000, 0xf6800000, 0xf6800000]CompactibleFreeListSpace space 40960K, 0% used [0xf4000000, 0xf6800000]concurrent-mark-sweep perm gen total 4096K, used 1158K<14> [0xf6800000, 0xf6c00000, 0xfa800000]CompactibleFreeListSpace space 4096K, 28% used [0xf6800000, 0xf6c00000]} , 0.0297669 secs]` |
| **-XX:SoftRefLRUPolicyMSPerMB=<value>**  | 1000(ms)             | [Soft Reference](http://wiki.ex-em.com/index.php?title=Soft_Reference&action=edit)가 [Java Heap](http://wiki.ex-em.com/index.php?title=Java_Heap&action=edit)에서 밀려나는 주기를 설정한다. 기본값이 1000ms(1초)이다. JDK 1.3.1까지는 [Soft Reference](http://wiki.ex-em.com/index.php?title=Soft_Reference&action=edit)는 GC 작업 발생시 항상 메모리에서 해제되었다. 하지만 이후 버전에서는 Free Memory에 비례해 일정 시간 정도 메모리에 보관하게끔 변경되었다. 가령 이 값이 **1000(1초)**이면, Heap의 Free Memory 1M마다 바로 직전에 참조된 시간에서 **1**초가 지나지 않았다면 메모리에서 해제하지 않는다. 이 값을 크게 부여하면 [Soft Reference](http://wiki.ex-em.com/index.php?title=Soft_Reference&action=edit)가 그만큼 오래 메모리에 머물고 사용 효율이 높아진다. 반면 메모리 점유율이 높아진다. 따라서 Applicaiton에서 [Soft Reference](http://wiki.ex-em.com/index.php?title=Soft_Reference&action=edit)를 많이 사용하고, Free Memory가 많지 않은 상황에서는 이 값을 낮출 필요가 있다. 반면 [Soft Reference](http://wiki.ex-em.com/index.php?title=Soft_Reference&action=edit)에 기반하여 Cache를 구현하고, Free Memory에 여유가 있는 상황에서는 이 값을 높임으로써 성능 향상을 꾀할 수 있다. |
| **-XX:SurvivorRatio=<value>**            | 5~6(OS/Version마다 다름) | [Survivor Space](http://wiki.ex-em.com/index.php?title=Survivor_Space&action=edit)와 [Eden Space](http://wiki.ex-em.com/index.php?title=Eden_Space&action=edit)의 비율을 지정한다. 만일 이 값이 6이면, **To Survivor Ratio:From Survivor Ratio:Eden Space = 1:1:6**이 된다. 즉, 하나의 [Survivor Space](http://wiki.ex-em.com/index.php?title=Survivor_Space&action=edit)의 크기가 [Young Generation](http://wiki.ex-em.com/index.php?title=Young_Generation&action=edit)의 1/8이 된다. [Survivor Space](http://wiki.ex-em.com/index.php?title=Survivor_Space&action=edit)의 크기가 크면 [Tenured Generation](http://wiki.ex-em.com/index.php?title=Tenured_Generation&action=edit)으로 옮겨가지 전의 중간 버퍼 영역이 커지는 셈이다. 따라서 [Full GC](http://wiki.ex-em.com/index.php?title=Full_GC&action=edit)의 빈도를 줄이는 역할을 할 수 있다. 반면 [Eden Space](http://wiki.ex-em.com/index.php?title=Eden_Space&action=edit)의 크기가 줄어들므로 [Minor GC](http://wiki.ex-em.com/index.php?title=Minor_GC&action=edit)가 자주 발생하게 된다. |
| **-XX:+TraceClassLoading**               | False                | Class Loading을 추적하는 메시지를 뿌릴지의 여부를 지정한다. [TraceClassUnloading](http://wiki.ex-em.com/index.php/Jvm_options#TraceClassUnloading) 옵션과 함께 ClassLoader 문제를 추적하고자 할 때 사용된다. |
| **-XX:+TraceClassUnloading**             | False                | Class Unloading을 추적하는 메시지를 뿌릴지의 여부를 지정한다. [TraceClassLoading](http://wiki.ex-em.com/index.php/Jvm_options#TraceClassLoading) 옵션과 함께 ClassLoader 문제를 추적하고자 할 때 사용된다. 이 옵션은 JDK 1.6에서 추가되었다. |
| **-XX:+UseAdaptiveSizePolciy**           | True                 | [Parallel Collector](http://wiki.ex-em.com/index.php/Parallel_Collector)를 사용할 경우 [Young Generation](http://wiki.ex-em.com/index.php?title=Young_Generation&action=edit)의 크기를 Adaptive하게 적용할 지의 여부를 지정한다. [Parallel Collector](http://wiki.ex-em.com/index.php/Parallel_Collector)의 목적은 Throughput을 최대화하는 것이며, 이 목적에 따라 [Young Generation](http://wiki.ex-em.com/index.php?title=Young_Generation&action=edit)의 크기를 JVM 스스로 조정한다.**(주의)** Adaptive Size를 사용하는 경우 [Young Generation](http://wiki.ex-em.com/index.php?title=Young_Generation&action=edit)의 크기가 잘못 계산되어 [Full GC](http://wiki.ex-em.com/index.php?title=Full_GC&action=edit)를 과잉 유발하는 것과 같은 오동작을 하는 경우가 있다. 이럴 경우에는 이 옵션의 값을 False(-XX:-UseAdaptiveSizePolicy)로 변경해주어야 한다. |
| **-XX:+UseCMSCompactAtFullCollection**   | True                 | [CMS Collector](http://wiki.ex-em.com/index.php/CMS_Collector)에 의한 [Concurrent GC](http://wiki.ex-em.com/index.php/Concurrent_GC) 수행 시 Compaction 작업을 수행할 지의 여부를 지정한다. 이 값이 True이면, [Old Generation](http://wiki.ex-em.com/index.php?title=Old_Generation&action=edit)의 Fragmentation에 의해 [Promotion Failure](http://wiki.ex-em.com/index.php?title=Promotion_Failure&action=edit)가 발생할 때 [Stop The World](http://wiki.ex-em.com/index.php?title=Stop_The_World&action=edit) 방식의 [Full GC](http://wiki.ex-em.com/index.php?title=Full_GC&action=edit)를 수행하며 Compaction이 이루어진다. JDK 1.4.2부터는 True가 Default 값이다. 좀 더 자세한 내용은 [CMSFullGCsBeforeCompaction](http://wiki.ex-em.com/index.php/Jvm_options#CMSFullGCsBeforeCompaction)파라미터를 참조한다. |
| **-XX:+UseCMSInitiatingOccupancyOnly**   | False                | [Concurrent Full GC](http://wiki.ex-em.com/index.php/Concurrent_Full_GC)를 수행할 기준으로 최초에 지정된 비율을 고정적으로 사용할지의 여부를 지정한다. 최초의 비율은[CMSInitiatingOccupancyFraction](http://wiki.ex-em.com/index.php/Jvm_options#CMSInitiatingOccupancyFraction) 옵션에 의해 지정된다. [CMS Collector](http://wiki.ex-em.com/index.php/CMS_Collector)를 사용하는 환경에서 [Full GC](http://wiki.ex-em.com/index.php?title=Full_GC&action=edit)가 자주 발생하는 경우 [CMSInitiatingOccupancyFraction](http://wiki.ex-em.com/index.php/Jvm_options#CMSInitiatingOccupancyFraction) 옵션의 값을 낮게(50이하)로 지정하고, 이 옵션의 값을 True로 지정하는 방법을 많이 사용한다. |
| **-XX:+UseConcMarkSweepGC**              | False                | [CMS Collector](http://wiki.ex-em.com/index.php/CMS_Collector)를 사용할 지의 여부를 지정한다. [GC Pause](http://wiki.ex-em.com/index.php?title=GC_Pause&action=edit)에 의한 사용자 응답 시간 저하 현상을 줄이고자 할 경우에 사용이 권장된다. |
| **-XX:+UseParallelGC**                   | 환경에 따라 다름            | [Parallel Collector](http://wiki.ex-em.com/index.php/Parallel_Collector)를 사용할 지의 여부를 지정한다. JDK 1.4까지는 False가 기본값이다. JDK 1.5부터는 서버급 머신인 경우에는 True, 클라이언트급 머신일 경우에는 False가 기본값이다. 서버급 머신이란 CPU가 2개 이상, Physical RAM이 2G 이상인 머신을 의미한다. 큰 크기의 [Young Generation](http://wiki.ex-em.com/index.php?title=Young_Generation&action=edit)이 일반적인 엔터프라이즈 환경에서는 [Parallel Collector](http://wiki.ex-em.com/index.php/Parallel_Collector)를 사용함으로써[Minor GC](http://wiki.ex-em.com/index.php?title=Minor_GC&action=edit)에 의한 [GC Pause](http://wiki.ex-em.com/index.php?title=GC_Pause&action=edit)를 최소화할 수 있다. [Parallel Collector](http://wiki.ex-em.com/index.php/Parallel_Collector)는 [Young Generation](http://wiki.ex-em.com/index.php?title=Young_Generation&action=edit)에 대해서만 작동한다는 사실에 주의하자. [Old Generation](http://wiki.ex-em.com/index.php?title=Old_Generation&action=edit)에 대해 Parallel Collection을 사용하고자 하는 경우에는 [UseParallelOldGC](http://wiki.ex-em.com/index.php/Jvm_options#UseParallelOldGC) 옵션을 사용한다. |
| **-XX:+UseParallelOldGC**                | False                | [Old Generation](http://wiki.ex-em.com/index.php?title=Old_Generation&action=edit)에 대해 Parallel Collection을 수행할 지의 여부를 지정한다. JDK 1.6에서 추가된 옵션이다. |
| **-XX:+UseParNewGC**                     | 환경에 따라 다름            | [CMS Collector](http://wiki.ex-em.com/index.php/CMS_Collector)를 사용하는 경우에 한해서, [Young Generation](http://wiki.ex-em.com/index.php?title=Young_Generation&action=edit)에 대해서 Parallel Collection을 수행할 지의 여부를 지정한다. 이 옵션과 [UseParallelGC](http://wiki.ex-em.com/index.php/Jvm_options#UseParallelGC), [UseParallelOldGC](http://wiki.ex-em.com/index.php/Jvm_options#UseParallelOldGC) 옵션과의 차이를 명확하게 구분해야 한다. |
| **-XX:+UseSerialGC**                     | 환경에 따라 다름            | [Serial Collector](http://wiki.ex-em.com/index.php/Serial_Collector)를 사용할 지의 여부를 지정한다. JDK 1.4까지는 Default 값이 True이다. JDK 1.5에서는 [UseParallelGC](http://wiki.ex-em.com/index.php/Jvm_options#UseParallelGC) 옵션에서 설명한 것처럼 머신의 등급에 따라 Default 값이 결정된다. |

## [[편집](http://wiki.ex-em.com/index.php?title=JVM_Options&action=edit&section=9)] IBM JVM (1.5 기준)

- **Command Line**: java -Xgcpolicy:optthruput 과 같은 형태로 지정
- **Options File**: –Xoptionsfile 옵션을 이용해서 Option을 모아둔 Text File을 지정. Optionsfile은 다음과 같은 형태이다.

```
#My options file
-X<option1>
-X<option2>=\<value1>,\
      <value2>
-D<sysprop1>=<value1>

```

- **IBM_JAVA_OPTIONS 환경변수**: IBM_JAVA_OPTIONS 환경변수에 값을 지정(예: IBM_JAVA_OPTIONS=-X<option1> -X<option2>=<value1>)

### [[편집](http://wiki.ex-em.com/index.php?title=JVM_Options&action=edit&section=10)] Standard Options

| **Option**                 | **Description**                          |
| -------------------------- | ---------------------------------------- |
| **-memorycheck:<optiton>** | JVM 내부에서 발생하는 [Memory Leak](http://wiki.ex-em.com/index.php/Memory_Leak)을 추적하기 위한 용도로 사용된다. JVM 기술지원 엔지니어들이 사용하는 용도로 보면 정확한다. JVM 자체는 C/C++로 구현되었다. 따라서 JVM 내부에서 발생하는 [Memory Leak](http://wiki.ex-em.com/index.php/Memory_Leak)은 Java에서 발생하는 것과는 달리 진정한 의미에서는 [Memory Leak](http://wiki.ex-em.com/index.php/Memory_Leak)으로 이해할 수 있다. 다음과 같은 옵션들이 제공된다([IBM JVM Diagnositics Guide](http://publib.boulder.ibm.com/infocenter/javasdk/v5r0/topic/com.ibm.java.doc.diagnostics.50/diag/appendixes/cmdline/cmdline_general.html)에서 발췌) **all -** The default if just -memorycheck is used. Enables checking of all allocated and freed blocks on every free and allocate call. This check of the heap is the most thorough and should cause the JVM to exit on nearly all memory-related problems very soon after they are caused. This option has the greatest impact on performance.**quick -** Enables block padding only. Used to detect basic heap corruption. Pads every allocated block with sentinel bytes, which are verified on every allocate and free. Block padding is faster than the default of checking every block, but is not as effective.**nofree -** Keeps a list of already used blocks instead of freeing memory. This list is checked, along with currently allocated blocks, for memory corruption on every allocation and deallocation. Use this option to detect a dangling pointer (a pointer that is "dereferenced" after its target memory is freed). This option cannot be reliably used with long-running applications (such as WAS), because "freed" memory is never reused or released by the JVM.**failat=<number of allocations> -** Causes memory allocation to fail (return NULL) after <number of allocations>. Setting <number of allocations> to 13 will cause the 14th allocation to return NULL. Deallocations are not counted. Use this option to ensure that JVM code reliably handles allocation failures. This option is useful for checking allocation site behavior rather than setting a specific allocation limit.**skipto=<number of allocations> -** Causes the program to check only on allocations that occur after <number of allocations>. Deallocations are not counted. Used to speed up JVM startup when early allocations are not causing the memory problem. As a rough estimate, the JVM performs 250+ allocations during startup.**callsite=<number of allocations> -** Prints callsite information every <number of allocations>. Deallocations are not counted. Callsite information is presented in a table with separate information for each callsite. Statistics include the number and size of allocation and free requests since the last report, and the number of the allocation request responsible for the largest allocation from each site. Callsites are presented as sourcefile:linenumber for C code and assembly function name for assembler code. Callsites that do not provide callsite information are accumulated into an "unknown" entry.**zero -** Newly allocated blocks are set 0 instead of being filled with the 0xE7E7xxxxxxxxE7E7 pattern. Setting to 0 helps you to determine whether a callsite is expecting zeroed memory (in which case the allocation request should be followed by memset(pointer, 0, size)). |
| **-showversion**           | Java의 버전과 기본적인 사용법에 대한 정보를 제공한다.         |
| **-verbose:<option>**      | Option에 따라 상세 정보를 출력한다. 다음과 같은 옵션이 제공된다. **class - **Class Loading 정보를 Standard Err에 출력한다. 출력 예는 아래와 같다.`class load: java/io/FilePermissionclass load: java/io/FilePermissionCollectionclass load: java/security/AllPermission...class load: testclass load: test from: file:/C:/Documents/Java_Test/GC%20dump/`**dynload - **Class Loading에 대한 매우 상세한 정보를 제공한다. 클래스명, 클래스 크기, 로딩 시간등의 정보를 포함한다. 출력 예는 아래와 같다.`<  Class size 6594; ROM size 7056; debug size 0> <  Read time 128 usec; Load time 126 usec; Translate time 222 usec><Loaded java/security/BasicPermissionCollection from c:\IBM\WebSphere\AppServer\java\jre\lib\core.jar><  Class size 4143; ROM size 3264; debug size 0><  Read time 103 usec; Load time 81 usec; Translate time 117 usec><Loaded java/security/Principal from c:\IBM\WebSphere\AppServer\java\jre\lib\core.jar><  Class size 239; ROM size 248; debug size 0><  Read time 44 usec; Load time 23 usec; Translate time 20 usec><Loaded test><  Class size 370; ROM size 448; debug size 0><  Read time 0 usec; Load time 28 usec; Translate time 39 usec>`**gc - **GC 작업에 대한 정보를 제공한다. 자세한 내용은 [GC Dump](http://wiki.ex-em.com/index.php/GC_Dump)를 참조한다.**jni - **[JNI](http://wiki.ex-em.com/index.php?title=JNI&action=edit) 호출에 대한 정보를 제공한다. 출력 예는 아래와 같다.`<JNI ReleaseStringChars: buffer=41EC45B8><JNI GetStaticMethodID: gc_dump.main ([Ljava/lang/String;)V><JNI GetMethodID: java/lang/reflect/Method.getModifiers ()I><JNI GetMethodID: java/lang/String.<init> ([B)V><JNI FindClass: java/lang/Object><JNI GetMethodID: java/lang/Object.finalize ()V><JNI FindClass: java/lang/ref/Reference><JNI GetMethodID: java/lang/ref/Reference.enqueueImpl ()Z>`**sizes - **Memory 사용과 관련된 설정값을 출력한다. 출력 예는 아래와 같다.` -Xmca32K              RAM class segment increment -Xmco128K            ROM class segment increment -Xmns0K                initial new space size -Xmnx0K                maximum new space size -Xms4M                 initial memory size -Xmos4M               initial old space size -Xmox1047608K     maximum old space size -Xmx1047608K       memory maximum -Xmr16K               remembered set size -Xmso32K             OS thread stack size -Xiss2K                java thread stack initial size -Xssi16K              java thread stack increment -Xss256K             java thread stack maximum size -Xscmx16M          shared class cache size`**stacks - **Thread 별로 Java/C Stack의 사용 크기를 출력한다. 출력 예는 아래와 같다.`JVMVERB000I Verbose stack: "Thread-1" used 188/3756 bytes on Java/C stacksJVMVERB000I Verbose stack: "Thread-2" used 516/3756 bytes on Java/C stacksJVMVERB000I Verbose stack: "main" used 1368/0 bytes on Java/C stacksJVMVERB000I Verbose stack: "Finalizer thread" used 456/2308 bytes on Java/C stacksJVMVERB000I Verbose stack: "Gc Slave Thread" used 232/3060 bytes on Java/C stacks` |

### [[편집](http://wiki.ex-em.com/index.php?title=JVM_Options&action=edit&section=11)] Non-Standard Options

| **Option**                    | **Default**                              | **Description**                          |
| ----------------------------- | ---------------------------------------- | ---------------------------------------- |
| **-Xalwaysclassgc**           | -Xclassgc 옵션에 의해 결정됨                     | [Global Collection](http://wiki.ex-em.com/index.php?title=Global_Collection&action=edit)이 발생할 때 [Class GC](http://wiki.ex-em.com/index.php?title=Class_GC&action=edit)를 수행할 지의 여부를 지정한다. [classgc](http://wiki.ex-em.com/index.php/JVM_Options#classgc) 옵션과 동일한 값이며, Default는 True이다. |
| **-Xbootclasspath**           |                                          | Sun JVM의 [bootclasspath](http://wiki.ex-em.com/index.php/JVM_Options#bootclasspath) 옵션과 동일 |
| **-Xcheck:jni**               | False                                    | Sun JVM의 [check:jni](http://wiki.ex-em.com/index.php/JVM_Options#check:jni) 옵션과 동일 |
| **-Xclassgc**                 | True                                     | [Classloader](http://wiki.ex-em.com/index.php?title=Classloader&action=edit)가 변했을 때만 [Class GC](http://wiki.ex-em.com/index.php?title=Class_GC&action=edit)를 수행할 지의 여부를 결정한다. |
| **-Xcodecache<size>**         | OS/Hardware Architecture에 따라 결정됨         |                                          |
| **-Xcomp**                    |                                          | [-Xjit:count=0](http://wiki.ex-em.com/index.php/JVM_Options#jit) 옵션을 사용한 것과 동일. z/OS에서만 사용되며, deprecated 옵션이다. |
| **-Xcompactexplicitgc**       | False                                    | System.gc() 호출에 의한 [Explicit GC](http://wiki.ex-em.com/index.php/Explicit_GC)가 발생했을 경우 항상 [Compaction](http://wiki.ex-em.com/index.php?title=Compaction&action=edit)을 수행할 지의 여부를 결정한다. Sun Hotspot JVM의 경우에는 System.gc() 호출이 발생할 경우 반드시 [Full GC](http://wiki.ex-em.com/index.php?title=Full_GC&action=edit)를 수행한다. 반면 IBM JVM의 경우에는 이전 GC 작업에서 [Compaction](http://wiki.ex-em.com/index.php?title=Compaction&action=edit)이 발생하지 않은 경우에만 [Compaction](http://wiki.ex-em.com/index.php?title=Compaction&action=edit)을 수행한다. |
| **-Xcompactgc**               | False                                    | [System GC](http://wiki.ex-em.com/index.php?title=System_GC&action=edit)나 [Global GC](http://wiki.ex-em.com/index.php?title=Global_GC&action=edit)가 발생할 때마다 [Compaction](http://wiki.ex-em.com/index.php?title=Compaction&action=edit)을 수행한다. |
| **-Xconcurrentbackground**    | 1                                        | [Response Time Collector](http://wiki.ex-em.com/index.php/Garbage_Collector#Response_Time_Collector)에 서 Concurrent Mark를 수행할 Background Thread의 개수를 지정한다. Concurrent Background Thread는 Application Thread의 성능을 다소 저하시킬 수 있으므로 하나만 구동하는 것이 바람직하다. 단, Concurrent Mark 작업이 잘 진행되지 않아 문제가 생기는 경우에는 이 값을 키우는 것이 해결책이 될 수 있다. |
| **-Xconcurrentlevel<value>**  |                                          |                                          |
| **-Xconmeter<option>**        |                                          |                                          |
| **-Xdisableexcessivegc**      | False                                    | GC 작업에 지나치게 많은(Excessive) 시간이 소요된 경우에 [Out Of Memory Error](http://wiki.ex-em.com/index.php/Out_Of_Memory_Error)를 유발하지 않도록 지정한다. |
| **-Xdisableexplicitgc**       | False                                    | System.gc() 호출에 의한 GC 작업을 비활성화한다. 이 옵션을 사용하면 System.gc()를 호출하더라도 GC 작업이 발생하지 않는다. RMI에 의한 불필요한 GC 작업이나 사용자의 실수에 의한 강제적인 GC 작업을 방지하고자 하는 목적으로 사용된다. |
| **-Xdisablejavadump**         | False                                    | [Java Dump](http://wiki.ex-em.com/index.php/Java_Dump)의 생성을 비활성화시킨다. IBM JVM은 치명적인 Error나 Signal을 받으면 [Java Dump](http://wiki.ex-em.com/index.php/Java_Dump)를 생성함으로써 사후 문제를 디버깅할 수 있도록 한다. 특정 문제로 인해 지나치게 많은 Dump가 생성될 때 이 옵션을 비활성시키는 경우가 있다. |
| **-Xdisablestringconstantgc** | False                                    | [Interned String](http://wiki.ex-em.com/index.php?title=Interned_String&action=edit)에 대한 GC 작업을 비활성화한다. |
| **-Xenableexcessivegc**       | True                                     | GC 작업에 지나치게 많은(Excessive) 시간이 소요된 경우에 [Out Of Memory Error](http://wiki.ex-em.com/index.php/Out_Of_Memory_Error)를 유발하도록 지정한다. [disableexcessivegc](http://wiki.ex-em.com/index.php/JVM_Options#disableexcessivegc)와 반대의 역할을 한다. |
| **-Xenablestringconstantgc**  | True                                     | [Interned String](http://wiki.ex-em.com/index.php?title=Interned_String&action=edit)에 대한 GC 작업을 활성화한다. [disablestringconstantgc](http://wiki.ex-em.com/index.php/JVM_Options#disablestringconstantgc) 옵션과 반대의 역할을 한다. |
| **-Xgcpolicy<option>**        | optthruput                               | [Garbage Collector](http://wiki.ex-em.com/index.php/Garbage_Collector)의 종류를 결정한다. **optthruput: **[Throughput Collector](http://wiki.ex-em.com/index.php/Throughput_Collector)를 사용한다. 처리량(Throughput)을 최적화할 목적으로 사용되며, Default Collector이다.**optavgpause: **[Response Time Collector](http://wiki.ex-em.com/index.php/Response_Time_Collector)를 사용한다. 응답시간(Response Time)을 최적화할 목적으로 사용된다. GC에 의한 Pause Time을 최소하기 위해 Concurrent Mark/Sweep 작업을 수행한다. [Throughput Collector](http://wiki.ex-em.com/index.php/Throughput_Collector)에 비해 처리량으로 다소(5~10%) 떨어진다.**gencon: **[Concurrent Generational Collector](http://wiki.ex-em.com/index.php/Concurrent_Generational_Collector)를 사용한다. IBM JDK 1.5에서 추가되었다. Sun Hotspot JVM의 [CMS Collector](http://wiki.ex-em.com/index.php/CMS_Collector)와 거의 동일한 방식으로 동작하다.**subpool: **[Subpool Collector](http://wiki.ex-em.com/index.php/Subpool_Collector)를 사용한다. |
| **-Xgcthreads<value>**        | CPU#                                     | [Throughput Collector](http://wiki.ex-em.com/index.php/Throughput_Collector)는 Mark & Sweep 작업을 Parallel하게, 즉 동시에 여러 Thread를 사용해서 수행한다. 이 옵션을 통해 Parallel GC를 수행할 Thread 수를 지정한다. 기본적으로 CPU 개수를 모두 활용한다. 만일 하나의 Machine에서 여러 JVM을 구동하거나, 다른 종류의 Application과 JVM이 공존하는 경우에는 이 값을 줄임으로써 [Context Switching](http://wiki.ex-em.com/index.php?title=Context_Switching&action=edit)에 의한 성능 저하를 피할 수 있다. |
| gcworkpackets                 |                                          |                                          |
| int                           |                                          |                                          |
| iss                           |                                          |                                          |
| **-Xjit:<options>**           | True([JIT](http://wiki.ex-em.com/index.php?title=JIT&action=edit) 컴파일을 사용함) | [JIT](http://wiki.ex-em.com/index.php?title=JIT&action=edit) 컴파일 옵션을 결정한다. <options>가 지정되지 않으면 단순히 [JIT](http://wiki.ex-em.com/index.php?title=JIT&action=edit) 컴파일을 활성화한다. 이 옵션은 [JIT](http://wiki.ex-em.com/index.php?title=JIT&action=edit) 컴파일러의 버그로 인해 JVM 장애에 대해 Workaround로 많이 활용된다. [JIT](http://wiki.ex-em.com/index.php?title=JIT&action=edit) 컴파일러의 버그에 의한 JVM Crash가 발생할 경우에는 다음과 같은 유형의 Error Stacktrace가 기록된다. `...TR_GlobalRegisterAllocator::perform()TR_OptimizerImpl::performOptimization()TR_OptimizerImpl::performOptimization()TR_OptimizerImpl::optimize()...`이 경우에는 다음과 같은 옵션을 이용해서 [JIT](http://wiki.ex-em.com/index.php?title=JIT&action=edit) 컴파일을 제어할 수 있다. `# Inlining을 비활성화한다.-Xjit:**disableInlining**-Xjit:{java/lang/Math.max(II)I}(**disableInlining**)# 특정 메소드를 Optimization에서 제외한다.-Xjit:**exclude**={java/lang/Math.max(II)I} ...`아래 옵션들은 [JIT](http://wiki.ex-em.com/index.php?title=JIT&action=edit) 컴파일러에 문제가 발생한 경우 이를 좀 더 쉽제 추적하고자 할 때 사용된다. `**count=<n>**   <n>번 째 수행에서 Method를 JIT 컴파일한다. JIT 문제를 추적할 때는 "0"의 값을 사용함으로써 보다 빨리 컴파일이 이루어지도록 한다.    **optlevel**=[noOpt | cold | warm | hot | veryHot | scorching]   [비최적화 ~ 최고 최적화]까지 JIT 컴파일에 의한 최적화 레벨을 지적한다.**verbose**   JIT 컴파일 과정에서 사용된 정보와 컴파일 과정을 출력한다.`아래에 **-Xjit:verbose** 옵션의 출력 예가 있다. count 값은 1000, optlevel 값은 warm이 기본값임을 알 수 있다. `JIT type: Testarossa (Full)JIT options specified:    verboseoptions in effect:    bcount=250    catchSamplingSizeThreshold=1100    classLoadPhaseInterval=500    classLoadPhaseThreshold=155    code=512 (KB)    codepad=0 (KB)    codetotal=0 (KB)    **count=1000**    ...    stack=256    target=ia32-win32    verbose=1 + **(warm)** java/lang/Double.doubleToRawLongBits(D)J @ 0x41630014-0x41630030+ (warm) java/lang/System.getEncoding(I)Ljava/lang/String; @ 0x41630054-0x41630145+ (warm) java/lang/String.hashCode()I @ 0x4163017C-0x4163024A+ (warm) java/util/HashMap.put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object; @ 0x4163027C-0x416304AF+ (warm) java/util/Locale.toLowerCase(Ljava/lang/String;)Ljava/lang/String; @ 0x416304DC-0x416307FF...+ (warm) java/io/FileOutputStream.writeBytes([BIILjava/io/FileDescriptor;)V @ 0x41636C34-0x41636D45|-` |
| loainitial                    |                                          |                                          |
| loamaximum                    |                                          |                                          |
| loaminimum                    |                                          |                                          |
| lp                            |                                          |                                          |
| maxe                          |                                          |                                          |
| maxf                          |                                          |                                          |
| maxt                          |                                          |                                          |
| mca                           |                                          |                                          |
| mco                           |                                          |                                          |
| mine                          |                                          |                                          |
| minf                          |                                          |                                          |
| mint                          |                                          |                                          |
| mn                            |                                          |                                          |
| mns                           |                                          |                                          |
| mnx                           |                                          |                                          |
| mo                            |                                          |                                          |
| moi                           |                                          |                                          |
| mos                           |                                          |                                          |
| mox                           |                                          |                                          |
| mr                            |                                          |                                          |
| mrx                           |                                          |                                          |
| ms                            |                                          |                                          |
| mso                           |                                          |                                          |
| mx                            |                                          |                                          |
| noaot                         |                                          |                                          |
| noclassgc                     |                                          |                                          |
| nocompactexplicitgc           |                                          |                                          |
| nocompactgc                   |                                          |                                          |
| **-Xnojit**                   | False                                    | [JIT](http://wiki.ex-em.com/index.php?title=JIT&action=edit) 컴파일 옵션을 사용하지 않는다. |
| noloa                         |                                          |                                          |
| nopartialcompactgc            |                                          |                                          |
| nosigcatch                    |                                          |                                          |
| nosigchain                    |                                          |                                          |
| optionsfile                   |                                          |                                          |
| oss                           |                                          |                                          |
| partialcompactgc              |                                          |                                          |
| quickstart                    |                                          |                                          |
| realtime                      |                                          |                                          |
| rs                            |                                          |                                          |
| runhprof                      |                                          |                                          |
| samplingExpirationTime        |                                          |                                          |
| scmx                          |                                          |                                          |
| shareclasses                  |                                          |                                          |
| sigcatch                      |                                          |                                          |
| sigchain                      |                                          |                                          |
| softrefthreshold<number>      |                                          |                                          |
| ss                            |                                          |                                          |
| ssi                           |                                          |                                          |
| thr                           |                                          |                                          |
| verbosegclog:<file>           |                                          |                                          |

출처 - http://hieehee.blogspot.kr/2015/02/tomcat.html