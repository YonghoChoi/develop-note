# Elasticsearch IAM Role 설정

## 리소스 기반 사용 권한

Resource 섹션에 정의되어 있는 hive-es라는 Amazon ES 도메인에 정책을 정의한다. 특정 IP 주소로부터의 요청만 허용할 수도 있고 IAM 사용자 별 접근 권한을 지정할 수도 있다.

* 특정 IP 주소로부터의 요청 허용 예

  ```json
  {
    "Version": "2012-10-17",
    "Statement": [
      {
        "Effect": "Allow",
        "Principal": {
          "AWS": "*"
        },
        "Action": "es:*",
        "Resource": "arn:aws:es:ap-northeast-2:138011803946:domain/hive-dev/*",
        "Condition": {
          "IpAddress": {
            "aws:SourceIp": [
              "218.x.x.x/32",
              "172.x.x.x/24"
            ]
          }
        }
      }
    ]
  }
  ```

* AWS 로그인 유저로부터의 접근 허용 예

  ```json
  {
    "Version": "2012-10-17",
    "Statement": [
      {
        "Effect": "Allow",
        "Principal": {
          "AWS": "*"
        },
        "Action": "es:*",
        "Resource": "arn:aws:es:ap-northeast-1:138011803946:domain/hive-es/*"
      }
    ]
  }
  ```

* 특정 IAM 사용자로부터의 접근 허용 예

  ```json
  {
    "Version": "2012-10-17",
    "Statement": [{
        "Effect": "Allow",
        "Principal": {
          "AWS": "arn:aws:iam::111111111111:user/recipes1alloweduser"
        },      
        "Action": "es:*", 
        "Resource": "arn:aws:es:us-west-2:111111111111:domain/recipes1/*" 
      }   
    ] 
  }
  ```







## ID 기반 사용 권한

identity-based 사용 권한을 부여하는 경우에는 Resource가 IAM 사용자가 되면서 해당 사용자가 수행할 수 있는 Action을 정의하는 방식으로 설정을 한다. Action은 Array 형식으로 다수의 AWS 리소스 권한을 부여할 수 있고, Effect를 통해 해당 리소스에 대한 접근을 허용할지 거부할지를 정의할 수 있다.

* IAM 사용자에게 특정 리소스의 권한 허용 예

  ```json
  {
   "Version": "2012-10-17",
   "Statement": [
    {
     "Resource": "arn:aws:es:us-west-2:111111111111:domain/recipes1/*",
     "Action": ["es:*"],
     "Effect": "Allow"
    }
   ]
  }
  ```







## 서명 버전 4 (Signature Version 4)

