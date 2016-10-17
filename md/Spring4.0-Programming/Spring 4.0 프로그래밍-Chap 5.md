# 05. 확장 포인트와 PropertyEditor/ConversionService

[TOC]

## 01. 스프링 확장 포인트

- BeanFactoryPostProcessor 를 이용한 ==설정 정보 변경==
- BeanPostProcessor 를 이용한 ==빈 객체 변경==




### 1.1 BeanFactoryPostProcessor 를 이용한 빈 설정 정보 변경

- PropertySourcesPlaceholderConfigurer

  - BeanFactoryPostProcessor 인터페이스 구현

  - ==빈 객체를 실제로 생성하기 전에 설정 메타 정보를 변경하기 위한 용도==

  - 스프링 설정 정보에서 placeholder를 프로퍼티 값으로 변경해주는 기능

  - ```xml
    <context:property-placeholder location="classpath:/db.properties"/>

    <bean id="connProvider" class="net.madviurs.spring4.chap04.ConnectionProvider" 
          init-method="init">
      <property name="driver" value="${db.driver}"/>
      ...생략
    </bean>
    ```

  - ```java
    @Configuration
    public class ConfigByProp {
        @Value("${db.drvier}")
        private String driver;

        @Bean
        public static PropertySourcesPlaceholderConfigurer properties() {
            PropertySourcesPlaceholderConfigurer configurer = 
                new PropertySourcesPlaceholderConfigurer();
            configurer.setLocation(new ClassPathResource("db.properties"));
            return configurer;
        }
    }
    ```

- BeanFactoryPostProcessor 

  - 설정 정보의 값 변경

  - 새로운 빈 설정 추가

  - `postProcessBeanFactory`에 전달되는 `ConfigurableListableBeanFactory`를 이용해서 설정 정보를 읽어 변경 또는 새로운 설정 정보 추가 가능

  - ```java
    public interface BeanFactoryPostProcessor {
        void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException;
    }
    ```

- ex) ThresholdRequired 인터페이스를 구현한 빈 객체가 threshold 프로퍼티의 값을 설정하지 않은 경우, 10으로 설정한다.

  - ```java
    package chap05;

    public interface ThresholdRequired {

    	public void setThreshold(int threshold);
    }
    ```

  - ```java
    package chap05;

    public class DataCollector implements ThresholdRequired {

    	private int threshold;

    	@Override
    	public void setThreshold(int threshold) {
    		this.threshold = threshold;
    	}

    	public int getThreshold() {
    		return threshold;
    	}

    }
    ```

  - ```java
    package chap05;

    import org.springframework.beans.BeansException;
    import org.springframework.beans.FatalBeanException;
    import org.springframework.beans.MutablePropertyValues;
    import org.springframework.beans.factory.config.BeanDefinition;
    import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
    import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

    public class ThresholdRequiedBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    	private int defaultThreshold;

    	public void setDefaultThreshold(int defaultThreshold) {
    		this.defaultThreshold = defaultThreshold;
    	}

    	@Override
    	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    		String[] beanNames = beanFactory.getBeanDefinitionNames();
    		for (String name : beanNames) {
    			BeanDefinition beanDef = beanFactory.getBeanDefinition(name);
    			Class<?> beanClass = getClassFromBeanDef(beanDef);
    			if (beanClass != null && ThresholdRequired.class.isAssignableFrom(beanClass)) {
    				MutablePropertyValues prop = beanDef.getPropertyValues();
    				if (!prop.contains("threshold")) {
    					prop.add("threshold", defaultThreshold);
    				}
    			}
    		}
    	}

    	private Class<?> getClassFromBeanDef(BeanDefinition beanDef) {
    		System.out.println(beanDef.toString());
    		if (beanDef.getBeanClassName() == null)
    			return null;
    		try {
    			return Class.forName(beanDef.getBeanClassName());
    		} catch (ClassNotFoundException e) {
    			throw new FatalBeanException(e.getMessage(), e);
    		}
    	}

    }
    ```

- 설명

  - 빈 이름 목록을 구한다

    - ```java
      String[] beanNames = beanFactory.getBeanDefinitionNames();
      ```

  - 지정한 이름을 가진 빈의 설정 정보를 구한다.

    - ```java
      BeanDefinition beanDef = beanFactory.getBeanDefinition(name);
      ```

  - 설정 정보에서 빈의 클래스 타입을 구한다.

    - ```java
      Class<?> beanClass = getClassFromBeanDef(beanDef);
      ```

  - 빈의 프로퍼티 설정 정보(MutablePropertyValues)를 구한다.

    - ```java
      MutablePropertyValues prop = beanDef.getPropertyValues();
      ```

  - 빈의 프로퍼티 설정 중에 "threshold" 프로퍼티의 값이 없을 경우에, defaultThreshold 값을 갖는 "threshold" 프로퍼티 추가.

    - ```java
      if (!prop.contains("threshold")) {

        prop.add("threshold", defaultThreshold);

      }
      ```

- ConfigurableListableBeanFactory 

  - String[] getBeanDefinitionNames()
    - 설정된 모든 빈의 이름을 구한다.
  - BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException 
    - 지정한 이름을 갖는 빈의 설정 정보를 구한다.

- ==**사용하기 위해 스프링 bean으로 등록**==.

  - ```xml
    <?xml version="1.0" encoding="UTF-8"?>

    <beans xmlns="http://www.springframework.org/schema/beans"
    	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    	xsi:schemaLocation="http://www.springframework.org/schema/beans
           http://www.springframework.org/schema/beans/spring-beans.xsd">

    	<bean
    		class="chap05.ThresholdRequiedBeanFactoryPostProcessor">
    		<property name="defaultThreshold" value="10" />
    	</bean>

    	<bean id="collector1" class="chap05.DataCollector">
    		<property name="threshold" value="5" />
    	</bean>

    	<bean id="collector2" class="chap05.DataCollector">
    	</bean>
    </beans>
    ```

- BeanFactoryPostProcessor 빈의 설정 정보 변경하는 방법을 사용하기때문에, ==**@Configuration 어노테이션을 이용해서 생성한 빈 객체에는 적용되지 않는다**==.

  - ==@Configuration을 이용해서 생성하는 빈 객체는 빈 설정 정보를 만들지 않는다==.

  - ```java
    package chap05;

    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;

    @Configuration
    public class Config {

    	@Bean
    	public static ThresholdRequiedBeanFactoryPostProcessor processor() {
    		ThresholdRequiedBeanFactoryPostProcessor p = new ThresholdRequiedBeanFactoryPostProcessor();
    		p.setDefaultThreshold(10);
    		return p;
    	}

    	@Bean
    	public DataCollector collector1() {
    		DataCollector collector = new DataCollector();
    		collector.setThreshold(5);
    		return collector;
    	}

    	@Bean
    	public DataCollector collector2() {
    		DataCollector collector = new DataCollector();
    		return collector;
    	}
    }
    ```


- BeanDefinition 인터페이스의 주요 메서드

  | 메소드                                      | 설명                          |
  | ---------------------------------------- | --------------------------- |
  | String getBeanClassNames() <br> setBeanClassName(String beanClassName) | 생성할 빈의 클래스 이름을 구하거나 지정      |
  | String getFactoryMethodName() <br> setFactoryMethodName(String factoryMethodName) | 팩토리 메소드 이름을 구하거나 지정         |
  | ConstructorArgumentValues getConstructorArgumentValues() | 생성 인자 값 설정 정보를 구한다          |
  | MutablePropertyValues getPropertyValues() | 프로퍼티 설정 정보를 구한다.            |
  | boolean isSingleton() <br> boolean isPrototype() | 싱글톤 또는 프로토타입 범위를 갖는지 여부를 확인 |
  | String getScope() <br> setScope(String scope) | 빈의 범위를 문자열로 구하거나 설정         |




### 1.2 BeanPostProcessor 를 이용한 빈 객체 변경

- ```java
  public interface BeanPostProcessor {
  	Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException;
  	Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException;
  }
  ```


- 빈 객체를 초기화하는 과정에서 위 두 메소드(`postProcessBeforeInitialization`, `postProcessAfterInitialization`) 을 호출
  - 리턴한 객체를 빈객체로 사용
- ![](spring5-1.png)

#### 예제

- 캐쉬 기능 확장

  - 직접 코드를 수정하는게 쉽지 않은 상황이라면

  - 스프링 설정은 복잡하지 않게 유지하고 싶다면

  - ```java
    package chap05;

    import java.util.Date;

    public interface StockReader {

    	public int getClosePrice(Date date, String code);
    }
    ```

  - ```java
    package chap05;

    import java.util.Date;

    public class StockReaderImpl implements StockReader {

    	@Override
    	public int getClosePrice(Date date, String code) {
    		System.out.println("StockReaderImpl: " + code);
    		// 가짜 구현
    		try { // 시간이 걸림을 나타내기 위한 300 밀리초 슬립
    			Thread.sleep(300);
    		} catch (InterruptedException e) {
    		}
    		return 500;
    	}
    }
    ```

- 캐쉬 기능 구현

  - ```java
    package chap05;

    import java.text.SimpleDateFormat;
    import java.util.Date;
    import java.util.HashMap;
    import java.util.Map;

    public class CacheStockReader implements StockReader {

    	private Map<String, Integer> cache = new HashMap<>();
    	private StockReader delegate;

    	public CacheStockReader(StockReader delegate) {
    		this.delegate = delegate;
    	}

    	@Override
    	public int getClosePrice(Date date, String code) {
    		String key = createKey(date, code);
    		System.out.println("CacheStockReader: " + key);
    		if (cache.containsKey(key))
    			return cache.get(key);

    		int value = delegate.getClosePrice(date, code);
    		cache.put(key, value);
    		return value;
    	}

    	private String createKey(Date date, String code) {
    		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    		return dateFormat.format(date) + "-" + code;
    	}

    }
    ```

  - ```java
    package chap05;

    import org.springframework.beans.BeansException;
    import org.springframework.beans.factory.config.BeanPostProcessor;
    import org.springframework.core.Ordered;

    public class CacheStockReaderBeanPostProcessor implements BeanPostProcessor, Ordered {

    	private int order;

    	@Override
    	public int getOrder() {
    		return order;
    	}

    	public void setOrder(int order) {
    		this.order = order;
    	}

    	@Override
    	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    		System.out.println("CacheStockReaderBeanPostProcessor:before-" + beanName);
    		return bean;
    	}

    	@Override
    	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    		System.out.println("CacheStockReaderBeanPostProcessor:after-" + beanName + ":" + bean.getClass().getName());
    		if (StockReader.class.isAssignableFrom(bean.getClass()))
    			return new CacheStockReader((StockReader) bean);
    		else
    			return bean;
    	}
    }
    ```

  - ```XML
    <?xml version="1.0" encoding="UTF-8"?>

    <beans xmlns="http://www.springframework.org/schema/beans"
    	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    	xsi:schemaLocation="http://www.springframework.org/schema/beans
           http://www.springframework.org/schema/beans/spring-beans.xsd">

    	<bean class="chap05.CacheStockReaderBeanPostProcessor">
    	</bean>

    	<bean id="stockReader" class="chap05.StockReaderImpl">
    	</bean>

    </beans>
    ```

- `postProcessAfterInitialization()` 메소드에서 전달받은 빈 객체의 타입이 `StockReader`인 경우, 빈 객체 대신에 새로운 `CacheStockReader`객체를 생성해 리턴

  - stockReader 빈 객체를 구하면 stockReaderImpl 객체가 아닌 `CacheStockReaderBeanProcessor`가 생성한 `CacheStockReader`객체를 구한다.

  - ```java
    if (StockReader.class.isAssignableFrom(bean.getClass()))

    return new CacheStockReader((StockReader) bean);
    ```

  - ```java
    package chap05;

    import org.springframework.context.support.GenericXmlApplicationContext;

    import java.util.Date;

    public class MainStockReader {

        public static void main(String[] args) {
            GenericXmlApplicationContext ctx = new GenericXmlApplicationContext("classpath:stockreader.xml");
            StockReader stockReader = ctx.getBean("stockReader", StockReader.class);
            Date date = new Date();
            printClosePrice(stockReader, date, "0000");
            printClosePrice(stockReader, date, "0000");
        
            ctx.close();
        }
        
        private static void printClosePrice(StockReader stockReader, Date date, String string) {
            long before = System.currentTimeMillis();
            int stockPrice = stockReader.getClosePrice(new Date(), "0000");
            long after = System.currentTimeMillis();
            System.out.println("읽어온 값 = " + stockPrice + ", 실행 시간 = " + (after - before));
        }

    }
    ```

  - 문제점 : class type 이 변경되었기때문에 StockReaderImpl.class 타입을 가진 stockReader빈은 존재하지 않는다.

  - ```java
    StockReaderImpl stockReader = ctx.getBean("stockReader", StockReaderImpl.class);
    ```

  ​

### 1.3 Ordered 인터페이스/@Order 어노테이션 적용 순서 지정

- 두개 이상의 BeanPostProcessor 가 존재할 경우 o.s.core.Ordered 인터페이스를 이용해서 순서를 지정

#### Ordered 인터페이스 구현.

- ```java
  package chap05;

  import org.springframework.beans.BeansException;
  import org.springframework.beans.factory.config.BeanPostProcessor;
  import org.springframework.core.Ordered;

  import java.lang.reflect.InvocationHandler;
  import java.lang.reflect.Method;
  import java.lang.reflect.Proxy;

  public class TraceBeanPostProcessor implements BeanPostProcessor, Ordered {

  	private int order;

  	@Override
  	public int getOrder() {
  		return order;
  	}

  	public void setOrder(int order) {
  		this.order = order;
  	}
  	
  	@Override
  	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
  		System.out.println("TraceBeanPostProcessor:before-" + beanName);
  		return bean;
  	}

  	@Override
  	public Object postProcessAfterInitialization(final Object bean, String beanName) throws BeansException {
  		System.out.println("TraceBeanPostProcessor:after-" + beanName+":"+bean.getClass().getName());
  		Class<?>[] interfaces = bean.getClass().getInterfaces();
  		if (interfaces.length == 0)
  			return bean;
  		InvocationHandler handler = new InvocationHandler() {
  			@Override
  			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
  				long before = System.currentTimeMillis();
  				Object result = method.invoke(bean, args);
  				long after = System.currentTimeMillis();
  				System.out.println(method.getName() + " 실행 시간 = " + (after - before));
  				return result;
  			}
  		};
  		return Proxy.newProxyInstance(getClass().getClassLoader(), interfaces, handler);
  	}

  }
  ```

- bean 객체가 구현한 인터페이스 구한다.

  - ```java
    Class<?>[] interfaces = bean.getClass().getInterfaces();
    ```

- 인터페이스에 정의된 메소드가 호출될 때, 이전/이후 시간을 기록

  - ```java
    long before = System.currentTimeMillis();

    Object result = method.invoke(bean, args);

    long after = System.currentTimeMillis();
    ```

- 프록시 객체 리턴

  - ```java
    return Proxy.newProxyInstance(getClass().getClassLoader(), interfaces, handler);
    ```

- 적용 순서 결정

  - BeanPostProcessor 구현 클래스가 Ordered 인터페이스를 구현한 경우, getOrder() 메소드를 이용

  - getOrder()로 구한 순서값이 ==**작은**== BeanPostProcessor 를 먼저 적용

  - Ordered 인터페이스를 구현하지 않은 BeanPostProcessor는 나중에 적용

  - ```xml
    <?xml version="1.0" encoding="UTF-8"?>

    <beans xmlns="http://www.springframework.org/schema/beans"
    	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    	xsi:schemaLocation="http://www.springframework.org/schema/beans
           http://www.springframework.org/schema/beans/spring-beans.xsd">

    	<bean class="chap05.CacheStockReaderBeanPostProcessor">
    		<property name="order" value="1" />
    	</bean>

    	<bean class="chap05.TraceBeanPostProcessor">
    		<property name="order" value="2" />
    	</bean>

    	<bean id="stockReader" class="chap05.StockReaderImpl">
    	</bean>

    </beans>
    ```

  - ```mermaid
    graph TD;
    	A[stockReader 빈 <br> StockReaderImpl]-->B;
    	B[CacheStockReader <br> BeanPostProcessor]-->C;
    	C[stockReader 빈 <br> CacheStockReader]-->D;
    	D[TraceBeanPostProcessor]-->E[stockReader 빈 <br> 런타임 프록시];
    ```

## 02. PropertyEditor와 ConversionService

- 스프링은 내부적으로 ==**PropertyEditor를 이용해서 문자열을 알맞은 타입**==으로 변환.
- 스프링 3.x 부터는 ==**ConversionService를 이용해서 타입 변환을 처리**==




### 2.1 PropertyEditor를 이용한 타입 변환

- PropertyEditor
  - 자바빈 규약
    - 문자열을 특정 타입의 프로퍼티 변환시 java.beans.PropertyEditor 인터페이스 사용
  - sun.beans.editors 패키지
    ![spring5-d.png](.\spring5-d.png)
  - ==**자바에 포함된 것은 기본적인 타입만 지원**==
  - 스프링은 PropertyEdtitor를 추가로 제공

#### (1) 스프링이 제공하는 주요 PropertyEditor

- o.s.beans.propertyeditors 패키지

  - ![spring5-spring-d.png](.\spring5-spring-d.png)

  - 스프링이 제공하는 주요 PropertyEditor

    | PropertyEditor          | 설명                                       | 기본 사용 |
    | ----------------------- | ---------------------------------------- | ----- |
    | ByteArrayPropertyEditor | String.getBytes()를 이용해서 문자열을 byte 배열로 변환 | 기본    |
    | CharArrayPropertyEditor | String.toCharArray()를 이용해서 문자열을 char 배열로 변환 |       |
    | CharsetEditor           | 문자열을 Charset으로 변환                        | 기본    |
    | ClassEditor             | 문자열을 Class 타입으로 변환                       | 기본    |
    | CurrencyEditor          | 문자열을 java.util.Currency로 변환              |       |
    | CustomBooleanEditor     | 문자열을 Boolean 타입으로 변환                     | 기본    |
    | CustomDateEditor        | DateFormat 을 이용해서 문자열을 java.util.Date로 변환 |       |
    | CustomNumberEditor      | Long, Double, BigDecimal등 숫자 타입을 위한 프로퍼티 에디터 | 기본    |
    | FileEditor              | 문자열을 java.io.File로 변환                    | 기본    |
    | LocaleEditor            | 문자열을 Locale로 변환                          | 기본    |
    | PatternEditor           | 정규 표현식 문자열을 Pattern으로 변환                 | 기본    |
    | PropertiEditor          | 문자열을 Properties로 변환                      | 기본    |
    | URLEditor               | 문자열을 URL로 변환                             | 기본    |

- CustomDateEditor 나 PatternEditor는 기본으로 사용되지 않는다.

  - ==**기본이 아닌 PropertyEditor를 사용하려면 추가로 등록해주어야한다.**==

#### (2) 커스텀 PropetyEditor 구현

- 문자열을 Money 클래스로 변환해주는 PropertyEditor

  - **==단순 변환 기능만 필요==**하다면, ==PropertyEditorSupport 클래스 상속받아 setAsText() 메소드를 재정의== 

  - ```java
    package chap05;

    import java.beans.PropertyEditorSupport;

    import java.util.regex.Matcher;

    import java.util.regex.Pattern;

    public class MoneyEditor extends PropertyEditorSupport {

        @Override
        public void setAsText(String text) throws IllegalArgumentException {
            Pattern pattern = Pattern.compile("([0-9]+)([A-Z]{3})");
            Matcher matcher = pattern.matcher(text);
            if (!matcher.matches())
                throw new IllegalArgumentException("invalid format");
        
            int amount = Integer.parseInt(matcher.group(1));
            String currency = matcher.group(2);
            setValue(new Money(amount, currency));
        }

    }
    ```

  - ```xml
    <bean class="chap05.InvestmentSimulator">
    	<property name="minimumAmount" value="10000WON" />
    </bean>
    ```

#### (3) PropertyEditor 추가 방법 : 같은 패키지에 PropertyEditor 위치 시키기.

- 자바빈 규약에 명시된 규칙에 따라
  - 변환 대상 타입과 동일한 패키지에 ==**'타입Editor'**==이름으로 PropertyEditor를 구현
  - chap05에 'MoneyEditor' 라는 이름으로 구현.

#### (4) PropertyEditor 추가 방법 : CustomEditorConfigurer 사용하기

- CustomEditorConfigurer

  - BeanFactoryPostProcessor 

  - 스프링 빈을 초기화하기전에 필요한 PropertyEditor를 등록할 수 잇게 한다.

  - ```xml
    <?xml version="1.0" encoding="UTF-8"?>

    <beans xmlns="http://www.springframework.org/schema/beans"
    	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    	xsi:schemaLocation="http://www.springframework.org/schema/beans
           http://www.springframework.org/schema/beans/spring-beans.xsd">

    	<bean class="org.springframework.beans.factory.config.CustomEditorConfigurer">
    		<property name="customEditors">
    			<map>
    				<entry key="chap05.Money"
    					value="chap05.MoneyEditor2" />
    			</map>
    		</property>
    	</bean>

    	<bean class="chap05.InvestmentSimulator">
    		<property name="minimumAmount" value="10000WON" />
    	</bean>
    </beans>
    ```

- **customEditors 프로퍼티이용**

  - 타입, 타입 PropertyEditor 쌍 맵으로 전달

  - ```xml
    <bean class="org.springframework.beans.factory.config.CustomEditorConfigurer">
    	<property name="customEditors">
    		<map>
    			<entry key="chap05.Money"
    				value="chap05.MoneyEditor2" />
    		</map>
    	</property>
    </b      ean>
    ```

#### (5) PropertyEditor 추가 방법 : PropertyEditorRegistrar 사용하기

- CustomEditorConfigurer설정의 경우 매개변수 설정 불가(Date Pattern 과 같은) -> **PropertyEditorRegistrar 이용**

PropertyEditorRegistrar

- PropertyEditor 에 매개 변수를 지정하고 싶을때

- ```java
  public interface PropertyEditorRegistrar {
  	void registerCustomEditors(PropertyEditorRegistry registry);
  }
  ```

1. `PropertyEditorRegistrar` 인터페이스 상속받은 클래스에서 PropertyEditor를 직접 생성하고 등록
2. 1번 과정에서 생성한 클래스를 빈으로 등록, `CustomEditorConfigurer`에 **propertyEditorRegistrar 로 등록**

#### A. PropertyEditorRegistrar 인터페이스 상속 구현

- ```java
  package chap05;

  import java.text.SimpleDateFormat;
  import java.util.Date;

  import org.springframework.beans.PropertyEditorRegistrar;
  import org.springframework.beans.PropertyEditorRegistry;
  import org.springframework.beans.propertyeditors.CustomDateEditor;

  public class CustomPropertyEditorRegistrar implements PropertyEditorRegistrar {

  	private String datePattern;

  	@Override
  	public void registerCustomEditors(PropertyEditorRegistry registry) {
  		CustomDateEditor propertyEditor = new CustomDateEditor(new SimpleDateFormat(datePattern), true);
  		registry.registerCustomEditor(Date.class, propertyEditor);
  	}

  	public void setDatePattern(String datePattern) {
  		this.datePattern = datePattern;
  	}

  }
  ```


- 상속 받은 클래스는 **==registerCustomEditors()  메소드에서 원하는 PropertyEditor를 생성/등록==**

- 첫번째 파라미터 : 변환 대상이 되는 타입, 두번째 파라미터 : 문자열을 지정된 타입으로 변할때 사용할 PropertyEditor 객체

- ```java
  registry.registerCustomEditor(Date.class, propertyEditor);
  ```

#### B. CustomEditorConfigurer 가 PropertyEditorRegistrar 구현 클래스를 사용하도록 설정.

- **CustomEditorConfigurer**의 **propertyEditorRegisrars 프로퍼티**에 등록.(customPropertyEditorRegistrar)

- customPropertyEditorRegistrar 은 bean으로 등록.

  - ```xml
    <?xml version="1.0" encoding="UTF-8"?>

    <beans xmlns="http://www.springframework.org/schema/beans"
    	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    	xsi:schemaLocation="http://www.springframework.org/schema/beans
           http://www.springframework.org/schema/beans/spring-beans.xsd">

    	<bean class="org.springframework.beans.factory.config.CustomEditorConfigurer">
    		<property name="customEditors">
    			<map>
    				<entry key="chap05.Money"
    					value="chap05.MoneyEditor2" />
    			</map>
    		</property>
    		<property name="propertyEditorRegistrars">
    			<list>
    				<ref bean="customPropertyEditorRegistrar"/>
    			</list>
    		</property>
    	</bean>

    	<bean id="customPropertyEditorRegistrar" class="chap05.CustomPropertyEditorRegistrar">
    		<property name="datePattern" value="yyyy-MM-dd HH:mm:ss" />
    	</bean>
    	
    	<bean class="chap05.RestClient">
    		<property name="serverUrl"
    			value="https://www.googleapis.com/language/translate/v2" />
    		<property name="apiDate" value="2010-03-01 09:30:00" />
    	</bean>
    </beans>
    ```




### 2.2 ConversionService를 이용한 타입 변환

- 스프링 3 버전부터  추가

- PropertyEditor 

  - 자바빈의 규약에 따라 문자열과 타입간의 변환 처리

- ConversionService

  - 타입과 타입간의 변환을 처리하는 기능 정의

- 스프링은 ConversionService를 스프링 빈으로 등록하면

  - PropertyEditor 대신 ConversionService를 이용해서 타입 변환 처리

- ==스프링은 이미 ConversionService 인터페이스를 구현한 클래스를 제공함(DefaultConversionService)==

  - ```java
    public interface ConversionService {

    	boolean canConvert(Class<?> sourceType, Class<?> targetType);
    	boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType);
    	<T> T convert(Object source, Class<T> targetType);
    	Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType);

    }
    ```


#### (1) ConversionFactoryBean 을 이용한 ConversionService 등록.

- DefaultConversionService

  - 스프링 제공하는 ConversionService 구현

  - 사용하기 위해, ==ConversionServiceFactoryBean 클래스를 빈으로 등록==

  - 빈 객체 이름을 "==**conversionService**=="로 등록해야함

  - ```xml
    <bean id="conversionService"
    		class="org.springframework.context.support.ConversionServiceFactoryBean">
    </bean>
    ```

- DefaultConversionService 클래스의 타입 변환

  - 직접 하지 않고 ==내부에 등록된 GenericConverter==에 위임
  - 다수의 컨버터를 가지고 있음
  - convert() 메소드 실행
    1. 등록된 GenericConverter들 중에서 소스 객체의 타입을 대상 타입으로 변환해주는 Generic Converter를 찾는다
    2. GenericConverter가 존재할 경우, 해당 GenericConverter를 이용해서 타입 변환을 수행
    3. 존재하지 않을 경우 익셉션 발생

#### (2) GenericConverter를 이용한 커스텀 변환 구현

* GenericConveter 인터페이스

  * ```java
    package org.springframework.core.convert.converter;

    import java.util.Set;
    import org.springframework.core.convert.TypeDescriptor;
    import org.springframework.util.Assert;

    public interface GenericConverter {
        Set<GenericConverter.ConvertiblePair> getConvertibleTypes();
        Object convert(Object var1, TypeDescriptor var2, TypeDescriptor var3);

        public static final class ConvertiblePair {
            private final Class<?> sourceType;
            private final Class<?> targetType;

            public ConvertiblePair(Class<?> sourceType, Class<?> targetType) {
                Assert.notNull(sourceType, "Source type must not be null");
                Assert.notNull(targetType, "Target type must not be null");
                this.sourceType = sourceType;
                this.targetType = targetType;
            }

            //...get메서드
        }
    }

    ```

  * getConvertibleTypes() 

    - 변환 가능한 타입 쌍(ConvertiblePair)의 집합 리턴

  * convert()

    - TypeDescriptor 정보를 이용 source 객체를 대상 타입으로 변환 리턴.

- String -> Money 로 변환해주는 클래스

  - ```java
    public class MoneyGenericConverter implements GenericConverter {

    	private Set<ConvertiblePair> pairs;

    	public MoneyGenericConverter() {
    		Set<ConvertiblePair> pairs = new HashSet<>();
    		pairs.add(new ConvertiblePair(String.class, Money.class));
    		this.pairs = Collections.unmodifiableSet(pairs);
    	}

    	@Override
    	public Set<ConvertiblePair> getConvertibleTypes() {
    		return pairs;
    	}

    	@Override
    	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
    		if (targetType.getType() != Money.class) {
    			throw new IllegalArgumentException("invalid targetType");
    		}
    		if (sourceType.getType() != String.class) {
    			throw new IllegalArgumentException("invalid sourceType");
    		}
    		String moneyString = (String) source;
    		Pattern pattern = Pattern.compile("([0-9]+)([A-Z]{3})");
    		Matcher matcher = pattern.matcher(moneyString);
    		if (!matcher.matches())
    			throw new IllegalArgumentException("invalid format");

    		int amount = Integer.parseInt(matcher.group(1));
    		String currency = matcher.group(2);
    		return new Money(amount, currency);
    	}

    }
    ```

- ConversionService가 해당 GenericConverter를 사용하도록 등록

  - ==ConversionServiceFactoryBean의 converters 프로퍼티에 등록==

  - ```xml
    <bean id="conversionService"
    	class="org.springframework.context.support.ConversionServiceFactoryBean">
    	<property name="converters">
    		<set>
     			<bean class="net.madvirus.spring4.chap05.MoneyGenericConverter" />
    		</set>
    	</property>
    </bean>
    ```

#### (3) Converter를 이용한 커스텀 변환 구현

- 타입 변환이 단순한 경우에는 GenericConverter 대신 ==Converter 인터페이스를 사용==.

- Converter 인터페이스

  - ```java
    package org.springframework.core.convert.converter;

    public interface Converter<S, T> {
        T convert(S var1);
    }
    ```

- 타입 변환 쌍이 한 개만 존재한다면, Converter 인터페이스를 사용하여 코드를 단순하게 만듦.

  - ```java
    public class StringToMoneyConverter implements Converter<String, Money> {

    	@Override
    	public Money convert(String source) {
    		Pattern pattern = Pattern.compile("([0-9]+)([A-Z]{3})");
    		Matcher matcher = pattern.matcher(source);
    		if (!matcher.matches())
    			throw new IllegalArgumentException("invalid format");

    		int amount = Integer.parseInt(matcher.group(1));
    		String currency = matcher.group(2);
    		return new Money(amount, currency);
    	}

    }
    ```

- ConversionServiceFactoryBean의 converters 프로퍼티에 구현한 Converter 등록

  - ```xml
    <bean id="conversionService"
        class="org.springframework.context.support.ConversionServiceFactoryBean">
        <property name="converters">
            <set>
    <!-- 				<bean class="net.madvirus.spring4.chap05.MoneyGenericConverter" /> -->
                <bean class="net.madvirus.spring4.chap05.StringToMoneyConverter" />
                <bean class="net.madvirus.spring4.chap05.StringToDateConverter">
                    <property name="pattern" value="yyyy-MM-dd HH:mm:ss" />
                </bean>
            </set>
        </property>
    </bean>
    ```

#### (4) FormattingConversionServiceFactoryBean을 이용한 ConversionService 등록

- 스프링 제공

  - DefaultConversionService
  - ==**DefaultFormattingConversionService**==

- DefaultFormattingConversionService : ConversionService 구현체

  - Converter/GenericConverter
  - Formatter
  - @DateTimeFormat/@NumberFormat Formatter

- DefaultFormattingConversionService를 ConversionService로 사용하려면

  - ==FormattingConversionServiceFactoryBean 클래스를 빈==으로 등록

  - ```xml
    <bean id="conversionService"
    	class="org.springframework.format.support.FormattingConversionFactoryBean">
    </bean>
    ```

  - ```java
    public class RestClient {
    	private Date apiDate;
        
        @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
        public void setApiDate(Date apiDate) {
        	this.apiData = apiDate;
        }
    }
    ```

  - ```xml
    <bean id="conversionService"
    	class="org.springframework.format.support.FormattingConversionServiceFactoryBean">
    </bean>

    <bean id="restClient" class="chap05.RestClient">
    	<property name="serverUrl"
        	value="https://www.googleapis.com/language/translate/v2" />
        <property name="apiDate" value="2010-03-01 09:30:00" />
    </bean>
    ```

#### (5) Formatter를 이용한 커스텀 변환 구현

- DefaultFormattingConversionService 타입 변환 지원.

  - Formatter
  - Parser
  - Printer 

- ```java
  public interface Printer<T> {
  	String print(T object, Locale locale);
  }

  public interface Parser<T> {
  	T parse(String text, Locale locale) throws ParserException;
  }

  public interface Formatter<T> extends Printer<T>,Parser<T> {
  }
  ```

- Printer

  - 객체를 문자열로 변환
  - 로케일에 따라 변환 결과 만듦

- Parser

  - 문자열을 지정한 타입으로 변환
  - 로케일에 따라 변환 결과 만듦

- Formatter

  - Printer와 Parser 인터페이스 상속받은 인터페이스
  - 두 기능을 함께 제공

- ```java
  public class MoneyFormatter implements Formatter<Money> {
  	@Override
      public String print(Money object, Locale locale) {
      	return object.getAmount() + object.getCurrency();
      }
      
      @Override
      public Money parser(String text, Locale locale) throws ParserException {
      	Pattern pattern = Pattern.compile("([0-9]+)([A-Z]{3})");
          Matcher matcher = pattern.matcher(text);
          if(!matcher.matchers())
          	throw new IllegalArgumentException("invalid format");
              
          int amount = Integer.parserInt(matcher.group(1));
          String currency = matcher.group(2);
          return new Money(amount,currency);
      }
  }
  ```

- Formatter 추가

  - ```xml
    <bean id="conversionService"
    	class="org.springframework.format.support.FormattingConversionServiceFactoryBean">
    	<property name="formatters">
        	<set>
            	<bean class="chap05.MoneyFormatter" />
            </set>
        </property>
    </bean>

    <bean class="chap05.InvestmentSimulator">
    	<property name="minimumAmount" value="10000WON" />
    </bean>
    ```

    ​