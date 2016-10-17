# 04. Environment,프로퍼티,프로필,메시지

* Environment를 이용한 프로퍼티, 프로파일
    * 환경에 따라서 다른 값을 사용하는 방법
* 다국어 지원을 위한 메시지 기능

## 01. Environment 소개

* 스프링 설정 변경 없이 외부에서 입력한 정보를 이용해서 설정 값을 변경
    * Environment

* Environment
    * 프로퍼티 통합 관리
    * 프로필을 이용해서 선택적으로 설정 정보를 사용할 수 있는 방법
    * 시스템 환경변수, JVM 시스템 프로퍼티, 프로퍼티 파일들의 프로퍼티를 `PropertySource` 라는 것으로 통합관리.
        * 설정 파일이나 클래스 수정이 `시스템 프로퍼티`나 `프로퍼티 파일`등을 이용해 설정 정보의 일부를 변경 가능
    * 여러 `Profile` 중에서 특정 프로필을 활성화하는 기능 제공
        * 개발환경, 통합 테스트 환경, 실 서비스 환경에 따라 다른 스프링 빈 설정을 선택할수 있음.
        * 서로 다른 환경을 위한 설정 정보를 편하게 관리 가능.

### 1.1 Environment 구하기

* ConfigurableApplicationContext 에 정의된 getEnvironment()메소드를 이용해 Environment 를 구함

```java
import org.springframework.core.env.ConfigurableEnvironment;

...

ConfigurableApplicationContext context = new AnnotationConfigApplicationContext();
ConfigurableEnvironment environment = context.getEnvironment();
environment.setActiveProfiles("dev");
```



## 02. Environment와 PropertySource

* 프로퍼티 값을 제공하는 기능
    * 다수의 PropertySource로부터 프로퍼티 값을 읽어온다.

![Spring4-1.jpeg](.\Spring4-1.jpeg "Environment의 프로퍼티 읽는 과정" "width:70%")

* o.s.core.env.MutablePropertySources 에 두 개 이상의 PropertySource가 등록되어 있을 경우
    * 프로퍼티 값을 구할 때까지 ==등록된 순서==에 따라 차례대로 확인
    * ex)
        * JAVA_HOME 인 환경 변수가 존재하고, 우선순위가 그림에서 왼쪽이 높고, 오른쪽이 낮다고 가정하면
        * 시스템 프로퍼티 PropertySource로 부터 JAVA_HOME 프로퍼티를 찾는다. (결국은 ==환경 변수 PropertySource== 에서 반화)

* 스프링은 ==시스템 프로퍼티==와 ==환경 변수==를 사용하는 두 개의 PropertySource를 기본적으로 사용
    * 우선 순위는 ==**시스템 프로퍼티를 사용하는 PropertySource가 높다**==.
    * (Environment의 설정을 변경하지 않는 이상) ==**시스템 프로퍼티로부터 먼저 값을 찾고, 그 다음에 환경 변수**==로부터 찾는다.

> 자바의 시스템 프로퍼티
> 설정 방식
> * Java 실행에 -D 옵션 지정
>   * `$java -Djdbc.user=dbuser1 ...`
> * System.setProperty() 메소드 사용
>   * `System.setProperty("jdbc.user", "dbuser1");`

### 2.1 Environmen에서 프로퍼티 읽기

*   Environment로부터 프로퍼티 읽는 방법
    * Environment를 구한뒤에 Environment 의 프로퍼티 관련 메소드 이용.

    ```java
    ConfigurableApplicationContext context = new GenericXmlApplicationContext();
    ConfigurableEnvironment env = context.getEnvironment();
    String javaVersion = env.getProperty("java.version");
    System.out.println("Java version is %s", javaVersion);
    ```

*   Environment가 제공하는 프로퍼티 관련 주요 메소드

| 메소드                                      | 설명                                       |
| ---------------------------------------- | ---------------------------------------- |
| boolean containsPropety(String key)      | 지정한 key에 해당하는 프로퍼티가 존재하는지 확인             |
| String getProperty(String key)           | 지정한 key에 해당하는 프로퍼티 값을 구한다. 존재하지 않으면 null 리턴 |
| String getProperty(String key, String defaultValue) | 지정한 key에 해당하는 프로퍼티 값을 구한다. 존재하지 않으면, defaultValue 를 리턴 |
| String getRequiredProperty(String key) throws IllegalStateException | 지정한 key에 해당하는 프로퍼티 값을 구한다. 존재하지 않으면 Exception 발생 |
| <T> T getProperty(String key, Class<T> targetType) | 지정한 key에 해당하는 프로퍼티 값을 targetType으로 변환해서 구한다. 존재하지 않으면 null 리턴 |
| <T> T getProperty(String key, Class<T> targetType, T defaultValue) | 지정한 key에 해당하는 프로퍼티 값을 targetType으로 변환해서 구한다. 존재하지 않으면, defaultValue를 리턴 |
| <T> getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException | 지정한 key에 해당하는 프로퍼티의 값을 targetType으로 변환해서 구한다. 존재하지 않을 경우 익셉션을 발생 |


### 2.2 Environment에 새로운 PropertySource 추가하기

* 스프링 Environment는 기본적으로 환경변수와 시스템 프로퍼티만 사용
    * 프로퍼티 파일을 Environment의 프로퍼티로 추가하려면, ==**PropertySource를 추가**== 해야한다.
    * addLast() : 파라미터로 전달한 PropertySource를 마지막 PropertySource로 등록(우선순위 최하)
    * addFirst() : 파라미터로 전달한 PropertySource를 첫번째 PropertySource로 등록(우선순위 최상)

```java
ConfigurableEnvironment env = context.getEnvironment();
MutablePropertySources propertySources = env.getPropertySources();
propertySources.addLast(new ResourcePropertySource("classpath:/db.properties"));
String dbUser = env.getProperty("db.user");
```

* `o.s.core.io.support.ResourcePropertySource`
    * 자바 프로퍼티 파일로부터 값을 읽어오는 PropertySource 구현 클래스
* `o.s.core.io.support.PropertiesPropertySource`
    * 자바의 Properties 객체로부터 프로퍼티 값을 읽어오는 구현 클래스
* `o.s.core.io.support.JndiPropertySource`
    * 디렉토리 서버에서 프로퍼티 값을 읽어오는 구현 클래스


#### @Configuration 어노테이션 기반 자바 설정

* @PropertySource 어노테이션 이용

```java
@Configuration
@PropertySource("classpath:/db.properties")
public class ConfigByEnv {

    @Autowired
    private Environment env;

    @Bean(initMethod = "init")
    public ConnectionProvider connectionProvider() {
        JdbcConnectionProvider connectionProvider = new JdbcConnectionProvider();
        connectionProvider.setDriver(env.getProperty("db.driver"));
        connectionProvider.setUrl(env.getProperty("db.jdbcUrl"));
        connectionProvider.setUser(env.getProperty("db.user"));
        connectionProvider.setPassword(env.getProperty("db.password"));
        return connectionProvider;
    }
}

```

* 두개 이상의 프로퍼티 파일 사용
* 자원이 없는 경우 익셉션을 발생하지 않고 무시하고 싶다면.
    * `ignoreResourceNotFound` 속성을 ==**true**==로 설정

```java
@PropertySource(value={"classpath:/db.properties", "classpath:/app.properties"}, ignoreResourceNotFound=true)
```

* @PropertySource 자체를 두 개 이상 설정할 때에는 `@PropertySources` 어노테이션을 사용

```java
@Configuration
@PropertySources({
	@PropertySource("classpath:/db.properties"),
	@PropertySource("classpath:/app.properties", ignoreResourceNotFound=true)
})
public class ConfigByEnv {
```

* Java8의 경우, @PropertySource어노테이션은 @Repeatable 을 적용하고 있으므로, @PropertySource 어노테이션을 여러번 사용하면 된다.

```java
@Configuration
@PropertySource("classpath:/db.properties"),
@PropertySource("classpath:/app.properties", ignoreResourceNotFound=true)
public class ConfigByEnv {
```

## 03. Environment를 스프링 빈에서 사용하기

* 스프링 빈에서 Environment 에 직접 접근하기
    * ==**o.s.context.EnvironmentAware 인터페이스 구현**==
    * ==**@Autowired 어노테이션을 Environment 필드에 적용**==


* o.s.context.EnvironmentAware 인터페이스 구현
    * setEnvironment() 메소드 이용

```java
package org.springframework.context;

import org.springframework.beans.factory.Aware;
import org.springframework.core.env.Environment;

public interface EnvironmentAware extends Aware {
	void setEnvironment(Environment environment);
}
```

```java
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

public class ConnectionProvider implements EnvironmentAware {
	private String driver;
	private String user;
	private String password;
	private String url;
	private Environment env;

	@Override
	public void setEnvironment(Environment environment) {
		this.env = environment;
	}

	public void init() {
		driver = env.getProperty("db.driver");
		url = env.getProperty("db.jdbcUrl");
		user = env.getProperty("db.user");
		password = env.getProperty("db.password");
	}
}
```


*   애노테이션 기반 의존 설정 기능이 활성화 되어 있다면 @Autowired 어노테이션을 이용해 Environment에 접근 할 수 있다.

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public class ConnectionProvider {
    private String driver;
    private String user;
    private String password;
    private String url;

    @Autowired
    private Environment env;

    public void init() {
        driver = env.getProperty("db.driver");
        url = env.getProperty("db.jdbcUrl");
        user = env.getProperty("db.user");
        password = env.getProperty("db.password");
    }
}
```

* @Configuration 기반 자바 설정 코드에서도 동일하게 사용 할 수 있다.

```java
package chap04.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import chap04.ConnectionProvider;

@Configuration
public class SpringConfig {

    @Autowired
    private Environment env;

    @Bean(initMethod = "init")
    public ConnectionProvider connectionProvider() {
        ConnectionProvider connectionProvider = new ConnectionProvider();
        connectionProvider.setDriver(env.getProperty("db.user"));
        connectionProvider.setUrl(env.getProperty("db.jdbcUrl"));
        connectionProvider.setUser(env.getProperty("db.user"));
        connectionProvider.setPassword(env.getProperty("db.password"));

        return connectionProvider;

    }
}
```

## 04. 프로퍼티 파일을 이용한 프로퍼티 설정

* ==스프링 빈 객체의 프로퍼티나 생성자 값을 설정하기 위해 Environment를 직접 사용하는 경우는 드물다.==


### 4.1 XML에서의 프로퍼티 설정 : `<context:property-placeholder>` 사용

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:context="http://www.springframework.org/schema/context"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context.xsd">

	<context:property-placeholder location="classpath:/db.properties, classpath:/app.properties" />

	<bean id="connProvider" class="chap04.JdbcConnectionProvider"
		init-method="init">
		<property name="driver" value="${db.driver}" />
		<property name="url" value="${db.jdbcUrl}" />
		<property name="user" value="${db.user}" />
		<property name="password">
			<value>${db.password}</value>
		</property>
	</bean>
</beans>
```


* placeholder(`${}`) 치환
* `<context:property-placeholder>` 태그 속성
    * file-encoding
        * 파일을 읽어올때 사용할 인코딩
        * 이 값이 없으면, native2ascii 를 이용해서 생성 필요
    * ignore-resource-not-found
        * 기본값 : ==**false**==
        * true : location 속성에 지정한 자원이 없어도 익셉션 발생 안함
        * false : location 속성에 지정한 자원이 없어도 익셉션 발생
    * ignore-unresolvable
        * 기본값 : ==**false**==
        * true : placeholder에 일치하는 프로퍼티가 없어도 익셉션 발생 안함.
        * false : placeholder에 일치하는 프로퍼티가 없어도 익셉션 발생.


*   `<context:property-placeholder>`
    *   PropertySourcesPlaceholderConfigurer 을 빈으로 등록.
    *   location 으로 지정한 파일에서 프로퍼티 값을 찾을수 없으면, Environment의 프로퍼티를 확인함.
    *   전체 설정에서 이 태그를 두번 이상 사용할 경우, ==**첫번째 사용한 태그의 값이 우선순위를 갖는다**==.
        * app.properties파일의 프로퍼티만 읽어오고, db.properties 파일의 프로퍼티는 읽어오지 않는다.

        * 별도의 XML 파일에서  `<context:property-placeholder>`태그를 사용한다.

          ```java
          -- 프로퍼티 전용 XML
          <beans ...>
          	<context:property-placeholder
          			location="classpath:/db.properties, classpath:/app.properties" />
          </beans>
          ```

```xml
---- app-config.xml 
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:context="http://www.springframework.org/schema/context"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context.xsd">

	<context:property-placeholder location="classpath:/app.properties" />
	
	<bean id="chargeCalculator" class="chap04.ChargeCalculator">
		<property name="batchSize" value="${calc.batchSize}" />
		<property name="connectionProvider" ref="connProvider" />
	</bean>
</beans>

----- db-config.xml
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:context="http://www.springframework.org/schema/context"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context.xsd">

	<context:property-placeholder location="classpath:/db.properties" />

	<bean id="connProvider" class="chap04.JdbcConnectionProvider"
		init-method="init">
		<property name="driver" value="${db.driver}" />
		<property name="url" value="${db.jdbcUrl}" />
		<property name="user" value="${db.user}" />
		<property name="password">
			<value>${db.password}</value>
		</property>
	</bean>
</beans>
```


> **PropertySourcesPlaceholderConfigurer의 동작 방식**
>
> * `context:property-placeholder>` 태그는 내부적으로 `PropertySourcesPlaceholderConfigurer` 객체를 빈으로 등록
> * 다른 빈들보다 먼저 생성 되기 위해 `BeanFactoryPostProcessor` 인터페이스를 구현하고 있음.
>
> **BeanFactoryPostProcessor 인터페이스**
> * 스프링은 설정 정보를 읽은뒤에 `BeanFactoryPostProcessor`를 구현한 클래스가 있으면, ==**가장 먼저 생성**==한다.


## 4.2 Configuration 어노테이션을 이용하는 자바 설정에서의 프로퍼티 사용
* @Configuration
    * PropertySourcesPlaceholderConfigurer
    * @Value 어노테이션 

* PropertySourcesPlaceholderConfigurer 
    * ==**설정하는 메소드는 정적(static)메소드**==
    * 특수한 목적의 빈(BeanFactoryPostProcessor 타입)이기 때문에 ==**정적 메소드로 지정해야한다**==.


```java
@Configuration
public class ConfigByProp {

	@Value("${db.driver}")
	private String driver;
	@Value("${db.jdbcUrl}")
	private String jdbcUrl;
	@Value("${db.user}")
	private String user;
	@Value("${db.password}")
	private String password;

	@Bean
	public static PropertySourcesPlaceholderConfigurer properties() {
		PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
		configurer.setLocation(new ClassPathResource("db.properties"));
		return configurer;
	}

	@Bean(initMethod = "init")
	public ConnectionProvider connectionProvider() {
		JdbcConnectionProvider connectionProvider = new JdbcConnectionProvider();
		connectionProvider.setDriver(driver);
		connectionProvider.setUrl(jdbcUrl);
		connectionProvider.setUser(user);
		connectionProvider.setPassword(password);
		return connectionProvider;
	}
}
```

* PropertySourcesPlaceholderConfigurer 클래스

| Method                                   | 설명                                       |
| ---------------------------------------- | ---------------------------------------- |
| setLocation(Resource location)           | location을 프로퍼티 파일로 사용                    |
| setLocations(Resources[] locations)      | locations를 프로퍼티 파일로 사용                   |
| setFileEncoding(String encoding)         | 파일을 읽어올때 사용할 인코딩 지정                      |
| setIgnoreREsourceNotFound(boolean b)     | true를 전달하면, 자원을 찾을수 없어도 익셉션 발생하지 않음      |
| setIgnoreUnresolvablePlaceholders(boolean b) | true를 전달하면, placeholder에 해당하는 프로퍼티를 찾을수 없어도 익셉션 발생하지 않음 |

---

> **Resource 인터페이스**
> 1. o.s.core.io.ClassPathResource : 클래스 패스에 위치한 자원으로 부터 읽음
> 2. o.s.core.io.FileSystemResource : 파일 시스템에 위치한 자원으로부터 데이터 읽음.

---


> **@Value 어노테이션**
> 스프링에서 프로퍼티 값을 설정할 때 사용할 수 있는 어노테이션
> 스프링에서 @Configurtion 어노테이션 사용한 설정 클래스는 ==**빈 객체로 생성**==.
> 스프링에서 @Configuration 어노테이션 사용 클래스에서 ==**@Value 붙은 필드는 빈의 프로퍼티로 인식**==.

---

* `@PropertySource` 어노테이션과 `PropertySourcesPlaceholderConfigurer` 를 함께 사용
    * @PropertySource 어노테이션 : Environment 에 프로퍼티를 추가.
    * PropertySourcesPlaceholderConfigurer 에 프로퍼티가 존재하지 않을 경우, Environment 에서 프로퍼티값 사용

```java
@Configuration
@PropertySources(@PropertySource("classpath:/db.properties"))
public class ConfigByPropSource {

	@Bean
	public static PropertySourcesPlaceholderConfigurer properties() {
		PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
		return configurer;
	}

	@Value("${db.driver}")
	private String driver;
	@Value("${db.jdbcUrl}")
	private String jdbcUrl;
	@Value("${db.user}")
	private String user;
	@Value("${db.password}")
	private String password;

	@Bean(initMethod = "init")
	public ConnectionProvider connectionProvider() {
		JdbcConnectionProvider connectionProvider = new JdbcConnectionProvider();
		connectionProvider.setDriver(driver);
		connectionProvider.setUrl(jdbcUrl);
		connectionProvider.setUser(user);
		connectionProvider.setPassword(password);
		return connectionProvider;
	}
}
```

## 05. 프로필을 이용한 설정

```mermaid
graph LR;
A(각 환경에 맞는 설정 정보를 따로 만들고, 환경에 알맞은 설정 정보 사용) --> B(스프링의 Profile);
```

### 5.1 XML설정에서 프로필 사용하기

* `<bean>` 패스의 ==**profile 속성**==에서 프로필 이름 지정.

```xml

--- datasource-dev.xml
<?xml version="1.0" encoding="UTF-8"?>

<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd"
    profile="dev">

	<bean id="connProvider" class="chap04.JdbcConnectionProvider"
		init-method="init">
		<property name="driver" value="${db.driver}" />
		<property name="url" value="${db.jdbcUrl}" />
		<property name="user" value="${db.user}" />
		<property name="password">
			<value>${db.password}</value>
		</property>
	</bean>
</beans>

--- datasource-prod.xml
<?xml version="1.0" encoding="UTF-8"?>

<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd"
    profile="prod">

	<bean id="connProvider" class="chap04.JndiConnectionProvider">
		<property name="jndiName" value="java:/comp/env/jdbc/db" />
	</bean>
</beans>
```

* 특정 프로필 선택하기 위해
    * `ConfigurableEnvironment`의 ==**setActiveProfiles()**== 메소드 사용

```java
GenericXmlApplicationContext context = new GenericXmlApplicationContext();
context.getEnvironment().setActiveProfiles("dev");
context.load(
        "classpath:/confprofile/app-config.xml",
        "classpath:/confprofile/datasource-dev.xml",
        "classpath:/confprofile/datasource-prod.xml"
        );
context.refresh();
```

* 두개 이상의 프로필 선택

```java
context.getEnvironment().setActiveProfiles("dev", "mysql");
```

* `spring.profiles.active` 시스템 프로퍼티에 프로필값 지정
    * 두 개 이상인 경우 콤마로 구분 설정

```java
java -Dspring.profiles.active=prod,oracle Main
```
```bash
//환경 변수 설정을 통한 방법
export spring.profile.active=prod,oracle
```

#### (1) `<beans>` 태그 중첩과 프로필

* `<beans>` 태그를 중첩해서 프로필을 설정 : 같은 목적을 위해 사용되는 빈 설정을 모을수 있음.(관리 용이)
* `<beans>` 태그 이후에 `<bean>` 태그가 올수 없다.


```xml
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:context="http://www.springframework.org/schema/context"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context.xsd">

	<context:property-placeholder
		location="classpath:/db.properties, classpath:/app.properties" />

	<bean id="chargeCalculator" class="chap04.ChargeCalculator">
		<property name="batchSize" value="${calc.batchSize}" />
		<property name="connectionProvider" ref="connProvider" />
	</bean>

	<beans profile="dev">
		<bean id="connProvider" class="chap04.JdbcConnectionProvider"
			init-method="init">
			<property name="driver" value="${db.driver}" />
			<property name="url" value="${db.jdbcUrl}" />
			<property name="user" value="${db.user}" />
			<property name="password">
				<value>${db.password}</value>
			</property>
		</bean>
	</beans>

	<beans profile="prod">
		<bean id="connProvider" class="chap04.JndiConnectionProvider">
			<property name="jndiName" value="java:/comp/env/jdbc/db" />
		</bean>
	</beans>
</beans>
```


### 5.2 자바 @Configuration 설정에서 프로필 사용

* `@Profile` 어노테이션 이용

```java
@Configuration
@Profile("prod")
public class DataSourceProdConfig {

	@Bean
	public ConnectionProvider connProvider() {
		JndiConnectionProvider provider = new JndiConnectionProvider();
		provider.setJndiName("java:/comp/env/jdbc/db");
		return provider;
	}
}
```

```java
AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
context.getEnvironment().setActiveProfiles("dev");
context.register(ApplicationConfig.class, DataSourceDevConfig.class, DataSourceProdConfig.class);
context.refresh();
```


#### (1) 중첩 @Configuration을 이용한 프로필 설정

* @Autowired : ConnectionProvider
* @Profile("dev"), @Profile("prod")
* @Configuration : `public public static class`

```java
@Configuration
public class ApplicationContextConfig {

	@Bean
	public static PropertySourcesPlaceholderConfigurer properties() {
		PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
		configurer.setLocations(new Resource[] {
				new ClassPathResource("db.properties"),
				new ClassPathResource("app.properties")
		});
		return configurer;
	}

	@Value("${calc.batchSize}")
	private int batchSize;
	@Autowired
	private ConnectionProvider connProvider;

	@Bean
	public ChargeCalculator chargeCalculator() {
		ChargeCalculator cal = new ChargeCalculator();
		cal.setBatchSize(batchSize);
		cal.setConnectionProvider(connProvider);
		return cal;
	}

	@Configuration
	@Profile("dev")
	public static class DataSourceDev {
		@Value("${db.driver}")
		private String driver;
		@Value("${db.jdbcUrl}")
		private String url;
		@Value("${db.user}")
		private String user;
		@Value("${db.password}")
		private String password;

		@Bean
		public ConnectionProvider connProvider() {
			JdbcConnectionProvider provider = new JdbcConnectionProvider();
			provider.setDriver(driver);
			provider.setUrl(url);
			provider.setUser(user);
			provider.setPassword(password);
			return provider;
		}
	}

	@Configuration
	@Profile("prod")
	public static class DataSourceProdConfig {

		@Bean
		public ConnectionProvider connProvider() {
			JndiConnectionProvider provider = new JndiConnectionProvider();
			provider.setJndiName("java:/comp/env/jdbc/db");
			return provider;
		}
	}
}
```

### 5.3 다수 프로필 설정

* 두 개 이상의 프로필 

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd"
    profile="prod,QA">
```

```java
@Configuration
@Profile("prod,QA")
public class DataSourceProdConfig {
```

* !사용(활성화 되지 않았을때 사용한다.)
    * prod 프로필이 활성화되지 않을 경우 사용.

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd"
    profile="!prod">
```

## 6. MessageSource를 이용한 메시지 국제화 처리

*   스프링은 메시지의 국제화 지원을 위해
    * o.s.context.MessageSource 인터페이스 제공

      ```java
      public interface MessageSource {
        String getMessage(String code, Object[] args, String defaultMessage, Locale locale);
        
        String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException;
        
        String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException;
      }
      ```
*   ApplicationContext는 등록된 빈 객체 중에서 이름이 **==messageSource==**인 `MessageSource` 타입의 빈 객체를 이용하여 메시지 가져옴
    * ApplicaionContext를 이용하여 메시지를 가져오려면, 빈 이름이 ==**messageSource**== 이어야한다.

```xml
<bean id="messageSource"
    class="org.springframework.context.support.ResourceBundleMessageSource">
    <property name="basenames">
        <list>
            <value>message.greeting</value>
        </list>
    </property>
    <property name="defaultEncoding" value="UTF-8" />
</bean>
```

### 6.1 프로퍼티 파일과 MessageSource

*   MessageSource의 주요 메소드
    * code : 메시지 식별 코드
    * String getMessage(String code, Object[] args, String defaultMessage, Locale locale)
    * String getMessage(String code, Object[] args, Locale locale)

*   사용 예제

    *   greeting.properties

        ```properties
        hello=안녕하세요?
        welcome={0}님, 환영합니다.
        ```

    *   java

        ```java
        messageSource.getMessage("hello", null, Locale.getDefault());

        String[] args = {"스프링"};
        messageSource.getMessage("welcome", args, Locale.getDefault());
        ```

*   스프링 기본 제공 구현 클래스

    * ResourceBundleMessageSource
    * ReloadableResourceBundleMessageSource

### 6.2 ResourceBundleMessageSource를 이용한 설정
* ResourceBundleMessageSource class 
    * MessageSource Interface 구현 클래스
    * java.util.ResourceBundle을 이용해서 메시지 읽음
    * basename 프로퍼티
        * 메시지를 로딩할 때 사용할 ResourceBundle 베이스 이름
        * 패키지를 포함한 완전한 이름
        * basename = message.greeting
            * message 패키지내의 greeting.properties 또는 greeting_<언어>.properties

```xml
<bean id="messageSource"
	class="org.springframework.context.support.ResourceBundleMessageSource">
	<property name="basenames">
		<list>
			<value>message.greeting</value>
			<value>message.error</value>
		</list>
	</property>
</bean>	
```

* ResourceBundle
    * message.greeting
        * greeting.properties : 기본 메시지. 시스템의 언어 및 지역에 맞는 프로퍼티 파일이 존재 하지 않을 경우
        * greeting_en.properties : 영어 메시지
        * greeting_ko.properties : 한글 메시지
        * greeting_en_UK.properties : 영국을 위한 영어 메시지
    * greeting.proeprties
        * 자바 5까지 유니코드를 직접 입력해야함
        * 자바 6부터는 캐릭터 인코딩 지정 가능

```
#greeting_en.properties
hello=Hello!

#greeting_ko.properties
hello=\uc548\ud558\uc138\uc694!
```

```xml
<bean id="messageSource"
	class="org.springframework.context.support.ResourceBundleMessageSource">
	<property name="basenames">
		<list>
			<value>message.greeting</value>
			<value>message.error</value>
		</list>
	</property>
	<property name="defaultEncoding" value="UTF-8"/>
</bean>
```

* ApplicationContext 
    * MessageSource 인터페이스 상속
    * 스프링 ==**빈 중에 이름이 'messageSource'**==인 MessageSource가 존재하면, 메시지 처리

```java
Locale locale = Locale.getDefault();
String greeting = context.getMessage("hello", null, locale);

Locale engLocale = Locale.ENGLISH;
String englishGreeting = context.getMessage("hello", null, engLocale);
```

### 6.3 RelocableResourceBundleMessageSource

* ResourceBundleMessageSource의 단점
    * classpath 이외의 다른곳에는 위치할 수 없다
    * 재시작해야만 변경된 내용이 반영

* ReloableResourceBundleMessageSource
    * classpath 이외 특정 디렉토리에 위치 시킬수 있다.
    * ==classpath 를 사용하지 않을 경우(파일을 사용), 변경된 내용이 반영==된다.
    * cacheSeconds 
        * -1 : 변경 내용을 반영하지 않는다.(기본값)
        * 0 : 메시지 요청시마다 확인
        * 그외 : 초 단위로 변경 내용 반영

```xml
<bean id="messageSource"
	class="org.springframework.context.support.ReloableResourceBundleMessageSource">
	<property name="basenames">
		<list>
			<value>file:src/message/greeting</value>
			<value>classpath:message/error</value>
		</list>
	</property>
	<property name="defaultEncoding" value="UTF-8"/>	
	<property name="cacheSeconds" value="10"/>	
</bean>	
```

### 6.4 빈 객체에 메시지 이용하기

* `ApplicationContextAware` 인터페이스를 구현한뒤, `setApplicationContext()` 메소드를 이용한다.
    * 3장 참조
* `MessageSourceAware` 인터페이스를 구현한뒤, `setMessageSource()` 메소드를 이용

```java
package org.springframework.context;

import org.springframework.beans.factory.Aware;

public interface MessageSourceAware extends Aware() {
	void setMessageSource(MessageSource messageSource);
}
```

```java
public class LoginProcessor implements MessageSourceAware {
	private MessageSource messageSource;
	
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	public void login(String username, String password) {
		...
		Object[] args = new String[]{username};
		String failMessage = messageSource.getMessage("login.fail",args, locale);
		...
	}
}
```