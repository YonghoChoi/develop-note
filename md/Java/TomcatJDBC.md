```java
PoolProperties p = new PoolProperties();
p.setUrl(Config.get("db_url", ""));
p.setDriverClassName(Config.get("db_driver", ""));
p.setUsername(Config.get("db_user", ""));
p.setPassword(Config.get("db_password", ""));
p.setJmxEnabled(true);
p.setTestWhileIdle(false);
p.setTestOnBorrow(true);
p.setValidationQuery("SELECT 1");
p.setTestOnReturn(false);
p.setValidationInterval(30000);
p.setTimeBetweenEvictionRunsMillis(30000);
p.setMaxActive(100);
p.setMaxIdle(20);
p.setInitialSize(10);
p.setMaxWait(10000);
p.setRemoveAbandonedTimeout(60);
p.setMinEvictableIdleTimeMillis(30000);
p.setMinIdle(10);
p.setLogAbandoned(true);
p.setRemoveAbandoned(true);
p.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"+
        "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
```

* maxWait : getConnection을 수행한 후 최대 대기 시간.
  * 이 값이 너무 크면 connection이 부족한 상태인데도 이 시간까지는 대기를 하게 되므로 좋지 않음
  * 사용자가 기다려줄 수 있는 시간 정도로 설정하는 것이 좋음. 예를 들어 브라우저가 먹통이면 사용자가 얼마만에 새로고침을 누를지 고려
  * 값이 너무 작을 경우 커넥션이 빈번하게 종료되므로 Eviction 작업 시간이 오래걸리거나 Full GC등의 이슈가 발생
  * 10초 정도 유지하면 될 듯
* timeBetweenEvictionRunsMillis : 정리 작업을 수행할 간격
  * 비용이 높은 작업이므로 너무 빈번하게 수행하면 성능 저하의 위험이 있음
  * 그렇다고 너무 드물게 수행되면 비정상적인 커넥션이 정리되지 못하는 경우가 발생할 수 있음
  * 하지만 비정상적인 커넥션이 생긴다는 것 자체가 문제이므로 근본적인 원인을 찾는 것이 중요하고, 애초에 그런 경우는 잘 발생하지 않음
  * Tomcat JDBC에서는 이 값이 너무 짧을 경우에 testWhileIdle 값이 true로 설정되어 있으면 DB에 너무 많은 select 쿼리가 전달되므로 적절히 늘리는 것이 좋음
  * 60초 정도가 적당 (주관적)
  * WAS와 DB 사이에 방화벽 정책이 존재한다면 방화벽에서 Idle 상태의 TCP를 끊어버릴 수 있으므로 이 값을 방화벽의 Timeout 보다 짧게 설정하고, testWhileIdle 설정을 활성화하여 연결이 끊어지도록 설정할 수 있다.
* numTestsPerEvictionRun : 정리 시 한번에 처리할 커넥션 수
  * Eviction 작업은 비용이 높은 작업이고, 작업 시간 동안 DB Pool에 Lock이 걸리므로 getConnection이 block된다.
  * 이 값을 크기와 Lock이 걸리는 시간이 비례하기 때문에 너무 크게 설정하는 것은 좋지 않다.
* minEvictableIdleTimeMillis : 정리 작업 수행 시 설정한 값보다 오랜 시간 Idle 상태인 경우 Connection을 회수한다.
  * Connection이 정리되는 것을 원치 않는다면 -1로 설정
* testOnBorrow : 커넥션을 대여할 경우(getConnection을 할 때) SQL로 검증
  * getConnection 수행 시 connection을 검증 후에 제공.
  * 검증 절차가 들어가게 되므로 성능은 조금 떨어질 수 있음
  * 커넥션에 문제가 발생하여 이 설정을 활성화 시킨 후에 해결이 되었다면 방화벽이나 DB timeout 값을 확인 해보고 그 값보다 짧은 간격으로 testWhileIdle이 되도록 설정하는 것을 권장함
* testOnConnection : connect 시 SQL을 통해 검증.
  * init 시에나 minIdle 등에 의해 connection이 생성 될 때 검증함
* testWhileIdle : timeBetweenEvictionRunsMillis 간격으로 수행되는 정리 작업 진행 시에 해당 connection에 SQL을 통해 이상 여부를 확인하고 이상이 있는 경우 제거한다.
  * 보통 이 설정에서 connection 검증을 몰아서 처리할 것을 권장함
  * testOnBorrow나 testOnConnect를 통해 처리하는 것이 안정적일 수 있으나 성능에 영향을 주기도 하고 그렇게까지 체크할 필요가 있나 싶음.
  * 이 기능을 활용하면 DB timeout, 방화벽 timeout, Application에서의 잘못된 connection 제어등에 의한 문제를 회피할 수 있다.
  * 이 설정은 validationQuery나 validatorClassName을 설정하지 않으면 동작하지 않음
* removeAbandoned : 이상 상태(오래 수행중인 경우) 시 제거 작업 활성화
* removeAbandonedTimeout : 정리 작업 수행 시 커넥션이 설정한 시간 이상 수행상태이면 제거
  * 수행 시간 기준으로 무조건 연결이 끊어짐
  * 시간 값이 너무 짧으면 처리가 오래걸리는 쿼리까지 제거될 수 있음
  * 일반적으로 이 설정은 활성화 하지 않음.
* logAbandoned : 활성화 하면 시간이 경과되어 커넥션이 끊어지는 내용을 로그로 남김