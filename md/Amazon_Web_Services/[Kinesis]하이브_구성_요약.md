## Kinesis

* 키네시스 스트림 생성

  ```
  aws kinesis create-stream \
  --stream-name examplestream \
  --shard-count 1 \
  --region ap-northeast-2 \
  --profile adminuser
  ```

* 로그 정규표현식

  ```
  (\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2},\d{3}) ([^ ]*) ([^ ]*) ([^ ]*) ([^ ]*) (.*)$
  ```

* Kinesis 설정

  ```
  {
    "awsAccessKeyId": "",
    "awsSecretAccessKey": "",
    "cloudwatch.emitMetrics": true,
    "cloudwatch.endpoint": "monitoring.ap-northeast-2.amazonaws.com",
    "kinesis.endpoint": "kinesis.ap-northeast-2.amazonaws.com",
    "firehose.endpoint": "",
    "flows": [
      {
        "filePattern": "/usr/share/tomcat8/logs/*",
        "kinesisStream": "hive-dev",
        "partitionKeyOption": "RANDOM",
        "dataProcessingOptions": [
          {
            "optionName": "LOGTOJSON",
            "logFormat": "COMMONAPACHELOG",
            "matchPattern": "(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{3}) ([^ ]*) ([^ ]*) ([^ ]*) ([^ ]*) (.*)$",
            "customFieldNames": ["timestamp", "loglevel", "uid", "networktype", "packet", "data"]
          }
        ]
      }
    ]
  }
  ```

* Lambda 생성

  ```
  $ aws lambda create-function \
  --region ap-northeast-2 \
  --function-name ProcessKinesisRecords  \
  --zip-file fileb:///home/ec2-user/script/ProcessKinesisEvents.zip \
  --role arn:aws:iam::xxx:role/LambdaKinessExecutionRole  \
  --handler example.ProcessKinesisRecords::recordHandler \
  --runtime java8 \
  --profile yongho
  ```

  * handler는 java인 경우 `example.ProcessKinesisRecords::recordHandler`

* Lambda 연동

  ```
  $ aws lambda create-event-source-mapping \
  --region ap-northeast-2 \
  --function-name ProcessKinesisRecords \
  --event-source  arn:aws:kinesis:ap-northeast-2:xxx:stream/hive-dev \
  --batch-size 100 \
  --starting-position TRIM_HORIZON \
  --profile yongho
  ```

* Lambda에 매핑된 이벤트 리스트

  ```
  $ aws lambda list-event-source-mappings \
  --region ap-northeast-2 \
  --function-name ProcessKinesisRecords \
  --event-source arn:aws:kinesis:ap-northeast-2:xxx:stream/hive-dev \
  --profile yongho \
  --debug
  ```

* Lambda Test

  ```
  {
      "Records": [
          {
              "kinesis": {
                  "partitionKey": "partitionKey-3",
                  "kinesisSchemaVersion": "1.0",
                  "data": "SGVsbG8sIHRoaXMgaXMgYSB0ZXN0IDEyMy4=",
                  "sequenceNumber": "49545115243490985018280067714973144582180062593244200961"
              },
              "eventSource": "arn:aws:kinesis:ap-northeast-2:xxx:stream/hive-dev",
              "eventID": "shardId-000000000000:xxx",
              "invokeIdentityArn": "arn:aws:iam::xxx:role/LambdaKinessExecutionRole",
              "eventVersion": "1.0",
              "eventName": "aws:kinesis:record",
              "eventSourceARN": "arn:aws:kinesis:ap-northeast-2:xxx:stream/hive-dev",
              "awsRegion": "ap-northeast-2"
          }
      ]
  }
  ```

  ```
  $ aws lambda invoke \
  --invocation-type Event \
  --function-name ProcessKinesisRecords \
  --region ap-northeast-2 \
  --payload file:///home/ec2-user/script/lambda-test.txt \
  --profile yongho outputfile.txt
  ```

* Lambda 함수 제거

  ```
  aws lambda delete-function --function-name ProcessKinesisRecords --profile yongho
  ```



## 참고

* [Kinesis로 App 로그 다루기](https://gist.github.com/haje01/5abaeadda792b40f39d7)