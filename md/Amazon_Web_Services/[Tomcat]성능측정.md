# 성능 측정

* 힙 크기와 최대 스레드 수는 비례
* 이론적으로 프로세스의 사용자 주소 공간(user address space)을 스레드 스택 크기(thread stack size)로 나눈 공간이 최대 스레드 수 이다.
  * 각 프로세스가 2GB의 사용자 주소 공간을 가지고 각 스레드에 128K 스택 크기를 제공하는 32 비트 Windows 환경에서는 최대 16384 스레드( = 2 * 1024 * 1024 / 128)가 필요. 실제로는 13000건을 시작할 수 있었음.

## JVM

* 스레드 수

  ```shell
  ps huH p <PID> | wc -l
  ```

* heap 확인

  * jmap을 사용하기 위해 아래 패키지가 설치되어야함

    ```shell
    sudo yum install java-1.8.0-openjdk
    ```

    ```
    sudo yum install java-1.8.0-openjdk-devel
    ```

  * Unknown collectedheap type 에러 발생 시

    ```
    Heap Usage:
    Exception in thread "main" java.lang.reflect.InvocationTargetException
           at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
           at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
           at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
           at java.lang.reflect.Method.invoke(Method.java:606)
           at sun.tools.jmap.JMap.runTool(JMap.java:197)
           at sun.tools.jmap.JMap.main(JMap.java:128)
    Caused by: java.lang.RuntimeException: unknown CollectedHeap type : class sun.jvm.hotspot.gc_interface.CollectedHeap
           at sun.jvm.hotspot.tools.HeapSummary.run(HeapSummary.java:146)
           at sun.jvm.hotspot.tools.Tool.start(Tool.java:221)
           at sun.jvm.hotspot.tools.HeapSummary.main(HeapSummary.java:40)
    ```

    * Elastic beanstalk의 경우 아래 명령으로 debuginfo 패키지를 설치해야함

      ```shell
      sudo debuginfo-install java-1.8.0-openjdk-devel
      ```

* GC 상태

  ```
  jstat -gc <PID> <시간주기(ms)|Optional>
  ```

  ​



## 고려해야할 항목들

* Old 영역으로 넘어가는 객체의 수 최소화
  * Old 영역의 GC는 New 영역의 GC에 비해 시간이 오래 소요되기 때문에 Old 영역으로 이동하는 객체의 수를 줄이면 Full GC가 발생하는 빈도를 많이 줄일 수 있다.
  * New 영역 크기를 조절하면 큰효과를 볼 수 있음
* Full GC 시간 줄이기
* ​



## 설정

* maxThread 수
  * Tomcat이 요청을 처리하기 위해 만들어내는 최대 Thread 개수
  * 성능에 큰 영향을 줌
  * DB 커넥션 수보다는 넉넉하게 설정하는 것이 좋음
* ​





## 참고

* [Java 시스템 운영 중 알아두면 쓸모 있는 지식들](https://www.holaxprogramming.com/2017/10/09/java-jvm-performance/)
* ​