볼륨 지정 시 권한을 rw로 설정했더라도 컨테이너에서 mount 명령 수행결과 mount된 볼륨 설정이 아래와 같이 `errors=remount-ro` 설정이 되어있다면 rw 권한으로 사용 중이더라도 파일 시스템 상에 에러가 발생할 경우 자동으로 ro 권한으로 remount 한다. 

![](images/docker_5.png)

