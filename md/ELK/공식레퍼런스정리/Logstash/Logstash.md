# Logstash

Logstash는 오픈소스 서버측 데이터 처리 파이프라인으로, 다양한 소스에서 동시에 데이터를 수집(Ingest)하여 변환한 후 자주 사용하는 "스태쉬(Stash)-보관소"로 보냅니다. 

데이터는 여러 시스템에 다양한 모습으로 산발적으로 보관된 경우가 많습니다. Logstash는 [다양한 입력](https://www.elastic.co/guide/en/logstash/current/input-plugins.html)을 지원하여 여러 공통 소스에서 이벤트들로 모두 동시에 가져옵니다. 모든 로그, 메트릭, 웹 애플리케이션, 데이터 저장소 및 다양한 AWS 서비스를 연속 스트리밍 형태로 간편하게 수집합니다.

데이터가 소스에서 저장소로 이동함에 따라 [Logstash 필터](https://www.elastic.co/guide/en/logstash/current/filter-plugins.html)는 각 이벤트를 구문 분석하고, 명명된 필드를 식별하여 구조를 구성하고, 이를 공통 형식으로 변환 통합하여 분석을 쉽게 만들고 시간을 단축할 뿐만 아니라 비즈니스 가치를 높여줍니다.

Logstash는 형식이나 복잡성에 상관 없이 다음과 같이 데이터를 동적으로 변환하고 준비합니다.

- grok을 통해 비정형 데이터에서 구조 도출
- IP 주소에서 위치 좌표 해독
- PII 데이터를 익명화하고, 민감한 정보 필드를 완전히 제외
- 데이터 소스, 형태 또는 스키마의 전체의 용이한 처리

Elasticsearch가 검색 및 분석 가능성의 세계를 열어주는 시스템이기는 하지만, 우리의 유일한 목적 저장소는 아닙니다. Logstash에는 [원하는 곳](https://www.elastic.co/guide/en/logstash/current/output-plugins.html)으로 데이터를 라우팅할 수 있는 다양한 출력이 있어, 다수의 저장소로 다운스트림 할 수 있는 유연성이 제공됩니다.

Logstash에는 200개 이상의 플러그인으로 확장 가능한 플러그인 프레임워크가 있습니다. 다양한 입력, 필터 및 출력을 섞고 조율하여 파이프라인에서 조화를 이루도록 할 수 있습니다. 별도의 애플리케이션으로부터 수집이 필요한가요? 필요한 플러그인이 없나요? Logstash 플러그인을 간단하게 빌드 할 수 있습니다. 플러그인 개발을 위한 뛰어난 API와 플러그인 생성기를 제공해 드리므로 [고유한 플러그인을 생성하고 공유](https://www.elastic.co/guide/en/logstash/current/contributing-to-logstash.html)하실 수 있습니다.

Logstash 노드가 다운되면 Logstash는 Persistent Queue를 사용하여 처리중인 이벤트에 대한 최소한 1회의 전송을 보장합니다. 성공적으로 처리되지 않은 이벤트는 검증 및 재전송을 위해 dead letter queue로 분류 될 수 있습니다. Logstash는 유동적으로 수집량 처리가 가능하여 수집중인 데이터량이 급증하였을 때도 별도의 외부 큐 프로그램 없이 정상적인 동작이 가능합니다.

Logstash 파이프라인은 보통 다양한 목적으로 쓰이며 복잡해 질 수 있어, 파이프라인 성능, 가용성 그리고 병목현상에 대한 깊은 이해가 필요합니다. [X-Pack의 Monitoring 기능](https://www.elastic.co/kr/products/x-pack/monitoring)을 통해 작동중인 Logstash 노드와 배포 시스템 전체를 쉽게 관찰하고 모니터링 할 수 있습니다.

실행중인 Logstash 인스턴스가 10개든 1000개든간에, 이제 우리는 수집 파이프라인을 완벽하게 보호할 수 있습니다. [Beats](https://www.elastic.co/kr/products/beats)로 수집되어 네트워크를 통해 들어오는 데이터는 모두 암호화되며 [Elasticsearch 클러스터의 보안](https://www.elastic.co/kr/products/x-pack/security) 기능과 완전히 통합됩니다.



## [Logstash 소개](https://www.elastic.co/guide/en/logstash/current/introduction.html)

Logstash는 실시간 파이프 라이닝 기능을 갖춘 오픈 소스 데이터 수집 엔진입니다. Logstash는 서로 다른 소스의 데이터를 동적으로 통합하고 원하는 목적지로 데이터를 표준화(normalize) 할 수 있습니다. 다양한 고급 다운 스트림 분석 및 시각화 사용 사례에 대한 모든 데이터를 정화(Cleanse)하고 민주화(democratize)하십시오.

Logstash는 원래 로그 수집에 혁신을 가져 왔지만 그 기능은 그 사용 사례를 훨씬 뛰어 넘습니다. 모든 유형의 이벤트는 광범위한 입력, 필터 및 출력 플러그인으로 풍부 해지고 변형 될 수 있으며 많은 원시 코덱을 사용하여 처리 프로세스를 더 단순화합니다. Logstash는 더 많은 양의 다양한 데이터를 활용하여 통찰력을 가속화합니다.



### Logstash의 힘

**Elasticsearch 및 더 많은 것을 위한 ingestion workhorse**

강력한 Elasticsearch 및 Kibana 시너지 효과를 제공하는 수평 적으로 확장 가능한 데이터 처리 파이프 라인

**플러그 형 파이프 라인 아키텍처**

다양한 입력, 필터 및 출력을 믹스, 매칭 및 조화(ochestrate)하여 파이프 라인의 조화로 동작합니다.

**커뮤니티 확장 및 개발자 친화적 인 플러그인 생태계**

200 개 이상의 플러그인을 사용할 수 있으며 자신 만의 플러그인을 만들고 제공 할 수있는 유연성이 있습니다.

![](images/logstash_1.png)





Logstash는 데이터를 좋아합니다. 더 많이 모아서 더 많이 알 수 있습니다. Logstash는 모든 모양과 크기의 데이터를 환영합니다.



### 로그 및 메트릭

- 모든 유형의 로깅 데이터 처리
  - [Apache](http://www.elastic.co/guide/en/logstash/5.6/advanced-pipeline.html) 와 같은 웹 로그나 Java 용 [log4j](http://www.elastic.co/guide/en/logstash/5.6/plugins-inputs-log4j.html) 와 같은 응용 프로그램 로그를 쉽게 수집
  - [syslog](http://www.elastic.co/guide/en/logstash/5.6/plugins-inputs-syslog.html) , [Windows 이벤트 로그](http://www.elastic.co/guide/en/logstash/5.6/plugins-inputs-eventlog.html) , 네트워킹 및 방화벽 로그 등과 같은 많은 다른 로그 형식을 캡처하십시오.
- [Filebeat를 사용](https://www.elastic.co/products/beats/filebeat) 하여 보완적인 안전한 로그 전달 기능 [즐기기](https://www.elastic.co/products/beats/filebeat)
- [Ganglia](http://www.elastic.co/guide/en/logstash/5.6/plugins-inputs-ganglia.html) , [collectd](http://www.elastic.co/guide/en/logstash/5.6/plugins-codecs-collectd.html) , [NetFlow](http://www.elastic.co/guide/en/logstash/5.6/plugins-codecs-netflow.html) , [JMX](http://www.elastic.co/guide/en/logstash/5.6/plugins-inputs-jmx.html) 및 기타 여러 인프라 및 응용 프로그램 플랫폼에서 [TCP](http://www.elastic.co/guide/en/logstash/5.6/plugins-inputs-tcp.html) 및 [UDP를](http://www.elastic.co/guide/en/logstash/5.6/plugins-inputs-udp.html)통해 메트릭 수집




### 웹

- [HTTP 요청](http://www.elastic.co/guide/en/logstash/5.6/plugins-inputs-http.html) 을 이벤트로 변환
  - 사회 정서(social sentiment) 분석을 위해 [Twitter](http://www.elastic.co/guide/en/logstash/5.6/plugins-inputs-twitter.html) 와 같은 웹 서비스 firehoses에서 소비하십시오.
  - GitHub, HipChat, JIRA 및 기타 수많은 응용 프로그램에 대한 Webhook 지원
  - [감시자(Watcher)](https://www.elastic.co/products/x-pack/alerting) 에게 경고하는 많은 사례 사용 가능
- 필요에 따라 [HTTP endpoint](http://www.elastic.co/guide/en/logstash/5.6/plugins-inputs-http_poller.html) 를 폴링하여 이벤트 만들기
  - 웹 응용 프로그램 인터페이스의 상태, 성능, 메트릭 및 기타 유형의 데이터를 종합적으로 캡처합니다.
  - 수신보다 폴링 제어가 선호되는 시나리오에 이상적입니다.



### 데이터 저장소 및 스트림

이미 소유하고있는 데이터로부터 더 많은 가치를 발견하십시오.

- [JDBC](http://www.elastic.co/guide/en/logstash/5.6/plugins-inputs-jdbc.html) 인터페이스를 사용하여 관계형 데이터베이스 또는 NoSQL 저장소의 데이터를보다 잘 이해할 수 있습니다.
- Apache [Kafka](http://www.elastic.co/guide/en/logstash/5.6/plugins-outputs-kafka.html) , [RabbitMQ](http://www.elastic.co/guide/en/logstash/5.6/plugins-outputs-rabbitmq.html) , [Amazon SQS](http://www.elastic.co/guide/en/logstash/5.6/plugins-outputs-sqs.html) 및 [ZeroMQ](http://www.elastic.co/guide/en/logstash/5.6/plugins-outputs-zeromq.html) 와 같은 메시징 대기열에서 다양한 데이터 스트림을 통합합니다.

### 센서 및 IoT

- 기술 발전의 시대에 거대한 IoT 세계는 연결된 센서의 데이터를 캡처하고 활용함으로써 끝없이 사용되는 사례를 극복합니다.
- Logstash는 모바일 장치에서 인텔리전트 홈, 연결된 차량, 의료 센서 및 기타 여러 산업별 응용 프로그램으로 전송되는 데이터를 처리하기위한 공통 이벤트 수집 백본입니다.



## 모든 것의 질을 쉽게 높입니다.

데이터가 좋을수록 지식이 향상됩니다. 처리 중 데이터를 정리하고 변환하여 색인 또는 출력시 즉시 실시간 통찰력을 얻을 수 있습니다. Logstash는 패턴 매칭, geo 매핑 및 동적 조회(dynamic lookup) 기능과 함께 많은 집계(aggregation)과 변화(mutation)로 즉시 사용할 수 있습니다.

- [Grok](http://www.elastic.co/guide/en/logstash/5.6/plugins-filters-grok.html) 은 Logstash 필터의 빵과 버터이며 구조화되지 않은 데이터에서 구조를 파생시키기 위해 어디에서나 사용됩니다. 웹, 시스템, 네트워킹 및 기타 유형의 이벤트 형식을 신속하게 해결하는 데 도움이되는 풍부한 통합 패턴을 즐기십시오.
- IP 주소에서 [지리적 좌표](http://www.elastic.co/guide/en/logstash/5.6/plugins-filters-geoip.html) 를 해독하고 , [날짜](http://www.elastic.co/guide/en/logstash/5.6/plugins-filters-date.html) 복잡도를 정규화하고 , [키 - 값 쌍](http://www.elastic.co/guide/en/logstash/5.6/plugins-filters-kv.html) 및 [CSV](http://www.elastic.co/guide/en/logstash/5.6/plugins-filters-csv.html) 데이터를 단순화 하고 , 민감한 정보를 [핑거 프린팅](http://www.elastic.co/guide/en/logstash/5.6/plugins-filters-fingerprint.html) (익명 처리)하고 [로컬 조회](http://www.elastic.co/guide/en/logstash/5.6/plugins-filters-translate.html) 또는 Elasticsearch [쿼리로](http://www.elastic.co/guide/en/logstash/5.6/plugins-filters-elasticsearch.html) 데이터를 풍부하게 함으로써 시야를 확장하십시오 .
- 코덱은 [JSON](http://www.elastic.co/guide/en/logstash/5.6/plugins-codecs-json.html) 및 [다중 행](http://www.elastic.co/guide/en/logstash/5.6/plugins-codecs-multiline.html) 이벤트 와 같은 공통 이벤트 구조의 처리를 쉽게하기 위해 종종 사용됩니다 .

일반적인 데이터 처리 플러그인에 대한 개요는 [데이터 변환을](http://www.elastic.co/guide/en/logstash/5.6/transformation.html) 참조하십시오 .

## Stash 선택하십시오.

가장 중요한 곳에서 데이터를 전달하십시오. 데이터를 저장, 분석하고 조치를 취함으로써 다양한 다운 스트림 분석 및 운영 유스 케이스의 잠금을 해제하십시오.

**분석**

- [탄성 검색](http://www.elastic.co/guide/en/logstash/5.6/plugins-outputs-elasticsearch.html)
- [MongoDB](http://www.elastic.co/guide/en/logstash/5.6/plugins-outputs-mongodb.html) 및 [Riak](http://www.elastic.co/guide/en/logstash/5.6/plugins-outputs-riak.html) 과 같은 데이터 저장소

**보관 처리**

- [HDFS](http://www.elastic.co/guide/en/logstash/5.6/plugins-outputs-webhdfs.html)
- [S3](http://www.elastic.co/guide/en/logstash/5.6/plugins-outputs-s3.html)
- [Google Cloud Storage](http://www.elastic.co/guide/en/logstash/5.6/plugins-outputs-google_cloud_storage.html)

**모니터링**

- [Nagios](http://www.elastic.co/guide/en/logstash/5.6/plugins-outputs-nagios.html)
- [Ganglia](http://www.elastic.co/guide/en/logstash/5.6/plugins-outputs-ganglia.html)
- [Zabbix](http://www.elastic.co/guide/en/logstash/5.6/plugins-outputs-zabbix.html)
- [Graphite](http://www.elastic.co/guide/en/logstash/5.6/plugins-outputs-graphite.html)
- [Datadog](http://www.elastic.co/guide/en/logstash/5.6/plugins-outputs-datadog.html)
- [CloudWatch](http://www.elastic.co/guide/en/logstash/5.6/plugins-outputs-cloudwatch.html)

**경고**

- [Elasticsearch](https://www.elastic.co/products/watcher)를 통한 [감시자(Whatcher)](https://www.elastic.co/products/watcher)
- [Email](http://www.elastic.co/guide/en/logstash/5.6/plugins-outputs-email.html)
- [Pagerduty](http://www.elastic.co/guide/en/logstash/5.6/plugins-outputs-pagerduty.html)
- [IRC](http://www.elastic.co/guide/en/logstash/5.6/plugins-outputs-irc.html)
- [SNS](http://www.elastic.co/guide/en/logstash/5.6/plugins-outputs-sns.html)



## [Logstash 시작하기](https://www.elastic.co/guide/en/logstash/current/getting-started-with-logstash.html)