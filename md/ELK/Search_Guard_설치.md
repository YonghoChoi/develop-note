## elasticsearch.yml 설정

```yaml
http.host: 0.0.0.0

cluster.name: mycluster
node.master: true
node.data: true
path.logs: .
network.publish_host: 0.0.0.0
network.bind_host: "0.0.0.0"
searchguard.ssl.transport.enabled: true
searchguard.ssl.transport.keystore_filepath: /usr/share/elasticsearch/config/keystore.jks
searchguard.ssl.transport.keystore_password: changeit
searchguard.ssl.transport.truststore_filepath: /usr/share/elasticsearch/config/truststore.jks
searchguard.ssl.transport.truststore_password: changeit
searchguard.ssl.transport.enforce_hostname_verification: false
searchguard.ssl.http.clientauth_mode: REQUIRE
searchguard.ssl.http.enabled: true
searchguard.ssl.http.keystore_filepath: /usr/share/elasticsearch/config/keystore.jks
searchguard.ssl.http.keystore_password: changeit
searchguard.ssl.http.truststore_filepath: /usr/share/elasticsearch/config/truststore.jks
searchguard.ssl.http.truststore_password: changeit
searchguard.authcz.admin_dn:
                  - "CN=yonghochoi, OU=vinusent, O=server, L=Samsung, ST=Seoul, C=16201"
```

* keystore/truststore 를 직접 생성하지 않고 이미 생성되어 있는 것을 사용하는 경우 아래 명령으로 인증 정보 확인

  ```shell
  $ keytool -list -v -keystore kirk.jks
  ```

  * install_demo_configuration.sh를 통해 생성된 kirk.jks 파일의 패스워드는 `changeit`





## keystore/truststore 생성

자바에 포함된 보완관련 어플리케이션인 keytool을 사용

1. keystore 생성 (인증서)

   ```shell
   $ keytool -genkey -alias hive-key -keyalg RSA -keypass changeit -storepass changeit -keystore keystore.jks
   ```

2. 위에서 생성한 keystore.jks 인증서를 server.cer 파일로 내보냄

   ```shell
   $ keytool -export -alias hive-key -storepass changeit -file server.cer -keystore keystore.jks
   ```

3. truststore 파일을 만들고 인증서를 truststore에 추가

   ```shell
   $ keytool -import -v -trustcacerts -file server.cer -keystore truststore.jks -keypass changeit
   ```

4. keystore.jks와 trusstore.jks 생성 완료

   ```
   Owner: CN=yongho, OU=vinus, O=server, L=Seoul, ST=Samsung, C=16201
   Issuer: CN=yongho, OU=vinus, O=server, L=Seoul, ST=Samsung, C=16201
   Serial number: 3ee66345
   Valid from: Thu Jul 13 05:27:44 UTC 2017 until: Wed Oct 11 05:27:44 UTC 2017
   Certificate fingerprints:
            MD5:  58:DD:2D:D2:11:93:8C:E4:EF:96:4C:0F:8D:04:52:96
            SHA1: 93:5F:BD:68:04:6C:E6:CC:7A:DC:08:79:0D:EB:B4:C3:69:38:EF:A1
            SHA256: 57:FB:66:40:47:A0:6B:1A:96:4B:91:31:60:6C:51:F7:7E:2B:04:29:9A:EE:EE:8E:AB:5A:23:43:F3:4B:E4:83
            Signature algorithm name: SHA1withDSA
            Version: 3

   Extensions:

   #1: ObjectId: 2.5.29.14 Criticality=false
   SubjectKeyIdentifier [
   KeyIdentifier [
   0000: 33 4D 88 F3 C9 AC 12 DF   3F 92 1D 96 50 9C 36 2E  3M......?...P.6.
   0010: E4 11 2E 51                                        ...Q
   ]
   ]
   ```

   ​

## Elasticsearch에 Search Guard 설치

1. install 명령

   ```shell
   $ bin/elasticsearch-plugin install -b com.floragunn:search-guard-5:5.4.3-14
   ```

   - 버전은 아래 버전표 참고

2. tools 디렉토리로 이동

   ```shell
   $ cd <Elasticsearch directory>/plugins/search-guard-<version>/tools
   ```

3. install_demo_configuration.sh 실행

   ```shell
   $ ./install_demo_configuration.sh
   ```

   - 실행권한이 없는 경우 `chmod +x install_demo_configuration.sh`
   - 실행 후 truststore와 두 개의 keystore 파일이 생성됨
     - `<Elasticsearch directory>/config` 디렉토리에 생성됨
   - truststore.jks : root CA와 intermediate/signing CA
   - keystore.jks : 노드 인증서(node certificate)

4. sgadmin_demo.sh 실행

   ```shell
   $ ./sgadmin_demo.sh
   ```

   - 실행 후 설정 파일들이 `plugins/search-guard-<version>/sgconfig` 디렉토리에 생성됨



## Kibana에 Search Guard 설치

1. 플러그인 [다운로드](https://github.com/floragunncom/search-guard-kibana-plugin/releases)

   ```shell
   $ wget https://github.com/floragunncom/search-guard-kibana-plugin/releases/download/v5.4.2-3/searchguard-kibana-5.4.2-3.zip
   ```

2. 플러그인 설치

   ```shell
   $ bin/kibana-plugin install file:///usr/share/kibana/temp/searchguard-kibana-5.4.2-3.zip
   ```

3. /etc/kibana/kibana.yml 설정

   ```yaml
   ... 생략 ...
   elasticsearch.username: "kibanaserver"
   elasticsearch.password: "kibanaserver"

   ... 생략 ...
   elasticsearch.ssl.verificationMode: none

   ... 생략 ...
   ```

   * username과 password는 elasticsearch 쪽에 sgconfig/sg_internal_user.xml 파일에 지정된 계정을 사용
   * 사설 인증서를 사용하지 않으므로 verificationMode는 none으로 설정



## 트러블슈팅

### zen_unicast 관련 에러

```
[2017-07-13T06:04:03,175][ERROR][c.f.s.t.SearchGuardRequestHandler] Error validating headers
[2017-07-13T06:04:03,191][WARN ][o.e.d.z.UnicastZenPing   ] [vOvmwD5] [1] failed send ping to {#zen_unicast_127.0.0.1:9300_0#}{UV7v4BgFTaSO3E3F6Ng2DQ}{127.0.0.1}{127.0.0.1:9300}
java.lang.IllegalStateException: handshake failed with {#zen_unicast_127.0.0.1:9300_0#}{UV7v4BgFTaSO3E3F6Ng2DQ}{127.0.0.1}{127.0.0.1:9300}
        at org.elasticsearch.transport.TransportService.handshake(TransportService.java:386) ~[elasticsearch-5.4.3.jar:5.4.3]
        at org.elasticsearch.transport.TransportService.handshake(TransportService.java:353) ~[elasticsearch-5.4.3.jar:5.4.3]
        at org.elasticsearch.discovery.zen.UnicastZenPing$PingingRound.getOrConnect(UnicastZenPing.java:401) ~[elasticsearch-5.4.3.jar:5.4.3]
        at org.elasticsearch.discovery.zen.UnicastZenPing$3.doRun(UnicastZenPing.java:508) [elasticsearch-5.4.3.jar:5.4.3]
        at org.elasticsearch.common.util.concurrent.ThreadContext$ContextPreservingAbstractRunnable.doRun(ThreadContext.java:638) [elasticsearch-5.4.3.jar:5.4.3]
        at org.elasticsearch.common.util.concurrent.AbstractRunnable.run(AbstractRunnable.java:37) [elasticsearch-5.4.3.jar:5.4.3]
        at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142) [?:1.8.0_131]
        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617) [?:1.8.0_131]
        at java.lang.Thread.run(Thread.java:748) [?:1.8.0_131]

```

* conf/elasticsearch.yml 설정에서 network.publish_host와 network.bind_host가 모두 설정되어 있는 경우 network.publish_host 설정을 제거하면 해결됨

  ```yaml
  cluster.name: mycluster
  node.master: true
  node.data: true
  discovery.zen.ping.multicast.enabled: false
  number_of_nodes : 1
  minimum_master_nodes: 1
  path.logs: .
  node.name: ElasticStack.0.0.0.0
  # network.publish_host: 0.0.0.0
  network.bind_host: "0.0.0.0"
  ... 생략 ...
  ```

* 참고

  * https://discuss.elastic.co/t/elasticsearch-failed-to-send-ping-error/55125/4



### Someone (/172.19.0.8:38490) speaks http plaintext instead of ssl, will close the channel

* Search Guard 사용 시 http 통신 또한 ssl로 사용하도록 설정해둔 경우 기존에 http를 사용하여 logstash나 kibana를 사용하고 있었기 때문에 이 통신이 https가 아니라는 오류가 출력된다.

* logstash나 kibana에 https로 통신을 하도록 설정하던가 https 통신을 사용하지 않도록 설정하도록 함.

* 여기서는 https 통신을 사용하지 않는 것으로 설정

* conf/elasticsearch.yml 파일 수정

  ```yaml
  ... 생략 ...
  searchguard.ssl.http.enabled: false
  searchguard.ssl.http.keystore_filepath: /usr/share/elasticsearch/config/keystore.jks
  searchguard.ssl.http.keystore_password: changeit
  searchguard.ssl.http.truststore_filepath: /usr/share/elasticsearch/config/truststore.jks
  searchguard.ssl.http.truststore_password: changeit
  ... 생략 ...
  ```

  * searchguard.ssl.http.enabled을 false로 설정







## 참고

* [Search Guard 구축 가이드](http://www.popit.kr/search-guard%EB%A1%9C-es-%ED%82%A4%EB%B0%94%EB%82%98-%EC%9D%B8%EC%A6%9D-%EA%B5%AC%EC%B6%95/)

* [Search Guard 버전표](https://github.com/floragunncom/search-guard/wiki)

* [Search Guard 다운로드](https://oss.sonatype.org/content/repositories/releases/com/floragunn/search-guard-5/)

* [keystore/truststore 생성](https://docs.oracle.com/cd/E19159-01/820-4605/ablrb/index.html)

* [Secure Guard 설정 참고](https://github.com/floragunncom/search-guard/issues/136)

* [SSL 이론 설명](http://btsweet.blogspot.kr/2014/06/tls-ssl.html)

  ​