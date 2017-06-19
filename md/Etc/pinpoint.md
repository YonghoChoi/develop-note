# pinpoint



* [release repository](https://github.com/naver/pinpoint/releases)
* collector : https://github.com/naver/pinpoint/releases/download/1.6.2/pinpoint-collector-1.6.2.war
* web : https://github.com/naver/pinpoint/releases/download/1.6.2/pinpoint-web-1.6.2.war
* agent : https://github.com/naver/pinpoint/releases/download/1.6.2/pinpoint-agent-1.6.2.tar.gz



## collector

* collector war파일을 다운로드 후 tomcat을 통해 실행
* WEB-INF/classes/ 하위에서 설정 파일 수정
  * **hbase.properties **
  * **pinpoint-collector.properties**

## hbase

* HBase는 많은 파일을 사용하므로 ulimit 을 이용하여 open files 갯수를 충분히 늘려놓는 것을 권장

* HBase Download

* start

  ```shell
  $ HBASE_HOME/bin/start-hbase.sh
  ```

* 스키마 다운로드

  ```shell
  $ wget -c https://raw.githubusercontent.com/naver/pinpoint/master/hbase/scripts/hbase-create.hbase -P /opt/
  ```

* 스키마 갱신

  ```shell
  $ HBASE_HOME/bin/hbase shell hbase-create.hbase
  ```

* Zookeeper 설정

  * Zookeeper는 기본 적으로 Hbase에서 클러스터를 관리
  * 독립적으로 Zookeeper를 구성하려면 conf/hbase-env.sh파일에서 HBASE_MANAGES_ZK 변수를 false로 설정
  * 외부 접근이 가능하도록 하려면 hbase.zookeeper.quorum 속성을 hbase-site.xml에 설정해야 한다.
    * 이 설정은 기본 값이 localhost이므로 외부에서 접근이 불가능

## Zookeeper

* [Download](http://mirror.navercorp.com/apache/zookeeper/)
* ​



## web

- web war파일을 다운로드 후 tomcat을 통해 실행
- WEB-INF/classes/ 하위에서 설정 파일 수정
  - **hbase.properties **
  - **pinpoint-web.properties**





## 하나의 머신에 설치

1. 필요한 파일들 다운로드

   * pinpoint git clone
   * tomcat 8x
   * pinpoint-collector
   * pinpoint-web

2. hbase 초기화

   1. quickstart/bin/start-hbase.sh
   2. quickstart/bin/init-hbase.sh

3. pinpoint-collector 실행

4. pinpoint-web 실행

   * 실행 전에 config파일을 수정하여 v2로 변경

5. docker로 만든 경우 collector로 전달할 포트를 열어줘야함

   * 9994
   * 9995/tcp
   * 9995/udp
   * 9996/tcp
   * 9996/udp

   ```shell
   $ docker run -d -p 9994:9994 -p 9995:9995/tcp -p 9996:9996/tcp -p 9995:9995/udp -p 9996:9996/udp -p 18081:18081 -p 18082:18082 --name pinpoint registry.hive.com/pinpoint
   ```

6. agent 세팅

   * 톰캣 서버에 [다운로드](https://github.com/naver/pinpoint/releases)

   * 압축 해제 후 pinpoint.config 파일을 수정하여 collector의 ip 주소 설정

     ```
     profiler.collector.ip=192.168.10.x
     ```

   * tomcat 구동 시 옵션 추가

     ```
     PINPOINT_OPTS="-javaagent:/opt/pinpoint-agent/pinpoint-bootstrap-1.6.2.jar -Dpinpoint.agentId=agentId_01 -Dpinpoint.applicationName=hive_server_1"
     JAVA_OPTS="$JAVA_OPTS $JSSE_OPTS $PINPOINT_OPTS"
     ```

     ​

   ​



## docker-compose 파일

```yaml
pinpoint-hbase:
  container_name: pinpoint-hbase
  hostname: hbase
  image: naver/pinpoint-hbase:1.7.0-SNAPSHOT
  restart: always
  expose:
    - "2181"
    - "16010"
  ports:
    - "2181:2181"
    - "16010:16010"

pinpoint-mysql:
  container_name: pinpoint-mysql
  restart: always
  image: mysql:5.7
  ports:
    - "13306:3306"
  environment:
    - MYSQL_ROOT_PASSWORD=root123
    - MYSQL_USER=admin
    - MYSQL_PASSWORD=admin
    - MYSQL_DATABASE=pinpoint

pinpoint-collector:
  container_name: pinpoint-collector
  hostname: collector
  image: naver/pinpoint-collector:1.7.0-SNAPSHOT
  restart: always
  expose:
    - "9994"
    - "9995"
    - "9996"
  ports:
    - "9994:9994"
    - "9995:9995/tcp"
    - "9996:9996/tcp"
    - "9995:9995/udp"
    - "9996:9996/udp"
  environment:
    HBASE_HOST: hbase
    HBASE_PORT: 2181
    COLLECTOR_TCP_PORT: 9994
    COLLECTOR_UDP_STAT_LISTEN_PORT: 9995
    COLLECTOR_UDP_SPAN_LISTEN_PORT: 9996
    CLUSTER_ENABLE: 'true'
    CLUSTER_ZOOKEEPER_ADDRESS: hbase
    DISABLE_DEBUG: 'true'
  links:
    - pinpoint-hbase:hbase
 
pinpoint-web:
  container_name: pinpoint-web
  hostname: web
  image: naver/pinpoint-web:1.7.0-SNAPSHOT
  restart: always
  expose:
    - "8080"
  ports:
    - "3080:8080"
  environment:
    HBASE_HOST: hbase
    HBASE_PORT: 2181
    CLUSTER_ENABLE: 'true'
    CLUSTER_ZOOKEEPER_ADDRESS: hbase
    ADMIN_PASSWORD: admin123
    DISABLE_DEBUG: 'true'
    JDBC_DRIVER: com.mysql.jdbc.Driver
    JDBC_URL: jdbc:mysql://mysql:3306/pinpoint?characterEncoding=UTF-8
    JDBC_USERNAME: admin
    JDBC_PASSWORD: admin
  links:
    - pinpoint-hbase:hbase
    - pinpoint-mysql:mysql

# only example
#pinpoint-agent:
#  image: naver/pinpoint-agent:1.7.0-SNAPSHOT
#  net: "none"
#  restart: unless-stopped
#  volumes:
#    - /var/container_data/pinpoint-agent/log:/opt/app/pinpoint-agent/log:rw
#  environment:
#    COLLECTOR_IP: 192.168.99.100
#    COLLECTOR_TCP_PORT: 9994
#    COLLECTOR_UDP_STAT_LISTEN_PORT: 9995
#    COLLECTOR_UDP_SPAN_LISTEN_PORT: 9996
#    DISABLE_DEBUG: 'true'
```





## 참고

* [pinpoint 소개 및 설치](http://dev2.prompt.co.kr/33)
* [HBase와 Zookeer 동작 원리](http://guruble.com/?p=136)
* [HBase에서의 Zookeeper](http://hbase.apache.org/0.94/book/zookeeper.html)

