# Hadoop 설치

참고 : http://withsmilo.github.io/bigdata/2016/04/06/setup-hadoop-2.6.4/

Ubuntu 15.10에서 hadoop 2.6.4를 설치하는 과정을 간단히 정리했다. PC 한대에서 가상 분산 환경을 만들 생각이기 때문에 [Pseudo-Distributed Mode](http://hadoop.apache.org/docs/r2.6.4/hadoop-project-dist/hadoop-common/SingleCluster.html#Pseudo-Distributed_Operation)로 설정한다.

### 1. Java openJDK 7 설치

```
$ sudo apt-get install openjdk-7-jdk
$ sudo update-alternatives --config java
$ javac -version
javac 1.7.0_95
```

### 2. 필요한 패키지 설치

```
$ sudo apt-get install ssh rsync

```

### 3. hadoop 2.6.4 [다운로드](http://apache.tt.co.kr/hadoop/common/) 후 적당한 위치에 풀기

```
$ tar -xvf hadoop-2.6.4.tar.gz
$ cd hadoop-2.6.4
$ mkdir tmp
```

### 4. etc/hadoop/hadoop-env.sh 수정

```
# Set Hadoop-specific environment variables here.
export JAVA_HOME="/usr/lib/jvm/java-7-openjdk-amd64"
export HADOOP_HOME="/home/smilo/Working/BigData/hadoop-2.6.4"

```

### 5. etc/hadoop/core-site.xml 수정

```
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

### 6. etc/hadoop/hdfs-site.xml 수정

```
<configuration>
    <property>
        <name>dfs.replication</name>
        <value>1</value>
    </property>
</configuration>

```

### 7. etc/hadoop/mapred-site.xml 수정 (YARN을 위한 설정)

```
<configuration>
    <property>
        <name>mapreduce.framework.name</name>
        <value>yarn</value>
    </property>
</configuration>

```

### 8. etc/hadoop/yarn-site.xml 수정 (YARN을 위한 설정)

```
<configuration>
    <property>
        <name>yarn.nodemanager.aux-services</name>
        <value>mapreduce_shuffle</value>
    </property>
</configuration>

```

### 9. passphraseless ssh 설정

```
$ ssh-keygen -t dsa -P '' -f ~/.ssh/id_dsa
$ cat ~/.ssh/id_dsa.pub >> ~/.ssh/authorized_keys
$ ssh localhost

```

### 10. filesystem 포멧

```
$ bin/hdfs namenode -format

```

### 11. NameNode daemon, DataNode daemon 실행

```
$ sbin/start-dfs.sh

```

### 12. ResourceManager daemon, NodeManager daemon 실행

```
$ sbin/start-yarn.sh
```

### 13. 확인

```
$ bin/hadoop fs -df -h
$ bin/hdfs dfs -mkdir -p /user/<username>
$ bin/hdfs dfs -put etc/hadoop input
$ bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.6.4.jar grep input output 'dfs[a-z.]+'
$ bin/hdfs dfs -cat output/*
6	dfs.audit.logger
4	dfs.class
3	dfs.server.namenode.
2	dfs.period
2	dfs.audit.log.maxfilesize
2	dfs.audit.log.maxbackupindex
1	dfsmetrics.log
1	dfsadmin
1	dfs.servers
1	dfs.replication
1	dfs.file
```

### 14. 유용한 도구

- namenode web UI (http://localhost:50070)
- resource manager web UI (http://localhost:8088)
- 현재 어떤 프로세스가 실행 중인지는 [jps](http://docs.oracle.com/javase/7/docs/technotes/tools/share/jps.html)를 실행하여 확인 가능

### 15. NameNode daemon and DataNode daemon 중지

```
$ sbin/stop-dfs.sh
```

### 16. ResourceManager daemon, NodeManager daemon 중지

```
$ sbin/stop-yarn.sh

```

### 17. 문제 해결

- namenode가 실행이 안되는 경우 아래 단계를 따른다. ([참고](http://stackoverflow.com/questions/8076439/namenode-not-getting-started))

```
$ rm -rf tmp
$ bin/dhfs namenode -format
$ sbin/start-dfs.sh
```