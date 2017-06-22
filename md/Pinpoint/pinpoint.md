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

* hadoop이 없는 상태의 hbase는 독립 실행 모드로만 동작 가능.

  * 분산 처리 불가능
  * HDFS가 없으므로 로컬 시스템에 테이블 저장




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

   * pinpoint.config 파일에 프로파일링을 원하는 클래스를 지정할 수 있다. 

     ```
     profiler.include=com.vinus.controller.*
     ```

     * 클래스를 지정하지 않으면 제대로 수집이 되지 않는 문제가 있었음.
     * 원인은 조금 더 찾아봐야할 듯

   * 샘플링 설정을 할 수 가 있는데 이 기능은 관리할 서버가 많은 경우 모든 패킷을 수집하게 되면 부하가 가기 때문에 전체 패킷의 퍼센티지로 패킷을 수집할 수 있다.

     * 성능 분석을 위해서는 모든 패킷을 받을 필요는 없으므로 실제 라이브 서비스에서는 사용할 것을 권장.
     * 샘플링 설정은 true 여야 모니터링이 가능.
     * 기본값은 20인데 20번의 request당 하나의 패킷을 전달한다는 의미
     * 1로 설정하면 무조건 수집

   ​


## 완전 분산형 설치

1. 하둡 설치 및 설정

   1. 하둡 [다운로드](http://apache.tt.co.kr/hadoop/common/)

   2. 압축 해제

      ```shell
      $ tar -xvf hadoop-x.x.x.tar.gz
      $ cd hadoop-x.x.x
      $ mkdir tmp
      ```

   3. etc/hadoop/hadoop-env.sh 수정
      ```shell
      # Set Hadoop-specific environment variables here.
      export JAVA_HOME="/usr/lib/jvm/java-7-openjdk-amd64"
      export HADOOP_HOME="/home/hive/programs/hadoop"
      ```

   4. etc/hadoop/core-site.xml 수정
      ```xml
      <configuration>
          <property>
              <name>fs.defaultFS</name>
              <value>hdfs://localhost:9000</value>
          </property>
          <property>
              <name>hadoop.tmp.dir</name>
              <value>/home/smilo/Working/BigData/hadoop-2.6.4/tmp</value>
          </property>
      </configuration>
      ```

   5. etc/hadoop/hdfs-site.xml 수정
      ```xml
      <configuration>
          <property>
              <name>dfs.replication</name>
              <value>1</value>
          </property>
      </configuration>
      ```

   6. etc/hadoop/mapred-site.xml 수정 (YARN을 위한 설정)
      ```xml
      <configuration>
          <property>
              <name>mapreduce.framework.name</name>
              <value>yarn</value>
          </property>
      </configuration>
      ```

   7. etc/hadoop/yarn-site.xml 수정 (YARN을 위한 설정)

      ```xml
      <configuration>
          <property>
              <name>yarn.nodemanager.aux-services</name>
              <value>mapreduce_shuffle</value>
          </property>
      </configuration>
      ```

   8. passphraseless ssh 설정
      ```shell
      $ ssh-keygen -t dsa -P '' -f ~/.ssh/id_dsa
      $ cat ~/.ssh/id_dsa.pub >> ~/.ssh/authorized_keys
      $ ssh localhost
      ```

   9. filesystem 포멧
      ```shell
      $ bin/hdfs namenode -format
      ```

   10. NameNode daemon, DataNode daemon 실행
     ```shell
     $ sbin/start-dfs.sh
     ```

   11. ResourceManager daemon, NodeManager daemon 실행
      ```shell
      $ sbin/start-yarn.sh
      ```

   12. 정상 동작 확인

       ```shell
       $ bin/hadoop fs -df -h
       ```

2. Zookeeper 설치 및 설정

   1. Zookeeper [다운로드](http://mirror.navercorp.com/apache/zookeeper/)

   2. 압축 해제

      ```shell
      $ tar xvfz zookeeper-x.x.x.tar.gz
      $ mv conf/zoo-sample.cfg conf/zoo.cfg
      ```

   3. Zookeeper 설정

      * conf/zoo.cfg

        ```
        dataDir=/data/zookeeper
        ```

3. HBase 설치 및 설정

   1. Hbase [다운로드](http://apache.tt.co.kr/hbase/)

   2. 압축해제

      ```shell
      $ tar xvfz hbase-x.x.x.tar.gz
      ```

   3. conf/hbase-env.sh 설정

     ```shell
     export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/jre
     export HBASE_PID_DIR=/opt/pids
     export HBASE_MANAGES_ZK=false
     ```

     * JAVA_HOME과 Pid 저장 경로 설정
     * zookeeper를 Hbase가 자체 관리하는 것 말고 외부에 설치한 것을 사용하기 위해 관리 설정을 false로 지정

   4. conf/hbase-site.xml 설정

     ```Xml
     <configuration>
             <property>
                     <name>hbase.rootdir</name>
                     <value>hdfs://25d2dfe7644e:9000/hbase</value>
             </property>
             <property>
                     <name>hbase.master</name>
                     <value>25d2dfe7644e:6000</value>
             </property>
             <property>
                     <name>zookeeper.znode.parent</name>
                     <value>/hbase</value>
             </property>
             <property>
                     <name>hbase.zookeeper.property.dataDir</name>
                     <value>/opt/data/zookeeper</value>
             </property>
             <property>
                     <name>hbase.zookeeper.quorum</name>
                     <value>25d2dfe7644e</value>
             </property>
             <property>
                     <name>dfs.replication</name>
                     <value>1</value>
             </property>
             <property>
                     <name>hbase.zookeeper.property.clientPort</name>
                     <value>2181</value>
             </property>
             <property>
                     <name>hbase.cluster.distributed</name>
                     <value>true</value>
             </property>
     </configuration>
     ```

     * 설치한 hadoop의 hdfs를 사용하기 위해 hbas.rootdir 경로 지정
       * 해당 경로에 대한 설정은 hadoop의 etc/hadoop/core-site.xml 에서 설정
     * hbase의 master 경로 설정
     * zookeeper의 znode 설정
     * zookeeper quorum 설정을 해야 해당 zookeeper 서버로 접속이 가능
     * 분산 클러스터를 사용하는 경우 hbase.cluster.distributed 설정을 true로 해야한다.

   5. HBase 실행

      ```shell
      $ HBASE_HOME/bin/start-hbase.sh
      ```

   6. 스키마 다운로드

      ```Shell
      $ wget https://raw.githubusercontent.com/naver/pinpoint/master/hbase/scripts/hbase-create.hbase
      ```

   7. 스키마 갱신

      ```shell
      $ HBASE_HOME/bin/hbase shell hbase-create.hbase
      ```

4. Pinpoint collector와 web의 war 파일 다운로드

   * [최신 release 다운로드](https://github.com/naver/pinpoint/releases)
   * 각각 톰캣을 구성하여 구동.
   * 톰캣 설정에 대한 부분은 생략 (한 머신에서 구동하는 경우 포트를 겹치지 않게 해야 함)

5. pinpoint collector 설정

   * WEB-INF/classes/hbase.properties

     ```shell
     hbase.client.host=25d2dfe7644e
     ```

     * hbase의 호스트명 입력

   * WEB-INF/classes/pinpoint-collector.properties

     ```
     기본 설정 그대로 사용
     ```

6. pinpoint web 설정

   * WEB-INF/classes/hbase.properties

     ```shell
     hbase.client.host=25d2dfe7644e
     ```

     - hbase의 호스트명 입력

   * WEB-INF/classes/pinpoint-web.properties

     ```
     기본 설정 그대로 사용
     ```




## 완전 분산형 실행

1. 하둡 시작

   ```shell
   $ /opt/hadoop/sbin/start-dfs.sh
   $ /opt/hadoop/sbin/start-yarn.sh
   ```

2. zookeeper 시작

   ```shell
   $ /opt/zookeeper/bin/zkServer.sh start
   ```

3. hbase 시작

   ```shell
   $ /opt/hbase/bin/start-hbase.sh
   ```

4. Pinpoint-collector 시작

   ```shell
   $ /opt/pinpoint-collector/bin/startup.sh
   ```

5. Pinpoint-web 시작

   ```shell
   $ /opt/pintpoint-web/bin/startup.sh
   ```




## 완전 분산형 초기화

초기화 전에 collector와 web, hbase는 모두 종료되어 있어야 한다.

1. hbase 초기화

   * hadoop에서 hbase 저장소 확인

     ```shell
     $ hadoop fs -ls /
     ```

   * hbase 디렉토리 제거

     ```shell
     $ hadoop fs -rmr /hbase
     ```

     * 제거시 hbase가 종료되어 있어야 함.
     * 살아있는 경우 캐시된 데이터를 다시 갱신하는 듯. 
     * 삭제해도 다시 데이터가 복원됨

2. zookeeper 초기화

   ```shell
   $ zkCli.sh -server localhost:2181
   zookeeper> rmr /hbase
   zookeeper> rmr /pinpoint-cluster
   ```

   ```shell
   $ rm -rf /opt/data/zookeeper/*
   ```

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



## 구성요소

* WriteQueue
  * CallStack Trace를 위해 각 메서드의 호출로 인해 스택에 스택프레임이 쌓이게 되면 이를 WriteQueue에 하나하나 저장한 후 collector에게 전송.
* 추적에 사용되는 값
  * TxId(Transaction ID) : GUID로 전체 메시지 아이디
  * SpanId : 부모 자식 관계 정렬을 위한 ID
  * pSpanId : 부모 자식 관계 정렬을 위한 ID
* 분산 Transaction Trace
  * 톰캣A에 Http요청이 오면 pinpoint가 이를 가로채서 TraceID를 injection
    * injection 위치는 http 헤더
  * 톰캣B로 전달을하면 해당 톰캣에서 request를 받아 transactionID를 찾음
    * 이렇게 전달받은 톰캣은 자식 노드가 됨
  * 톰캣 B의 처리가 끝나면 TraceData는 collector에게 전달




## TODO

* hbase의 각 노드들과 zookeeper는 전부 같은 네트워크 안에서 서로 통신할 수 있어야 한다.
* docker를 사용한다면 swarm으로 구성 고려
  * overlay 네트워크를 만들어서 모든 서비스가 이 네트워크 안에 속하게
  * 서비스 리스트
    * pinpoint-collector
    * pinpoint-web
    * hbase
    * hadoop
    * zookeeper
* hbase가 증가하면 zookeeper가 regionserver에 대한 설정파일을 업데이트 해줄 수 있는지 검토
  * 이게 가능하면 docker로 scale 가능
* pinpoint는 zookeeper에 접근하여 hbase의 접속 정보를 얻음
  * 이 때 zookeeper는 master만 알려주나?
  * 아니면 어떤 기준에 의해서 등록된 hbase 중 하나를 선택?
  * 만약 master만 준다면 master가 알아서 분산해주는 건가?
* pinpoint에서 cluster 설정은 뭘 의미하는거지?
* Test 순서
  1. 한 컨테이너 안에서 각각 프로그램을 실행하여 ip로 구성
  2. 1이 성공하면 211 서버에 그대로 구성해보기
  3. 2가 성공하면 docker swarm 구성 후 같은 네트워크 안에서 구동




## 참고

* [pinpoint 소개 및 설치](http://dev2.prompt.co.kr/33)
* [HBase와 Zookeer 동작 원리](http://guruble.com/?p=136)
* [HBase에서의 Zookeeper](http://hbase.apache.org/0.94/book/zookeeper.html)
* [대규모 분산 시스템 추적 플랫폼, Pinpoint](http://d2.naver.com/helloworld/1194202)
* [pinpoint QnA](https://yangbongsoo.gitbooks.io/study/content/q&a.html)
* [Deview 2015 pinpoint 관련 내용](http://serviceapi.rmcnmv.naver.com/flash/outKeyPlayer.nhn?vid=8B3E4703564F97B2324684DAAA3C8470A23D&outKey=V126be8b6348f5c4d31ba343b1734c0a7d68ad2e391384e567e39343b1734c0a7d68a&controlBarMovable=true&jsCallable=true&skinName=tvcast_white)
* [허광남님의 pinpoint 설치 영상](https://www.youtube.com/watch?v=hrvKaEaDEGs)
* [HBase 완전 분산 설치](http://blog.iotinfra.net/?tag=hbase-%EC%99%84%EC%A0%84%EB%B6%84%EC%82%B0-%EC%84%A4%EC%B9%98)

