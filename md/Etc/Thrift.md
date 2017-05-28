# Apache Thrift

* 2007년에 Facebook 내부에서 개발되어 아파치 인큐베이터에서 오픈 아파치 프로젝트가 됨.
* 차세대 프로토콜 버퍼가 되는 것이 목표.
* IDL 문법은 프로토콜 버퍼보다 좀 더 깔끔함.
* 프로토콜 버퍼보다 많은 언어 지원.
* RPC 호출을 위한 stack 제공.

## 실행

* IDL 파일을 작성하여 다음과 같은 명령으로 수행
  * thrift --gen java MyProject.thrift
  * 생성된 파일은 수천 라인의 코드로 이루어지지만 읽을 수 있는 수준이다.
  * 생성된 파일을 수정하면 안됨.
* thrift를 다운 받으면 각 플랫폼 별로 라이브러리 파일을 제공.
  * ant를 수행해야 함.
  * 자바의 경우 lib\java 디렉토리로 이동하여 "ant" 명령을 수행하면 .jar 파일이 생성된다.

## Types

### Structs

* OOP 언어에서의 class와 동등한 의미를 가지지만 상속은 허용하지 않는다. 
* struct는 타입을 갖는 유니크한 이름의 필드들을 가진다.

### Services

* service는 Thrift의 타입 체계를 사용하여 정의된다.
* OOP 언어의 interface와 동등한 의미를 가진다.
* Thrift 컴파일러는 이 인터페이스를 구현한 client/server stubs을 generate한다.

## IDL

* IDL의 "=1" 또는 "1:"의 표시는 각각의 요소들을 식별하기위해 바이너리 인코딩에서 사용되는 유니크한 tag 필드이다.
* 이 태그들은 중요하기 때문에 변경되지 말아야 한다.
* 자바를 예로 들면 만들어지는 class는 getter와 setter 메서드들을 가진다.

## 프로토콜 버퍼와 차이점

* 단순한 타입 (bool, byte, 16/32/64-bit integers, double, string)
* Container 제공 (list, set, map)
* const 사용 가능
* Exception 처리 가능
* TBinaryProtocol : 효과적인 공간을 위한 최적화가 되지 않았다. 텍스트 프로토콜보다 처리가 빠르지만 디버깅이 더 어렵다.
* TCompactProtocol : 더 축소되고 처리 속도 또한 더 빠른 바이너리 형태.
  * 근소한 차이로 프로토콜 버퍼 사이즈가 더 작음.
* 테스트 시나리오를 만번 수행한 결과. CPU 사용률과 처리 소요시간이 프로토콜 버퍼보다 효율적이었다.
  * CPU 사용률
    * Thrift : 30%
    * Protocol Buffer : 30%
  * 소요시간
    * Thrift : 1:05.12
    * Protocol Buffer : 1:19.48
* Thrift는 Facebook, Cassandra, Haddop, HBase 등에서 사용됨
* Protocol Buffer는 Google, ActiveMQ, Netty 등에서 사용됨.
* RPC 구현이 포함되어있음.
* documentation 제공이 Thrift보다는 Protocolbuffer가 잘 되어 있음.
  * Thrift보다 API가 조금 더 깔끔함.
* 좋은 예제를 찾기 힘듬.

## Versioning

* 시스템은 이전 버전의 데이터를 읽을 수 있도록 지원해야 하고 새로운 버전의 서버들과 지난 버전의 클라이언트간의 요청 또한 처리할 수 있어야 한다.
* 필드 식별자와 타입 지정자의 조합은 필드를 식별할 수 있게 한다.
* version이 맞지 않는 경우는 네가지 정도가 있다.
  * 필드가 새로 추가된 경우 이전 버전의 클라이언트와 새 버전의 서버간 통신.
  * 필드가 제거된 경우 이전 버전의 클라이언트와 새 버전의 서버간 통신.
  * 필드가 새로 추가된 경우 새 버전의 클라이언트와 이전 버전의 서버간 통신.
  * 필드가 제거된 경우 새 버전의 클라이언트와 이전 버전의 서버간 통신.
* 이전 버전의 클라이언트가 새 버전의 서버로 메세지를 전송하면 새로운 서버는 필드가 셋팅되지 않은 것을 알아채고 기본 값을 셋팅한다.
* 새 버전의 클라이언트가 이전 버전의 서버로 메세지를 전송하면 서버는 해당 필드를 무시한다.

## 예제 (RPC 사용)

### .thrift 파일 작성

```
namespace java tutorial

typedef i32 int
service MultiplicationService
{
        string print(1:int n1, 2:int n2),
}
```

### 서버 코드

```java
public class MultiplicationServer {
    public static MultiplicationServiceHandler handler;
    public static MultiplicationService.Processor processor;

    public static void main(String[] args) {
        try {
            handler = new MultiplicationServiceHandler();
            processor = new MultiplicationService.Processor(handler);

            Runnable simple = new Runnable() {
                @Override
                public void run() {
                    simple(processor);
                }
            };

            new Thread(simple).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void simple(MultiplicationService.Processor processor) {
        try {
            TServerTransport serverTransport = new TServerSocket(9091);
            TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));

            System.out.println("Starting the simple server ...");
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
```

```java
public class MultiplicationServiceHandler implements MultiplicationService.Iface{
    @Override
    public String multiply(int n1, int n2) throws TException {
        return "success";
    }
}
```

* 클라이언트로 부터 요청이 오면 MultiplicationService.Iface를 구현한 Handler의 함수가 수행되어 결과가 클라이언트로 반환된다.

### 클라이언트 코드

```java
public class MultiplicationClient {
    public static void main(String[] args) {
        try {
            TTransport transport = new TSocket("localhost", 9091);
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            MultiplicationService.Client client = new MultiplicationService.Client(protocol);

            perform(client);

            transport.close();
        } catch(TException e) {
            e.printStackTrace();
        }
    }

    private static void perform(MultiplicationService.Client client) throws TException {
        System.out.println(client.print(3,5));
    }
}
```

* 서버 연결 후 MultiplicationService.Client 인스턴스를 이용하여 서버와 통신한다.

## Avro

* schema 기반 시스템.
* Avro 데이터는 항상 스키마로 serialize 된다. Avro 데이터가 파일에 저장될 때 스키마도 함께 저장된다.
* Thrift / Protocol Buffer 와의 차이점
  * 다이나믹 스키마를 제공.
  * 하둡 안에 내장되어 있음.
  * JSON 스키마 사용.
  * 컴파일이 필요 없음.
  * ID들에 대한 정의가 필요 없음.
* 제공 type
  * Primitive : null, boolean, int, long, float, double, bytes, string
  * Complex : records, enums, arrays, maps, unions, fixed
* 코드가 generate 될 것을 요구하지 않는다. Data는 항상 스키마를 동반한다.


## 자료수집이 더 필요한 부분

* thrift의 class를 사용하지 않고 클라이언트와 서버 구현.
* RPC를 사용하지 않고 통신.
* 효율적인 클래스 관리 방법.
* Avro에 대한 자료와 예제.

## 참고

* [pb vs thrift vs avro]
* [thrift 예제]
* [아파치 쓰리프트 slide share]


[아파치 쓰리프트 slide share]: http://www.slideshare.net/mrg7211/apache-thrift-51044563
[thrift 예제]: http://thrift-tutorial.readthedocs.io/en/latest/usage-example.html
[pb vs thrift vs avro]: http://www.slideshare.net/IgorAnishchenko/pb-vs-thrift-vs-avro