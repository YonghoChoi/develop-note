# AWS의 각종 CLI 설치

## AWS CLI 설치

1. pip와 python 설치 확인

   ```shell
   $ pip --version
   $ python --version
   ```

   * 설치되어 있지 않은 경우 설치 진행

     ```shell
     $ sudo apt-get install python3.4
     ```

2. Python Packaging Authority에서 제공하는 스크립트를 사용하여 pip 설치

   ```shell
   $ curl -O https://bootstrap.pypa.io/get-pip.py
   $ python3 get-pip.py --user
   ```

3. 환경 변수 추가

   ```shell
   export PATH=~/.local/bin:$PATH
   ```

   * `.bash_profile`, `.profile` 또는 `.bash_login`

4. pip를 사용하여 AWS CLI 설치

   ```shell
   $ pip install awscli --upgrade --user
   ```

5. AWS CLI 설치 확인

   ```shell
   $ aws --version
   ```

6. 최신 버전으로 업그레이드 시 아래 명령 수행

   ```shell
   $ pip install awscli --upgrade --user
   ```

   ​

## AWS EB CLI 설치

1. awsebcli 설치

   ```shell
   $ pip install --upgrade --user awsebcli
   ```

   * python 및 pip가 설치되어 있지 않은 경우 위 AWS CLI 설치의 1~3번 참조

2. AWS EB CLI 설치 확인

   ```shell
   $ eb --version
   ```

3. 초기화 진행

   ```shell
   $ eb init
   ```

   * 선택한 리전에 application이 존재하는 경우 아래와 같이 출력됨

     ```
     Select an application to use
     1) hive
     2) [ Create new Application ]
     ```

     * 새로 생성할 경우에는 Create new Application을 선택

4. 어플리케이션 상태 확인

   ```shell
   $ eb status
   ```

   ```
   Environment details for: hive-env
     Application name: hive
     Region: ap-northeast-2
     Deployed Version: hive-source
     Environment ID: e-4hrqqpzuz2
     Platform: arn:aws:elasticbeanstalk:ap-northeast-2::platform/Tomcat 8 with Java 8 running on 64bit Amazon Linux/2
     Tier: WebServer-Standard
     CNAME: hive-env.zavqfii6fy.ap-northeast-2.elasticbeanstalk.com
     Updated: 2017-06-13 03:32:10.672000+00:00
     Status: Ready
     Health: Green
   ```

   ​

