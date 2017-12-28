# CloudFront로 Dynamic Contents를 전달하는 이유

* TTL 값을 0으로
  * 바로 Expire되고 항상 Request가 backend로 전달됨
* CloudFront의 Edge와 VPC Region 간에 최적화된 Network Peering을 통해 전송되므로 빠름
* CloudFront는 기본적으로 L7 로드 밸런서
* ELB에서 Cloudfront로의 Outboud 트래픽 비용은 무료
* CloudFront에서 Client로의 Outbound 트래픽 비용이 ELB에서 나가는 Outbound 트래픽 비용보다 저렴
* CloudFront의 IP Range가 존재하므로 ELB에서 Security Group에 의해 CloudFront의 트래픽만 받도록 설정할 수 있음.
  * CloudFront IP Range는 public 이므로 변경될 수 있음.
  * 변경 시에는 CloudWatch에 의해 알람이 전달되므로 이를 활용하여 Lambda로 ELB 보안 그룹 적용을 자동화 할 수 있음.
* Origin은 CloudFront에서 배포할 파일을 가져올 HTTP 서버 또는 S3 버킷을 의미
* SNI(Server Name Indication) : tcp 통신을 수행 시 핸드세이크 과정을 거치는데 이 때 핸드세이크 과정의 시작점에서 웹 브라우저에게 호스트명을 정해주는 것. 이를 통해 동일 서버에 여러개의 SSL 통신이 가능해진다.
  * 웹 서버에 브라우저가 HTTPS 접속을 시도하게 되면 SSL/TLS 접속을 하게 되는데 브라우저는 피싱사이트가 아닌 진짜 사이트에 접속된 것인지 여부를 알 수 있도록 서버에서 SSL 인증서를 가져오게 된다. 
  * 브라우저는 가져온 인증서를 가지고 접속하고자하는 도메인 주소와 인증서의 이름을 비교하여 일치하는 경우 보안접속이 이루어진다.