# Amazon Athena

* timestamp 형식에 ISO8601은 지원하지 않음

  * Athena는 Apache Hive를 사용하고 있기 때문에 Hive의 타입 형식을 따른다.
  * [하이브 Timestamp 형식](https://cwiki.apache.org/confluence/display/Hive/LanguageManual+Types#LanguageManualTypes-timestamp)
  * 위 링크에 나와있는 것처럼 date 형식은 일자까지만을 제공하고 timestamp로 해야 ms 단위까지 기록이 가능

  ​

## 스키마

* debug 로그

  ```sql
  CREATE EXTERNAL TABLE IF NOT EXISTS hive.debug212 (
    `timestamp` timestamp,
    `logLevel` string,
    `uid` string,
    `networkType` string,
    `packetName` string,
    `data` string 
  )
  ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe'
  WITH SERDEPROPERTIES (
    'serialization.format' = '@',
    'field.delim' = '@',
    'collection.delim' = 'undefined',
    'mapkey.delim' = 'undefined'
  ) LOCATION 's3://hive-server-log2/212/debug/'
  TBLPROPERTIES ('has_encrypted_data'='false');
  ```

* info 로그

  ```sql
  CREATE EXTERNAL TABLE IF NOT EXISTS hive.info212 (
    `timestamp` timestamp,
    `logLevel` string,
    `message` string
  )
  ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe'
  WITH SERDEPROPERTIES (
    'serialization.format' = '@',
    'field.delim' = '@',
    'collection.delim' = 'undefined',
    'mapkey.delim' = 'undefined'
  ) LOCATION 's3://hive-server-log2/212/info/'
  TBLPROPERTIES ('has_encrypted_data'='false');
  ```

* error 로그

  ```sql
  CREATE EXTERNAL TABLE IF NOT EXISTS hive.error212 (
    `timestamp` timestamp,
    `logLevel` string,
    `uid` string,
    `type` string,
    `message` string
  )
  ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe'
  WITH SERDEPROPERTIES (
    'serialization.format' = '@',
    'field.delim' = '@',
    'collection.delim' = 'undefined',
    'mapkey.delim' = 'undefined'
  ) LOCATION 's3://hive-server-log2/212/error/'
  TBLPROPERTIES ('has_encrypted_data'='false');
  ```



## logback 설정

- logback에서 날짜 저장시 debug 레벨과 trace 레벨의 date 형식을 다르게 남겨서 사용
  - trace는 elasticsearch에 저장이 되는데 elasticsearch는 ISO8601과 호환됨



```xml
... 생략 ...
<appender name="DEBUG" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <filter class="ch.qos.logback.classic.filter.LevelFilter">
        <level>DEBUG</level>
        <onMatch>ACCEPT</onMatch>
        <onMismatch>DENY</onMismatch>
    </filter>

    <file>Log/debug.log</file>
    <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
        <!-- %d{dateformat} 형식에서 dateformat에 따라 로그 남기는 주기가 설정됨. yyyy-MM-dd 인 경우 일자별로 로그 파일 생성 -->
        <fileNamePattern>Log/debug-%d{yyyy-MM-dd}.%i.txt</fileNamePattern>

        <!--하나의 로그 파일 용량이 100MB를 초과하면 위 fileNamePattern에서 %i 부분이 카운팅되면서 파일이 생성됨-->
        <maxFileSize>100MB</maxFileSize>

        <!--로그 파일을 유지하는 기간. (일 단위)-->
        <maxHistory>60</maxHistory>

        <!--totalSizeCap에 지정된 용량보다 로그 해당 loglevel의 전체 파일 사이즈가 커지는 경우 이전 파일 제거. 제거는 logback 마음대로 (비동기적, 적절히)-->
        <totalSizeCap>10GB</totalSizeCap>
    </rollingPolicy>

    <encoder>
        <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS}@%level@%msg%n</pattern>
    </encoder>
</appender>

<appender name="TRACE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <filter class="ch.qos.logback.classic.filter.LevelFilter">
        <level>TRACE</level>
        <onMatch>ACCEPT</onMatch>
        <onMismatch>DENY</onMismatch>
    </filter>
    <file>Log/trace.log</file>
    <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
        <fileNamePattern>Log/trace-%d{yyyy-MM-dd}.%i.txt</fileNamePattern>
        <maxFileSize>100MB</maxFileSize>
        <maxHistory>60</maxHistory>
        <totalSizeCap>10GB</totalSizeCap>
    </rollingPolicy>
    <encoder>
        <pattern>%d{ISO8601}@%level@%msg%n</pattern>
    </encoder>
</appender>

... 생략 ...
    
```



