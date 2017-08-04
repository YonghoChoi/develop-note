# 로드밸런서 구성

* 기본적으로 포트 80으로 HTTP 트래픽을 수신
  * 환경 내의 각 인스턴스 별 동일한 포트로 트래픽을 분산
* 443 포트와 TLS 인증서를 사용하여 보안 연결 적용 가능
* ELB는 상태 검사를 통해 EC2 인스턴스가 정상적인지 여부를 확인
  * 지정된 간격으로 지정된 URL에 요청하여 상태 확인
* ELB는 라운드로빈 방식으로 트래픽을 분배
* Connection Draining : 오토 스케일링이 사용자의 요청을 처리 중인 EC2 인스턴스를 바로 삭제하지 못하도록 방지하는 기능
  * 사용자 수가 줄어들면 오토 스케일링이 EC2 인스턴스를 삭제하는데 사용자가 해당 EC2 인스턴스에서 어떠한 작업을 수행 중이라면 바로 삭제하지 않고 요청을 처리할 수 있도록 지정한 시간만큼 대기
  * 기다리는 동안에는 새로운 커넥션을 받지 않음.
* Sticky Sessions : 사용자의 세션을 확인하여 적절한 EC2 인스턴스로 트래픽을 분배.
  * Http 쿠키를 이용한 세션
  * L7 로드밸런싱 기능
  * 예를 들어 동일한 사용자가 서비스에 계속 접속하는 경우 처음 접속했던 EC2 인스턴스에 연결시켜 줌
  * 이 기능을 사용하지 않으면 라운드 로빈 방식에 의해 EC2 인스턴스에 연결됨
* Latency : ELB와 EC2 인스턴스 간의 지연시간
* HTTP 2XX, 4XX, 5XX: EC2 인스턴스에서 리턴한 HTTP Response Code
* ELB HTTP 4XX, 5XX: ELB 로드 밸런서에서 리턴한 HTTP Error Code
* Surge Queue Length: ELB 로드 밸런서에서 EC2 인스턴스로 전달되지 못하고 큐에 남아있는 요청의 개수
* Spillover Count: 서지 큐가 꽉 차서 ELB 로드 밸런서가 거부한 요청의 개수
* 트래픽이 급격히 늘어날 것으로 예상되면 Pre-warming을 요청하여 미리 ELB의 처리량을 늘릴 수 있음.
  * [기술 지원 문의]([https://aws.amazon.com/support/createCase/?type=technical_support](https://aws.amazon.com/support/createCase/?type=technical_support)



## 참고

* [부하 분산과 고가용성을 제공하는 ELB](http://pyrasis.com/book/TheArtOfAmazonWebServices/Chapter18)