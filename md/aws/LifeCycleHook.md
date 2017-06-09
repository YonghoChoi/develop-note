# LifeCycleHook

* 오토 스케일링 그룹에 대해 일련의 라이프 사이클 작업을 구성
* 인스턴스가 pendding 또는 terminate 상태가 될 때마다 SQS 또는 SNS를 통해 대상으로 전달됨.
* 메시지를 받은 인스턴스는 pendding 대기 또는 terminate 대기 상태가 됨
  * 이 상태에서 응용 프로그램에 60분의 시간이 주어진다.
  * 작업시간이 60분을 넘어가는 경우 하트비트를 전달하여 시간을 연장할 수 있다.
* 시간이 만료되면 인스턴스는 대기상태가 해제된다.
* [AWS CLI](https://aws.amazon.com/cli/) 또는 [Auto Scaling API](http://docs.aws.amazon.com/AutoScaling/latest/APIReference/Welcome.html)를 통해 LifeCycleHook을 만들고 관리할 수 있다.



## 주요 기능

1. PutLifecycleHook : 오토 스케일링 그룹의 라이프 사이클 후크를 생성하거나 업데이트
   * 이 함수를 호출하여 인스턴스가 시작되거나 종료될 때 작동하는 후크를 생성
2. CompleteLifecycleAction : 라이프 사이클 후크에 대한 라이프 사이클 조치 완료를 나타냄
   * 후크가 인스턴스를 성공적으로 설정 또는 해제한 경우 이 함수를 호출
3. RecordLifecycleActionHeartbeat : 라이프 사이클 작업을 위한 하트 비트를 기록.
   * 라이프 사이클 액션 타임 아웃 시간을 연장하려는 경우 함수 호출



## 참고

* https://aws.amazon.com/ko/blogs/aws/auto-scaling-update-lifecycle-standby-detach/
* ​