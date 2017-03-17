기본 : CPU, RAM 10%

8192 : CPU 10, RAM 40



## public IP 확인

```
$ curl ifconfig.me
```



## top



* 로드 에버리지(load average)
  * 작업의 대기시간 , 값이 1이라면 1분동안 평균 1개의  프로세스가 대기상태임을 나타낸다.
  * 보통 5이면 서버가 부하를 받는다고 생각함, 10~15면 과부하
* Tasks: 131 total,   1 running, 130 sleeping,   0 stopped,   0 zombie 
  * 전체 프로세스 수, 현재 실행중인 프로세스, 유휴상태 프로세스, 정지상태 프로세스, 좀비 프로세스
* Cpu(s):  2.4%us,  0.3%sy,  0.0%ni, 97.0%id,  0.2%wa,  0.0%hi,  0.0%si,  0.0%st
  * 사용자가 사용중인 CPU 사용률(us), 시스템이 사용하는 CPU 사용률(sy), NICE 정책에 의해 사용되는 CPU 사용률(ni), 사용되지 않은 CPU의 미사용률(id), 입출력 대기상태의 사용률(wa)
* Mem:   8140668k total,  7900820k used,   239848k free,  3074544k buffers 
  * 전체 물리적인 메모리, 사용중인 메모리(used), 사용되지 않는 여유 메모리(free), 버퍼된 메모리(buffers)
* Swap:  8349692k total,    13476k used,  8336216k free,  3792984k cached 
  * 전체 스왑 메모리, 사용중인 스왑 메모리, 남아있는 스왑메모리, 캐싱메모리
* PID USER      PR  NI  VIRT  RES  SHR S %CPU %MEM    TIME+  COMMAND
  * PID : 프로세스 ID (PID)
  * USER : 프로세스를 실행시킨 사용자 ID
  * PRI : 프로세스의 우선순위 (priority)
  * NI : NICE 값. 일의 nice value값이다. 마이너스를 가지는 nice value는 우선순위가 높음.
  * VIRT : 가상 메모리의 사용량(SWAP+RES)
  * RES : 현재 페이지가 상주하고 있는 크기(Resident Size)
  * SHR : 분할된 페이지, 프로세스에 의해 사용된 메모리를 나눈 메모리의 총합.
  * S : 프로세스의 상태 [ S(sleeping), R(running), W(swapped out process), Z(zombies) ]
  * %CPU : 프로세스가 사용하는 CPU의 사용율
  * %MEM : 프로세스가 사용하는 메모리의 사용율
  * COMMAND : 실행된 명령어



### 명령 실행 후 사용하는 옵션들

* 1 :  cpu 갯수별 사용량 보기
* shift + m : 메모리 사용량이 큰 순서로 정령
* shift + p : CPU 사용량이 큰 순서로 정렬
* shift + t : 실행시간이 큰 순서로 정렬
* k : 프로세스  kill  - k 입력 후 종료할 PID 입력 signal을 입력하라고 하면 kill signal인 9를 입력
* c : 명령인자 표시/비표시
* space bar : refresh
* u : 입력한 유저의 프로세스만 표시 - which u



### 유용한 명령어 조합

* CPU 유휴율

  ```shell
  $ top -n 1 | grep -i cpu\(s\)| awk '{print $8}'
  ```

  * top 명령어의 출력 방식에 따라 print 뒤의 $숫자 값이 달라질 수 있음.





## vmstat

시스템 작업, 하드웨어 및 시스템 정보, 메모리, 페이징, 블록장치의 I/O, CPU상태 등 확인.

주의할 것은 vmstat 명령을 실행한 첫번째 결과는 부팅후 지금까지의 통계 값이므로 첫번째 결과는 제외하고 봐야함. 그러므로 vmstat 명령에는 항상 delay 값을 입력하여 업데이트된 데이터를 봐야한다.

![20150903_vmstat](http://tech.whatap.io/wp-content/uploads/2016/01/20150903_vmstat.png)

* Procs : 메모리가 읽어야 할 데이터의 수로 5이하가 좋다.
  * **r** : 현재 실행중인 프로세스의 수
  * **b** : 인터럽트가 불가능한 sleep 상태에 있는 프로세스의 수
    * I/O 처리를 하는 동안 블럭 처리된 프로세스
    * 만약 b의 수치가 높은 경우라면 CPU가 계속 대기상태로 있다는 의미이므로 디스크I/O를 확인해 볼 필요가 있음
* Swap : 메모리가 가득 차 작업을 할 수 없을 때, 대기중인 작업을 넣어 두는 곳.
  * **si(swap in)** : 사용되고 있는 디스크메모리(스왑공간에 있는 데이터)가 해제되는 양(per sec)
  * **so(swap out)** : 물리적 메모리가 부족할 경우 디스크로부터 사용되는 메모리 양(per sec)
    * 이 때, swap out이 지속적으로 발생한다면 메모리 부족을 의심 해 볼 수 있음.
    * swap out값이 증가하면 메모리가 부족하다는 의미이므로 메모리를 늘려야 한다.
    * Swap out값은 0에 가까워야 좋고 초당 10블럭이하가 좋다. 그러나 swap필드의 값이 높다고 해도 free 메모리에 여유가 있다면 메모리가 부족한 것은 아님.
* -s 옵션 : 메모리 통계 항목 확인

![20150903_vmstat-s](http://tech.whatap.io/wp-content/uploads/2016/01/20150903_vmstat-s.png)



* 실시간 메모리 상태 확인

  ```shell
  $ vmstat [delay] [count]
  ```

  * ex ) vmstat 3 5 : 3초 간격으로 5번 출력
  * ex) vmstat 3 : 3초 간격으로 계속 모니터링



## iostat

평균 CPU부하 와 디스크 I/O의 세부적인 내용 확인

![20150903_iostat](http://tech.whatap.io/wp-content/uploads/2016/01/20150903_iostat.png)

- **tps** : 디바이스에 초당 전송 요청 건수
- **kB_read/s** : 디바이스에서 초당 읽은 데이터 블록 단위
- **kB_wrtn/s** : 디바이스에서 초당 쓴 데이터 블록 단위
- **kB_read** : 디바이스에서 지정한 간격 동안 읽은 블록 수
- **kB_wrtn** : 디바이스에서 지정한 간격 동안 쓴 전체 블록 수

더 자세한 정보를 확인하기 위해 -x옵션을 사용

![20150903_iostat-x](http://tech.whatap.io/wp-content/uploads/2016/01/20150903_iostat-x.png)

 

* 실시간 디스크 상태 확인

  ```shell
  $ iostat [delay] [count]
  ```
  * ex ) iostat 3 5 : 3초 간격으로 5번 출력
  * ex) iostat 3 : 3초 간격으로 계속 모니터링



## netstat

현재 시스템에 연결된 네트워크 상태, 라우팅테이블, 인터페이스 상태 등 확인

![20150903_netstat](http://tech.whatap.io/wp-content/uploads/2016/01/20150903_netstat.png)

* 2개의 영역으로 분류
  * Active Internet connections : TCP, UDP, raw로 연결 된 목록
  * Active UNIX domain sockets : 도메인소켓으로 연결 된 목록
* 옵션
  * **-n** : 호스트명, 포트명을 lookup하지 않고(도메인으로 보이지 않고) IP, Port번호를 보여준다.
  * **-a** : 모든 네트워크상태를 보여준다.
  * **-t** : TCP 프로토콜만 보여준다.
  * **-u** : UDP 프로토콜만 보여준다.
  * **-p** : 해당 포트를 사용하는 프로그램과 프로세스ID(PID)를 보여준다.
  * **-r** : 라우팅 테이블 출력
  * **-s** : 프로토콜 별(IP, ICMP, TCP, UDP 등)로 통계를 보여준다
  * **-c** : 연속적으로 상태를 보여준다.
  * **-l** : 대기중인 소켓 목록을 보여준다.
* State
  * **공백** : 연결되어 있지 않음
  * **FREE** : socket은 존재하지만 할당되어 있지 않다.
  * **LISTENING** : 연결요청에 대한 응답준비가 되어 있는 상태
  * **CONNECTING** : 연결이 막 이루어진 상태.
  * **DISCONNECTING** : 연결해제 되고 있는 상태
  * **UNKNOWN** : 알 수 없는 연결, 알려지지 않은 연결 상태
  * **LISTEN** : 연결가능하도록 daemon이 떠있으며 연결이 가능한 상태
  * **SYS-SENT** : 연결을 요청한 상태.
  * **SYN_RECEIVED** : 연결요구에 응답 후 확인메세지 대기중인 상태
  * **ESTABLISHED** : 연결이 완료된 상태
  * **FIN-WAIT1** : 소켓이 닫히고 연결이 종료되고 있는 상태
  * **FIN-WAIT2** : 로컬이 원격으로부터 연결 종료 요구를 기다리는 상태
  * **CLOSE-WAIT** : 종료 대기 중
  * **CLOSING** : 전송된 메세지가 유실되었음
  * **TIME-WAIT** : 연결종료 후 한동안 유지되어 있음
  * **CLOSED** : 연결이 완전히 종료



* 옵션 사용 예시

  ```shell
  $ netstat -r # 서버의 라우팅 테이블 출력 
  $ netstat -na --ip # tcp/udp의 세션 목록 표시 
  $ netstat -na | grep ESTABLISHED | wc -l # 활성화된 세션수 확인 
  $ netstat -nap | grep :80 | grep ESTABLISHED | wc -l # 80포트 동시 접속자수 
  $ netstat -nltp # LISTEN 중인 포트 정보 표시
  ```



* 실시간으로 확인하려면 watch 명령 사용

  ```shell
  $ watch -n [delay] "명령"
  ```

  * ex ) watch -n 3 "netstat -na | grep ESTABLISHED | wc -l"



## df

현재 디스크의 전체 용량 및 남은 용량 확인

![20150903_df](http://tech.whatap.io/wp-content/uploads/2016/01/20150903_df.png)



* 옵션
  * -h : 용량을 읽기 쉽게 단위를 계산
  * -T : 파일 시스템 종류와 함께 디스크 정보 출력
  * --total : 전체 용량



## 테스트용 명령어 정리

* top
* vmstat 3
* iostat 3
* watch -n 3 "netstat -na | grep ESTABLISHED | grep 211:80 | wc -l"



## 참고

* http://tech.whatap.io/2015/09/03/linux-monitoring/

  ​