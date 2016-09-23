## Extract Adapter

하나의 클래스가 컴포넌트, 라이브러리, API등의 여러 버전을 동시에 지원하기 위한 어댑터 역할을 하고 있다면, 
각 버전을 위한 기능을 별도의 어댑터로 뽑아낸다.

![]()

### 동기

*문제의 원인*

* 소프트웨어를 개발하다 보면 컴포넌트, 라이브러리 또는 API를 동시에 여러 버전으로 지원해야 할 때가 있다. 
* 특정 버전만을 위한 상태 변수, 생성자, 메서드 등을 한 클래스에 오버로딩해 구현하는 경우를 볼 수 있다.
  * 이러한 코드에는 "다음 버전으로 옮겨갈 경우 이 코드는 삭제 할 것"과 같은 내용의 주석이 달리는 경우가 많다.
  * 하지만 다른 부분에서 문제가 생길지도 모른다는 걱정에 코드는 삭제되지 않고 여러 버전을 지원하는 코드가 그대로 남게 된다.

*방안*

* 각 버전을 지원하는 별도의 클래스를 만든다. (이름에 지원하는 버전을 명시하는 것도 좋다.)
  * 이러한 클래스를 어댑터라 부른다.
  * 어댑터는 공통 인터페이스를 구현하고, 특정 버전의 코드에 대해 정확히 동작해야한다.
* 오픈 소스 라이브러리의 경우 API가 끊임없이 바뀌는 경우가 많은데, 어댑터로 감싸 API의 변경에 대응하는 것이 좋다.

### Adapter와 Facade

* Adapter 패턴은 객체 수준에서의 어댑팅.
* Facade(퍼사드) 패턴은 어떤 서브 시스템 전체를 어댑팅.
  * 새 시스템에 설계가 좋지 않고 복잡한 레거시 코드에 대한 좀 더 단순한 뷰를 제공.
  * 새 시스템은 Facade 객체와 통신하고, 이 Facade 객체가 레거시 코드와 관련된 복잡한 작업을 대신한다.

#### Facade 패턴 적용 과정

1. 주어진 레거시 시스템의 서브 시스템을 확인.
2. 그 서브시스템을 위한 퍼스드를 구현.
3. 앞서 만든 퍼사드를 사용하도록 클라이언트 코드를 수정.
4. 레거시 시스템의 기능을 신기술로 다시 구현하고 그를 어댑팅하는 새 퍼사드를 만든다.
5. 기존의 퍼사드와 새 퍼사드가 동일하게 동작하는지 테스트.
6. 새 퍼사드를 사용하도록 클라이언트 코드 수정.
7. 나머지 서브시스템에 대해서 위의 과정 반복.

### 장점과 단점

```
+ 컴포넌트,라이브러리 또는 API의 버전에 따른 차이점을 격리한다.
+ 클래스가 하나의 버전만 책임지도록 한다.
+ 자주 변하는 코드를 시스템과 분리할 수 있다.
- 원래 있던 중요 기능을 어댑터에서 제공하지 못하면, 클라이언트가 그런 중요 기능에 접근하는데 장벽이 될 수 있다.
```

### 절차

1. 여러 버전의 코드를 어댑팅하기 위해 과중한 책임을 떠맡고 있는 어댑터 클래스를 찾는다.
2. 과중한 책임을 맡고 있는 어댑터 클래스에 Extract Subclass/Class 리팩터링을 적용해 특정 버전에 종속적인 부분을 각각 별도의 클래스로 뽑아낸다.
3. 기존의 어댑터 클래스에 버전 종속적인 코드가 모두 사라질 때까지 2를 반복한다.
4. 새로 만든 어댑터 클래스들 사이에 존재하는 중복 코드는 Pull Up Method 또는 Form Template Method 리팩터링을 통해 제거한다.

### 예제

요약 : 써드파티 라이브러리(SuperDatabase)를 이용해 데이터베이스 쿼리를 처리하는 코드.

1. 여러 버전의 SuperDatabase를 지원하기 위해 과중한 책임을 떠맡고 있는 어댑터를 찾는다.
  * Query 클래스는 SuperDatabase의 버전 5.1과 5.2를 지원.
```java
public class Query...
    private SDLogin sdLogin;    // SD 5.1
    private SDSession sdSession;    // SD 5.1
    private SDLoginSession sdLoginSession;  // SD 5.2
    private boolean sd52;   // SD 5.2로 동작하고 있음을 나타내는 플래그
    private SDQuery sdQuery;    // SD 5.1, 5.2 모두
    
    // SD 5.1을 위한 로그인 메서드
    // 주의 : 모든 애플리케이션이 5.2로 전환하면, 이 코드를 삭제할 것.
    public void login(String server, String user, String password) throws QueryException {
        sd52 = false;
        try {
            sdSession = sdLogin.loginSession(server, user, password);
        } catch (SDLoginFailedException lfe) {
            throw new QueryException(QueryException.LOGIN_FAILED, "Login failure\n" + lfe, lfe);
        } catch (SDSocketInitFailedException ife) {
            throw new QueryException(QueryException.LOGIN_FAILED, "Socket fail\n" + ife, ife);
        }
    }

    // SD 5.2를 위한 로그인 메서드
    public void login(String server, String user, String password, String sdConfigFileName) throws QueryException {
        sd52 = true;
        sdLoginSession = new SDLoginSession(sdConfigFileName, false);
        try {
            sdLoginSession.loginSession(server, user, password);
        } catch (SDLoginFailedException lfe) {
            throw new QueryException(QueryException.LOGIN_FAILED, "Login failure\n" + lfe, lfe);
        } catch (SDSocketInitFailedException ife) {
            throw new QueryException(QueryException.LOGIN_FAILED, "Socket fail\n" + ife, ife);
        } catch (SDNotFoundException nfe) {
            throw new QueryException(QueryException.LOGIN_FAILED, "Not Found Exception\n" + nfe, nfe);
        }
    }

    public void doQuery() throws QueryException {
        if(sdQuery != null) {
            sdQuery.clearResultSet();
        }

        if(sd52) {
            sdQuery = sdLoginSession.createQuery(SDQuery.OPEN_FOR_QUERY);
        } else {
            sdQuery = sdSession.createQuery(SDQuery.OPEN_FOR_QUERY);
        }

        executeQuery();
    }
```

2. Query 클래스에서 Extract Subclass 리팩터링으로 SuperDatabase 5.1을 위한 코드를 분리하기 위해 서브 클래스 정의.
```java
class QuerySD51 extends Query {
    public QuerySD51() {
        super();
    }
}
```
3. 클라이언트 코드에서 Query의 생성자를 호출하는 부분을 모두 찾아 적절한 곳이라면(5.1버전 사용) QuerySD51 생성자를 호출.
  * 이 때 생성자를 무조건 바꾸면 안된다.
  ```java
  public void loginToDatabase(String db, String user, String password) {
      query = new Query
      try {
          if(usingSDVersion52()) {
              query.login(db, user, password, getSD52ConfigFileName()); // SD 5.2로 로그인
          } else {
              query.login(db, user, password);  // SD 5.1로 로그인
          }
          ...
      } catch(QueryException qe) ...
  }
  ```
  * 위의 경우 query 생성자를 무조건 바꾸면 SDVersion52를 사용하는 부분에서 오류가 난다.
  * 아래와 같이 수정.
  ```java
  public void loginToDatabase(String db, String user, String password) {
      //query = new Query
      try {
          if(usingSDVersion52()) {
              query = new Query();
              query.login(db, user, password, getSD52ConfigFileName()); // SD 5.2로 로그인
          } else {
              query = new QuerySD51;
              query.login(db, user, password);  // SD 5.1로 로그인
          }
          ...
      } catch(QueryException qe) ...
  }
  ```
4. QuerySD51이 필요한 메서드와 필드를 가질 수 있도록 Push Down Method와 Push Down Field 리팩터링을 적용.
  * public 메서드 리팩토링 시 주의. 
    * 메서드를 옮기면 기존에 Query를 통해 메서드를 호출하던 클라이언트는 QuerySD51로 형변환을 하지 않으면 해당 메서드를 사용할 수 없음.
    * 그래서 리팩토링 과정에서는 Query에서 완전히 제거하지 않고 중복이 생기더라도 복사와 수정을 병행.
    * 중복코드는 리팩토링의 마지막 단계에서 제거.
    ```java
    class Query...
        private SDLogin sdLogin;
        private SDSession sdSession;
        protected SDQuery sdQuery;

        // SD 5.1을 위한 로그인 메서드
        public void login(String server, String user, String password) throws QueryException {
            // 아무 작업도 하지 않음.
        }

        public void doQuery() throws QueryException {
            if(sdQuery != null) {
                sdQuery.clearResultSet();
            }

            //if(sd52)
                sdQuery = sdLoginSession.createQuery(SDQuery.OPEN_FOR_QUERY);
            //else
                //sdQuery = sdSession.createQuery(SDQuery.OPEN_FOR_QUERY);

            executeQuery();
        }
    ```
    ```java
    class QuerySD51 {
        private SDLogin sdLogin;
        private SDSession sdSession;

        public void login(String server, String user, String password) throws QueryException {
            sd52 = false;
            try {
                sdSession = sdLogin.loginSession(server, user, password);
            } catch (SDLoginFailedException lfe) {
                throw new QueryException(QueryException.LOGIN_FAILED, "Login failure\n" + lfe, lfe);
            } catch (SDSocketInitFailedException lfe) {
                throw new QueryException(QueryException.LOGIN_FAILED, "Socket fail\n" + ife, ife);
            }
        }

        public void doQuery() throws QueryException {
            if(sdQuery != null) {
                sdQuery.clearResultSet();
            }

            //if(sd52)
                //sdQuery = sdLoginSession.createQuery(SDQuery.OPEN_FOR_QUERY);
            //else
                sdQuery = sdSession.createQuery(SDQuery.OPEN_FOR_QUERY);

            executeQuery();
        }
    }
    ```
5. 테스트
6. 단계 3을 반복해 QuerySD52 클래스를 만들고 Query를 추상클래스로, doQuery()도 추상 메서드로 만든다.
7. 중복코드 제거
  * doQuery()에 코드 중복이 존재.
    ```java
    abstract class Query...
        public abstract void doQuery() throws QueryException;

    class QuerySD51 ...
        public void doQuery() throws QueryException {
            if(sdQuery != null) {
                sdQuery.clearResultSet();
            }

            sdQuery = sdSession.createQuery(SDQuery.OPEN_FOR_QUERY);
            executeQuery();
        }
	class QuerySD52 ...
        public void doQuery() throws QueryException {
            if(sdQuery != null) {
                sdQuery.clearResultSet();
            }

            sdQuery = sdLoginSession.createQuery(SDQuery.OPEN_FOR_QUERY);
            executeQuery();
        }
  ``` 
  * sdQuery 객체를 얻는 방식만 다르므로 Introduce Polymorphic Creation with Factory Method, Form Template Method 리팩토링을 통해 doQuery() 메서드를 super 클래스로 옮긴다.
  ```java
  abstract class Query...
    public abstract SDQuery createQuery();

    public void doQuery() throws QueryException {
        if(sdQuery != null)
            sdQuery.clearResultSet();
        sdQuery = createQuery();
        executeQuery();
    }
	class QuerySD51 ...
        protected SDQuery createQuery() {
            return sdSession.createQuery(SDQuery.OPEN_FOR_QUERY);
        }

      class QuerySD52 ...
        protected SDQuery createQuery() {
            return sdLoginSession.createQuery(SDQuery.OPEN_FOR_QUERY);
        }
  ```
  * Query의 login() 메서드는 아무일도 하지 않고, 두 서브 클래스의 login() 메서드의 시그니처는 파라미터 하나만 빼고 동일하다.
  ```java
  // SD 5.1 로그인
  public void login(String server, String user, String password) throws QueryException ...
  // SD 5.2 로그인
  public void login(String server, String user, String password, String sdConfigFileName) throws QueryException ...
  ```
8. sdConfigFileName 정보를 QuerySD52 클래스의 생성자를 통해 넘기면 login() 메서드의 시그니처를 동일하게 만들 수 있다.
```java
class QuerySD52 ...
    private String sdConfigFileName;
    public QuerySD52(String sdConfigFileName) {
        super();
        this.sdConfigFileName = sdConfigFileName;
    }
```
```java
abstract class Query ...
    public abstract void login(String server, String user, String password) throws QueryException ...
```
```java
public void loginToDatabase(String db, String user, String password) ...
    if(usingSDVersion52())
        query = new QuerySD52(getSD52ConfigFileName());
    else
        query = new QuerySD51();
    
    try {
        query.login(db, user, password);
        ...
    } catch(QueryException qe) ...
```
9. Query는 이제 추상 클래스가 되었으므로 의도를 명확하게 하기 위해 AbstractQuery로 바꾸는 것이 좋지만, Query를 사용하는 클라이언트 코드를 모두 찾아 바꿔주어야 한다.
  * Extract Interface 리팩터링을 적용하면 클라이언트 코드를 수정하지 않아도 된다.
  ```java
  interface Query {
      public void login(String server, String user, String password) throws QueryException;
      public void doQuery() throws QueryException;
  }
  ``` 
  ```java
  abstract class AbstractQuery implements Query ...
    /*public abstract void login(String server, String user, String password) throws QueryException*/
  ```
10. 테스트 후 리팩토링 종료.

* 얻게된 이점.
  * 각 버전 간의 유사점과 차이점을 쉽게 알아볼 수 있게 되었다.
  * 오래되어 사용되지 않는 버전을 위한 코드를 쉽게 제거할 수 있게 되었다.
  * 새 버전을 지원하는 일이 쉬워졌다.

### 변형

#### 익명 내부 클래스를 사용해 어댑팅하기

* JDK 1.0에서는 Enumeration 인터페이스를 통해 컬렉션을 순회했었다.
* 점점 더 발전하여 Iterator 인터페이스가 그 역할을 대신하게 되었다.
* Enumeration 인터페이스를 사용해 작성된 코드와도 상호 동작이 가능해야 하므로 JDK에는 익명 내부 클래스 기능을 이용해 Iterator를 어댑팅하는 생성 메서드를 제공한다.

```java
public class Collections ...
    public static Enumeration enumeration(final Collection c) {
        return new Enumeration() {
            Iterator i = c.iterator();

            public boolean hasMoreElements() {
                return i.hasNext();
            }

            public Object next Element() {
                return i.next();
            }
        }
    }
```