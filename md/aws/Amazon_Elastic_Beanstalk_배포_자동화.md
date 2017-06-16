# Jenkins를 통해 Amazon Elastic Beanstalk 배포 자동화

1. Jenkins에서 빌드를 수행하는 Item의 설정 페이지로 이동

2. Add build step으로 Execute shell 추가

3. 스크립트에 빌드를 통해 만들어진 war파일을 S3로 업로드하는 명령 추가

   ```shell
   $ aws s3 cp $WORKSPACE/build/libs/HiveServer.war s3://hive-build-artifacts/$BUILD_NUMBER/
   ```

   * 이 때 만들어진 war파일들에 대한 버전관리를 위해 빌드 번호로 디렉토리를 생성하여 업로드

4. Jenkins에서 배포를 수행하는 Item의 설정 페이지로 이동

5. General 항목에서 `이 빌드는 매개변수가 있습니다. `체크박스에 체크

   * 아래에서 application versions에 war파일을 업로드할 때 설명을 넣기 위해 매개변수 추가를 선택하여 String Parameter 추가

6. Add build step으로 Copy artifacts from another project 추가

   * Project name에 빌드를 수행하는 Jenkins Item 지정
   * 해당 플러그인이 존재하지 않을 경우 설치 후 진행

7. Add build step으로 Execute shell 추가

   1. Amazon Elastic Beanstalk의 application versions에 war upload

      ```shell
      $ aws elasticbeanstalk create-application-version --application-name <어플리케이션명> --version-label <버전 레이블> --description <설명> --source-bundle S3Bucket="버킷명",S3Key="<파일 경로>" --auto-create-application
      ```

      * 하이브에서의 사용 예

        ```shell
        $ aws elasticbeanstalk create-application-version --application-name hive --version-label hive-b$BUILD_NUMBER --description "$APPLICATION_VERSION_DESC" --source-bundle S3Bucket="hive-build-artifacts",S3Key="$COPYARTIFACT_BUILD_NUMBER_BUILD_HIVE_GAME/HiveServer.war" --auto-create-application
        ```

      * Output

        ```json
        {
            "ApplicationVersion": {
                "ApplicationName": "hive",
                "Status": "UNPROCESSED",
                "VersionLabel": "hive-b2",
                "Description": "외부 시연용 빌드",
                "DateCreated": "2017-06-15T09:20:32.204Z",
                "DateUpdated": "2017-06-15T09:20:32.204Z",
                "SourceBundle": {
                    "S3Bucket": "hive-build-artifacts",
                    "S3Key": "83/HiveServer.war"
                }
            }
        }
        ```

   2. 어플리케이션과 envoironment를 지정하여 deploy

      ```shell
      $ aws elasticbeanstalk update-environment --application-name <어플리케이션명> --environment-name <environment명> --version-label <Application Versions에 명시된 버전 레이블>
      ```

      * 하이브에서의 사용 예

        ```shell
        $ aws elasticbeanstalk update-environment --application-name hive --environment-name hive-env --version-label hive-b$BUILD_NUMBER
        ```

      * Output

        ```json
        {
            "ApplicationName": "hive",
            "EnvironmentName": "hive-env",
            "VersionLabel": "hive-b8",
            "Status": "Updating",
            "PlatformArn": "arn:aws:elasticbeanstalk:ap-northeast-2::platform/Tomcat 8 with Java 8 running on 64bit Amazon Linux/2.6.0",
            "EndpointURL": "52.78.205.157",
            "SolutionStackName": "64bit Amazon Linux 2017.03 v2.6.0 running Tomcat 8 Java 8",
            "EnvironmentId": "e-4hrqqpzuz2",
            "CNAME": "hive-env.zavqfii6fy.ap-northeast-2.elasticbeanstalk.com",
            "AbortableOperationInProgress": true,
            "Tier": {
                "Version": " ",
                "Type": "Standard",
                "Name": "WebServer"
            },
            "Health": "Grey",
            "DateUpdated": "2017-06-15T09:32:51.431Z",
            "DateCreated": "2017-06-13T03:29:09.361Z"
        }
        ```



위에서 지정한 Jenkins의 환경변수들은 [Jenkins 홈페이지](https://wiki.jenkins-ci.org/display/JENKINS/Building+a+software+project)를 참고하던가 스크립트에 env 명령을 수행해서 확인 후 지정한다.