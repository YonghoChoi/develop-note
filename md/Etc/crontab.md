# crontab

* 리스트

  ```shell
  $ crontab -l
  $ crontab -l -u ec2-user
  ```

* 등록

  ```shell
  $ crontabl -e
  ```

* 등록 형식

  ```
  *  *  *  *  *  수행할 명령어
  ┬  ┬  ┬  ┬  ┬
  │  │  │  │  │
  │  │  │  │  │
  │  │  │  │  └───────── 요일 (0 - 6) (0:일요일, 1:월요일, 2:화요일, …, 6:토요일)
  │  │  │  └───────── 월 (1 - 12)
  │  │  └───────── 일 (1 - 31)
  │  └───────── 시 (0 - 23)
  └───────── 분 (0 - 59)
  ```

* 예시

  ```
  * * * * * /root/every_1min.sh
  ```

  * 매 1분마다 수행

  ```
  15,45 * * * * /root/every_30min.sh
  ```

  * 매시 15분, 45분에 수행

  ```
  */10 * * * * /root/every_10min.sh
  ```

  * 10분마다 수행

  ```
  0 2 * * * /root/backup.sh
  ```

  * 매일 02:00에 수행

  ```
  30 */6 * * * /root/every_6hours.sh
  ```

  * 매 6시간 마다 (00:30, 06:30, 12:30, 18:30) 수행

  ```
  30 1-23/6 * * * /root/every_6hours.sh
  ```

  * 1시부터 매 6시간마다(01:30, 07:30, 13:30, 19:30) 수행

  ```
  0 8 * * 1-5 /root/weekday.sh
  ```

  * 평일(월요일~금요일) 08:00에 수행

  ```
  0 8 * * 0,6 /root/weekend.sh
  ```

  * 주말(일요일, 토요일) 08:00에 수행



## 참고

* [리눅스 반복 예약작업 cron, crond, crontab](http://zetawiki.com/wiki/%EB%A6%AC%EB%88%85%EC%8A%A4_%EB%B0%98%EB%B3%B5_%EC%98%88%EC%95%BD%EC%9E%91%EC%97%85_cron,_crond,_crontab)
* [지연으로 인한 cron 작업 중복 실행 문제](http://bencane.com/2015/09/22/preventing-duplicate-cron-job-executions/)

