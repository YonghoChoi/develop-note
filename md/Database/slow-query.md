## slow query에 commit만 찍히는 경우

slow query 로그를 보면 쿼리 정보 없이 아래와 같이 timestamp와 commit 만 찍히는 경우가 있다. 

```
# User@Host: hive[hive] @  [172.19.0.1]
# Thread_id: 69  Schema: hive_game_1  QC_hit: No
# Query_time: 1.592948  Lock_time: 0.000000  Rows_sent: 0  Rows_examined: 0
use hive_game_1;
SET timestamp=1490077464;
commit;
```

이런 경우는 트랜잭션을 사용할 경우에 발생하는데 아래와 같이 다른 slow 쿼리와 비교해보면 use와 SET timestamp는 동일하게 수행된다. 

```
# User@Host: hive[hive] @  [172.19.0.1]
# Thread_id: 58  Schema: hive_game_1  QC_hit: No
# Query_time: 1.605137  Lock_time: 0.000050  Rows_sent: 0  Rows_examined: 0
SET timestamp=1490077464;
insert into Mail(UID, MID, MType, Sender, ExpireDt) values(4709273128878385119,1,1,'Hive','2017-04-20 15:24:22');
```

결국 트랜잭션 처리 마지막에 commit을 수행할 때 처리가 지연되어 트랜잭션 안의 다른 쿼리들은 출력되지 않고 commit만 출력된 것이다.

트랜잭션에 묶인 쿼리들을 확인해보고 싶어서 방법을 찾아봤는데 general log 파일의 스레드 번호에 따라 순서대로 보는것 외에는 딱히 방법이 없었다. 그래서 테스트 시에는 트랜잭션으로 묶지 않고 테스트 후 slow 쿼리를 잡아낸 후 다시 트랜잭션을 걸어주는 식으로 작업했다. 



## innodb_flush_log_at_trx_commit 옵션

mysql이 크래쉬 날 경우를 대비하여 디스크에 플러시 하는 기능이 있는데 기본 1초로 설정이 되어 있다. 이 옵션을 0으로 설정한 후 테스트를 하니 약 8배 정도 성능이 빨라졌었다. 

```
innodb_flush_log_at_trx_commit = 0
```

