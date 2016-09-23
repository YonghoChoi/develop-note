# netty

## handler 등록 시 주의 사항

* 하나의 이벤트는 하나의 이벤트 메서드만 수행한다. 만약 두 번째 이벤트 핸들러의 동일 이벤트 메서드를 수행하고 싶다면 처음 호출된 이벤트 핸들러에서 fire가 붙은 메서드를 수행해야 한다. 
* 예를들어 FirstHandler와 SecondHandler에서 ChannelRead 메서드를 구현했다면 FirstHandler에 의해 SecondHandler의 ChannelRead 메서드가 가려진다. SecondHandler의 ChannelRead 메서드를 호출해야 한다면 FirstHander의 ChannelRead 메서드에서 ChannelHandlerContext의 fireChannelRead 메서드를 호출해줘야한다.

  ```java
public class FirstHandler extends ChannelInboundHandlerAdapter...
    @Override
    public void ChannelRead(ChannelHandlerContext ctx, Object msg){
        ...
        ctx.write(msg);
        ctx.fireChannelRead(msg);
    }
    ```

## Codec

### 인코더와 디코더

* 데이터를 전송할 때는 인코더를 사용하여 패킷으로 변환하고, 데이터를 수신할 때는 디코더를 사용하여 패킷을 데이터로 변환한다.
* 패킷을 수신 받는 ChannelInboundHandler에서 디코딩을 수행하고, 패킷을 송신하는 ChannelOutboundHandler에서 인코딩을 수행한다.

### codec 실행 과정

write 이벤트 발생 -> MessageToMessageEncoder의 write 메서드 수행 -> write 메서드 내의 encode 메서드 수행

* netty에서 제공되는 Encoder들은 MessageToMessageEncoder를 확장한다.
* write 이벤트가 발생하면 MessageToMessageEncoder의 write 메서드가 호출되는데 이 클래스를 확장한 클래스들에서 encode 메서드를 오버라이드하면 write 메서드에서 encode 메서드가 호출되어 수행된다. (Template Method 패턴)

### 기본 제공 코덱

* base64 : 8비트 이진 데이터를 문자 코드에 영향을 받지 않는 공통 ASCII 영역의 문자로 이루어진 일련의 문자열로 바꾸는 인코딩.
* bytes : 바이트 배열 데이터에 대한 송수신을 지원.
* compression : 송수신 데이터의 압축을 지원.
* http : HTTP 프로토콜을 지원. 하위 패키지에서 다양한 데이터 송수신 방법을 지원.
  * cors 코덱 : 현재 접속한 웹 서비스 도메인에서 다른 도메인에 존재하는 URI 호출을 허용.
  * multipart 코덱 : 파일 송수신 지원.
  * websocketx 코덱 : 웹 소켓 프로토콜의 데이터 송수신 지원.
* marshalling : 객체를 네트워크를 통해서 송수신 지원. (JBoss 라이브러리 사용)
* protobuf : 구글의 프로토콜 버퍼 지원.
* rtsp(Real Time Streaming Protocol) : 오디오와 비디오 같은 실시간 데이터 전달.
* sctp : TCP가 아닌 SCTP 전송 계층을 사용.
* spdy : 구글의 SPDY 프로토콜 지원.
* string : 문자열의 송수신 지원.
* serialization : 자바의 객체를 네트워크로 전송할 수 있도록 직렬화와 역직렬화 지원.

## 이벤트 루프

이벤트를 실행하기 위한 무한루프 스레드.

* 이벤트 루프가 지원하는 스레드 종류에 따라
  * 단일 스레드 이벤트 루프
  * 다중 스레드 이벤트 루프

* 이벤트 루프가 처리한 이벤트의 결과를 돌려주는 방식에 따라
  * 콜백 패턴
  * 퓨처 패턴

### 두가지 처리 방법

* 이벤트 리스너와 이벤트 처리 스레드에 기반한 방법.
  * 이벤트를 처리하는 로직을 가진 이벤트 메서드를 대상 객체의 이벤트 리스너에 등록.
  * 객체에 이벤트가 발생했을 때 이벤트 처리 스레드에서 등록된 메서드를 수행.
  * 이벤트 처리 스레드는 대부분 단일 스레드로 구현.

* 이벤트 큐에 이벤트를 등록하고 이벤트 루프가 이벤트 큐에 접근하여 처리하는 방법
  * 이벤트 루프가 다중 스레드일 때 이벤트 큐는 여러 개의 스레드에서 공유하게 된다.
  * 가장 먼저 이벤트 큐에 접근한 스레드가 첫번째 이벤트를 가져와서 이벤트를 수행.

### 네티의 이벤트 루프

* 단일/다중 스레드 이벤트를 모두 사용 가능.
* 다중 스레드 이벤트 루프의 경우 이벤트의 발생 순서와 실행 순서가 일치하지 않을 수 있다.
* 네티에서는 이벤트 루프의 종류에 상관없이 이벤트 발생순서에 따른 실행 순서를 보장한다.
  * 네티의 이벤트는 채널에서 발생.
  * 이벤트 루프 객체는 이벤트 큐를 가짐.
  * 네티의 채널은 하나의 이벤트 루프에 등록.
  * 결과적으로 채널에서 발생한 이벤트는 항상 동일한 이벤트 루프 스레드에서 처리하여 이벤트 발생 순서와 처리 순서가 일치된다.

## Inbound handler

* HttpRequestDecode
  * netty-codec-http 라이브러리에 속한 핸들러.
  * HTTP 프로토콜의 데이터를 HttpRequest, HttpContent, LastHttpContent의 순서로 디코딩하여 FullHttpMessage 객체로 만들고 인바운드 이벤트를 발생시킨다. FullHttpMessage 인터페이스는 HttpRequest, HttpMessage, HttpContent의 최상위 인터페이스.
* HttpObjectAggregator



## Collections

* RecyclableArrayList : 