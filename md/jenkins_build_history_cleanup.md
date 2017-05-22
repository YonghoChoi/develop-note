## jenkins build history cleanup

* Jenkins 설정 > Script Console 선택 후 스크립트 수행

  * 모든 아이템의 빌드 제거

    ```groovy
    item = Jenkins.instance.getAllItems().each() { item ->
      item.builds.each() { build ->
        build.delete()
      }
      item.updateNextBuildNumber(1) 
    }
    ```

  * 특정 아이템의 빌드 제거

    ```groovy
    item = Jenkins.instance.getItemByFullName("job_name")
    item.builds.each() { build ->
      build.delete()
    }
    item.updateNextBuildNumber(1)
    ```

* 스크립트 수행 시 오류가 발생하는 경우(빌드가 사용 중이라는 에러가 발생하는 경우가 있음) 수동으로 제거

  * 빌드 디렉토리 제거

    ```shell
    $ /jenkins_home/jobs> rm -rf */builds/*
    ```

  * jenkins 설정 리로드

    ```
    "Manage Jenkins" ==> "Reload Configuration from Disk"
    ```

  * 이 후 위의 스크립트를 수행하여 빌드 넘버 1로 설정