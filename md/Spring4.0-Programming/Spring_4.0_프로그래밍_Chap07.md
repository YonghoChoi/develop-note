[TOC]

# 07. 스프링 MVC: 기본기

## 01. 스프링 MVC 일단 해보기

### 1.1 Gralde 설정

* build.gradle

```java
group 'springsample'
version '1.0-SNAPSHOT'

apply plugin: 'java'
apply plugin: 'war'
apply plugin: 'idea'

sourceCompatibility = 1.8

// 소스 인코딩 지정방법 1
[compileJava, compileTestJava]*.options*.encoding = 'UTF-8'
// 소스 인코딩 지정밥법 2
//tasks.withType(Compile) {
//    options.encoding = 'UTF-8'
//}

repositories {
    mavenCentral()
}

List loggers = [
        "org.slf4j:slf4j-api:${slf4jVersion}",
        "org.slf4j:jcl-over-slf4j:${slf4jVersion}",
        "org.slf4j:log4j-over-slf4j:${slf4jVersion}",
        "org.slf4j:jul-to-slf4j:${slf4jVersion}",
        "ch.qos.logback:logback-core:${logbackVersion}",
        "ch.qos.logback:logback-classic:${logbackVersion}"
]


dependencies {
    compile loggers

    compile "jstl:jstl:${jstlVersion}"
    compile "org.projectlombok:lombok:${lombokVersion}"
    compile "org.springframework:spring-webmvc:${springFrameworkVersion}"
    compile "org.hibernate:hibernate-validator:${hibernateValidatorVersion}"
    compile "javax.validation:validation-api:${javaxValidationVersion}"
    providedCompile "javax.servlet:javax.servlet-api:${servletApiVersion}"
    providedCompile "javax.servlet.jsp:jsp-api:${jspApiVersion}"

    testCompile "org.springframework:spring-test:${springFrameworkVersion}"
    testCompile group: 'junit', name: 'junit', version: '4.11'
}


configurations {
    all.collect { configuration ->
        configuration.exclude group: 'commons-logging', module: 'commons-logging'
        configuration.exclude group: 'log4j', module: 'log4j'
        configuration.exclude group: 'org.slf4j', module: 'slf4j-log4j12'
        configuration.exclude group: 'org.slf4j', module: 'slf4j-jcl'
        configuration.exclude group: 'org.slf4j', module: 'slf4j-jdk14'
    }
}
```

* gradle.properties

```java
junitVersion = 4.11

slf4jVersion = 1.7.10
logbackVersion = 1.0.13

springFrameworkVersion = 4.3.2.RELEASE
servletApiVersion = 3.1.0
jspApiVersion = 2.2
jstlVersion = 1.2

lombokVersion = 1.16.6

hibernateValidatorVersion = 5.3.0.CR1
javaxValidationVersion = 1.1.0.Final
```



### 1.2 스프링 MVC를 위한 설정을 web.xml 에 추가하기

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns="http://java.sun.com/xml/ns/javaee"
	xsi:schemaLocation="http://java.sun.com/xml/ns/javaee 
		http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd"
	id="spring4-chap07" version="3.0">
	<display-name>spring4-chap07</display-name>

	<servlet>
		<servlet-name>dispatcher</servlet-name>
		<servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
		<init-param>
			<param-name>contextConfigLocation</param-name>
			<param-value>
				/WEB-INF/mvc-quick-start.xml
			</param-value>
		</init-param>
		<load-on-startup>1</load-on-startup>
	</servlet>

	<servlet-mapping>
		<servlet-name>dispatcher</servlet-name>
		<url-pattern>*.do</url-pattern>
	</servlet-mapping>

	<filter>
		<filter-name>encodingFilter</filter-name>
		<filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
		<init-param>
			<param-name>encoding</param-name>
			<param-value>UTF-8</param-value>
		</init-param>
	</filter>
	<filter-mapping>
		<filter-name>encodingFilter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>
</web-app>
```

*   소스 설명
    *   라인 9 - 21 :
        * DispatcherServlet 을 등록. 
        * 내부적으로 스프링 컨테이너 생성
        * ==contextConfigLocation 초기화 파라미터==를 이용하여 컨테이너 생성할때 사용할 설정 파일 지정
    *   라인 23 - 26 :
        * dispatcher 서블릿에 대한 매핑을 *.do 로 지정
        * do 로 끝나는 모든 요청을 dispatcher 서블릿이 처리
    *   라인 28 - 41 :
        * 요청 파라미터를 UTF-8로 처리하기 위한 필터 설정

          ​

### 1.3 스프링 컨트롤러 구현

```java
package chap07.quickstart;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HelloController {

	@RequestMapping("/hello.do")
	public String hello(Model model) {
		model.addAttribute("greeting", "안녕하세요");
		return "hello";
	}
	
	@RequestMapping("/hello-raw.do")
	public void hello(HttpServletResponse response) throws IOException {
		response.setContentType("text/plain");
		response.setCharacterEncoding("utf-8");
		PrintWriter writer = response.getWriter();
		writer.write("안녕하세요");
		writer.flush();
	}
}
```
* 소스 설명
    * 라인 7 :
        * 클래스가 스프링 MVC 컨트롤러임을 지정
    * 라인 10 :
        * hello()  메소드가 /hello.do로 들어오는 요청을 처리함을 지정
    * 라인 12 :
        * 뷰에 'greeting' 이라는 이름으로 '안녕하세요' 라는 데이터를 전달.
    * 라인 13 :
        * 'hello'를 뷰 이름 리턴

* 스프링 MVC 컨트롤러는 클라이언트의 요청을 처리하는 기능을 제공
    * /hello.do 로 요청 하면 hello() 메소드를 이용해 처리한다고 지정함
* 컨트롤러에서 직접 응답 결과를 생성할 수도 잇지만, 보통 결과를 보여줄 뷰 이름을 리턴
* 뷰에 전달할 데이터를 model 에 담당서 전달
    * 뷰에서는 모델에 담긴 데이터를 이용해서 알맞은 응답 결과 생성




### 1.4 JSP를 이용한 뷰 구현

* 스프링은 기본적으로 JSP 를 포함하여 벨로시티나 프리마커와 같은 템플릿엔진 지원

![spring7-2.jpg](.\spring7-2.jpg "" "width:600px")


```html
<%@ page contentType="text/html; charset=utf-8" %>
<!DOCTYPE html>
<html>
<head>
	<title>Hello</title>
</head>
<body>
	인사말: ${greeting}
</body>
</html>
```

* 소스 설명
    * 라인 8:
        * ${greeting} 
        * HelloController 클래스의 model 설정을 통해서 전달

* HelloController에서 'hello'를 리턴했는데, 어떻게 WEB-INF/view/hello.jsp 뷰를 사용했는가~?
    * ==스프링 MVC 설정== 을 통해서 처리 됨.



### 1.5 스프링 MVC 설정 파일 작성

* WEB-INF/mvc-quick-start.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>

<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:mvc="http://www.springframework.org/schema/mvc" 
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/mvc
       http://www.springframework.org/schema/mvc/spring-mvc.xsd">

	<mvc:annotation-driven />

	<bean id="viewResolver"
		class="org.springframework.web.servlet.view.InternalResourceViewResolver">
		<property name="prefix" value="/WEB-INF/view/" />
		<property name="suffix" value=".jsp" />
	</bean>

	<bean class="chap07.quickstart.HelloController" />
</beans>
```

* 소스 설명
    * 라인 11:
        * &lt;mvc:annotation-driven/&gt; 
        * 몇 개의 설정을 한번에 해주는 코드
    * 라인 13 - 17 : 
        * 뷰 이름을 실제 어떤 뷰와 연결할지를 결정하는 ==**ViewResolver**==를 설정.
        * prefix + 뷰 이름 + suffix로 결정
    * 라인 19 : 
        * 컨트롤러 객체를 빈으로 등록




### 1.6 실행

* gradle jettyRun




## 02. 주요 흐름과 주요 컴포넌트


![spring7-4.jpg](.\spring7-4.jpg "" "width:600px")

* HandlerMapping/HandlerAdapter/ViewResolver/컨트롤러 처럼 <<spring bean>> 이 표시된것은 ==**스프링 빈으로 등록**==
* ==**회색 부분**==은 직접 구현해주는 부분

| 구성 요소             | 설명                                       |
| ----------------- | ---------------------------------------- |
| DispatcherServlet | 클라이언트의 요청을 전달 받음. 컨트롤러에게 클라이언트의 요청 전달, 컨트롤러의 결과값을 View에 전달하여 알맞은 응답을 생성. |
| HandlerMapping    | 클라이언트의 요청 URL을 어떤 컨트롤러가 처리할지 결정          |
| HandlerAdapter    | DispatcherServlet의 처리 요청을 변환해서 컨트롤러에 전달, 컨트롤러의 응답 결과를 DispatcherServlet이 요구하는 형식으로 변환. 웹 브라우저 캐시등의 설정도 담당 |
| 컨트롤러(Controller)  | 클라이언트의 요청을 처리한뒤 결과 리턴. 응답 결과에서 보여줄 데이터를 Model에 담당 전달 |
| ModelAndView      | 컨트롤러가 처리한 결과 정보 및 뷰 선택에 필요한 정보를 담음       |
| ViewResolver      | 컨트롤러의 처리 결과를 보여줄 뷰를 결정                   |
| View              | 컨트롤러의 처리 결과 화면을 생성. JSP나 Velocity 템플릿 파일등을 이용 |



## 03. 스프링 MVC 설정 기초

* 스프링 MVC를 이용해서 웹 어플리케이션 개발할때 먼저 할일
    * 스프링 MVC 설정을 작성

* 스프링 MVC를 위한 기본 설정 과정
    * web.xml에 DispatcherServlet 설정
    * web.xml에 캐릭터 인코딩 처리를 위한 필터 설정
    * 스프링 MVC 설정
        * HandlerMapping, HandlerAdapter 설정
        * ViewResolver 설정




### 3.1 DispatcherServlet 서블릿 설정

* DispatcherServlet
    * 스프링 MVC 프레임워크의 중심이 되는 서블릿 클래스
    * 내부적으로 스프링 컨테이너를 생성
    * 별도의 초기화 파라미터없이 설정하면 ==**/WEB-INF/[서블릿이름]-servlet.xml 파일**==을 스프링 설정 파일로 사용.

* xml config
```xml
<servlet>
    <servlet-name>dispatcher</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    <init-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>
            /WEB-INF/main.xml
            /WEB-INF/bbs.xml
            classpath:/common.xml
        </param-value>
    </init-param>
    <load-on-startup>1</load-on-startup>
</servlet>
```

* 소스 설명
    * 한 개 이상의 설정 파일을 사용하거나 직접 지정할 경우 ==**contextConfigLocation 초기화 파라미터**==로 설정 파일 목록 지정
        * 여러개의 설정 파일은 콤마(,), 공백( ), 탭(\t), 줄 바꿈(\n), 세미콜록(;)을 이용하여 구분
        * 설정 파일의 경로는 웹 어플리케이션 루트 디렉토리를 기준
        * file:, classpath: 접두어를 이용해서 로컬파일이나 클래스패스에 위치한 파일을 사용 가능.


* java config
```xml
<servlet>
    <servlet-name>dispatcherConfig</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    <init-param>
        <param-name>contextClass</param-name>
        <param-value>
org.springframework.web.context.support.AnnotationConfigWebApplicationContext
        </param-value>
    </init-param>
    <init-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>
            chap07.quickstart.MvcQuickStartConfig
        </param-value>
    </init-param>
    <load-on-startup>1</load-on-startup>
</servlet>
```

* 소스 설명
    * contextClass 초기화 파라미터는 DispatcherServlet이 스프링 컨테이너를 생성할 때 사용할 구현 클래스 지정
        * 지정하지 않으면 XmlWebApplicationContext를 사용하고, XML 설정 파일을 사용
    * 초기화 파라미터의 값은 @Configuration 자바 클래스의 완전한 이름을 지정
        * 두 개 이상인 경우 콤마(,), 공백( ), 탭(\t), 줄 바꿈(\n), 세미콜론(;)을 이용하여 구분




#### (1) 캐릭터 인코딩 필터 설정

* 스프링은 요청 파라미터의 캐릭터 인코딩을 지정할 수 있는 서블릿 필터(==**CharacterEncodingFilter**==)를 제공

```xml
<filter>
    <filter-name>encodingFilter</filter-name>
    <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
    <init-param>
        <param-name>encoding</param-name>
        <param-value>UTF-8</param-value>
    </init-param>
</filter>
<filter-mapping>
    <filter-name>encodingFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```



### 3.2 스프링 MVC 설정 기초

* 스프링 MVC 설정을 위해 필요한 최소한의 구성 요소(빈 객체)
    * HandlerMapping 구현 객체
    * HandlerAdapter 구현 객체
    * ViewResolver 구현 객체

* <mvc:annotation-driven> 태그
    * RequestMappingHandlerMapping(HandlerMapping)
    * RequestMappingHandlerAdapter(HandlerAdapter)
        * 위 두 클래스는 @Controller 어노테이션이 적용된 클래스를 컨트롤러로 사용할 수 있도록 함
    * JSON 이나 XML 등 요청/응답 처리를 위해 필요한 변환 모듈이나 데이터 바인딩 처리를 위한 **==ConversionService==**등을 빈으로 등록.(아래 no mvc:annotation-driven 코드 보기)


```xml
<?xml version="1.0" encoding="UTF-8"?>

<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:mvc="http://www.springframework.org/schema/mvc" 
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/mvc
       http://www.springframework.org/schema/mvc/spring-mvc.xsd">

	<mvc:annotation-driven />

	<bean id="viewResolver"
		class="org.springframework.web.servlet.view.InternalResourceViewResolver">
		<property name="prefix" value="/WEB-INF/view/" />
		<property name="suffix" value=".jsp" />
	</bean>
</beans>

```

* 소스 설명
    * InternalResourceViewResolver
        * JSP를 이용해서 뷰를 생성할 때 사용되는 ViewResolver 구현체
        * 이름이 ==**viewResolver**== 여야만 한다.


```java
package chap07.quickstart;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@EnableWebMvc
public class MvcQuickStartConfig {

	@Bean
	public InternalResourceViewResolver viewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/view/");
		viewResolver.setSuffix(".jsp");
		return viewResolver;
	}

	@Bean
	public HelloController2 helloController() {
		return new HelloController2();
	}

}
```

* @Configuration 자바 설정
    * ==**@EnableWebMvc 어노테이션**==을 사용
      * <mvc:annotation-driven>과 동일한 효과.

* no mvc:annotation-driven

```xml
<?xml version="1.0" encoding="UTF-8"?>

<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:util="http://www.springframework.org/schema/util" 
	xmlns:mvc="http://www.springframework.org/schema/mvc" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/util
       http://www.springframework.org/schema/util/spring-util.xsd">
	
	<!-- mvc:annotation-driven 태그가 생성하는 빈 설정 시작 -->
	<bean id="mvcContentNegotiationManager" class="org.springframework.web.accept.ContentNegotiationManagerFactoryBean">
		<property name="mediaTypes">
			<props>
				<!-- ROME가 존재하면 -->
					<!-- <prop key="atom">application/atom+xml</prop> -->
					<!-- <prop key="rss">application/rss+xml</prop> -->
				<!-- Jackson2가 존재하면 -->
					<!-- <prop key="json">application/json</prop> -->
				<!-- JAXB2가 존재하면, -->
					<!-- <prop key="xml">application/xml</prop> -->
			</props>
		</property>
	</bean>
	
	<bean id="formattingConversionService" 
		class="org.springframework.format.support.FormattingConversionServiceFactoryBean">
	</bean>
	
	<bean id="optionalValidatorFactoryBean" 
		class="org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean">
	</bean>
	
	<bean class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping">
		<property name="order" value="0" />
		<property name="contentNegotiationManager" ref="mvcContentNegotiationManager" />
	</bean>
	
	<bean id="configurableWebBindingInitializer" 
		class="org.springframework.web.bind.support.ConfigurableWebBindingInitializer">
		<property name="conversionService" ref="formattingConversionService" />
		<property name="validator" ref="optionalValidatorFactoryBean" />
		<property name="messageCodesResolver"><null/></property>
	</bean>
	
	<util:list id="messageConverters">
		<bean class="org.springframework.http.converter.ByteArrayHttpMessageConverter" />
		<bean class="org.springframework.http.converter.StringHttpMessageConverter">
			<property name="writeAcceptCharset" value="false" />
		</bean>
		<bean class="org.springframework.http.converter.ResourceHttpMessageConverter" />
		<bean class="org.springframework.http.converter.xml.SourceHttpMessageConverter" />
		<bean class="org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter" />
		<!-- ROME가 존재하면 -->
<!-- 		<bean class="org.springframework.http.converter.feed.AtomFeedHttpMessageConverter" /> -->
<!-- 		<bean class="org.springframework.http.converter.feed.RssChannelHttpMessageConverter" /> -->
		<!-- JAXB2가 존재하면 -->
<!-- 		<bean class="org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter" /> -->
		<!-- Jackson2가 존재하면 -->
<!-- 		<bean class="org.springframework.http.converter.json.MappingJackson2HttpMessageConverter" /> -->
	</util:list>
	
	<bean id="requestMappingHandlerAdapter" 
			class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter">
		<property name="contentNegotiationManager" ref="mvcContentNegotiationManager" />
		<property name="webBindingInitializer" ref="configurableWebBindingInitializer" />
		<property name="messageConverters" ref="messageConverters" />
	</bean>

	<bean id="mvcUriComponentsContributor" 
			class="org.springframework.web.servlet.config.AnnotationDrivenBeanDefinitionParser.CompositeUriComponentsContributorFactoryBean">
		<property name="handlerAdapter" ref="requestMappingHandlerAdapter" />
		<property name="conversionService" ref="formattingConversionService" />
	</bean>

	<bean class="org.springframework.web.servlet.handler.MappedInterceptor">
		<constructor-arg><null /></constructor-arg>
		<constructor-arg>
			<bean class="org.springframework.web.servlet.handler.ConversionServiceExposingInterceptor">
				<constructor-arg ref="formattingConversionService"/>
			</bean>
		</constructor-arg>
	</bean>

	<bean class="org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver">
		<property name="contentNegotiationManager" ref="mvcContentNegotiationManager" />
		<property name="messageConverters" ref="messageConverters" />
		<property name="order" value="0" />
	</bean>
	
	<bean class="org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver">
		<property name="order" value="1" />
	</bean>
		
	<bean class="org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver">
		<property name="order" value="2" />
	</bean>
	<!-- mvc:annotation-driven 태그가 생성하는 빈 설정 끝 -->	

	
	<!-- mvc:default-servlet-handler 태그가 생성하는 빈 설정 시작 -->
	<bean id="defaultServletHttpRequestHandler" 
		class="org.springframework.web.servlet.resource.DefaultServletHttpRequestHandler">
	</bean>
	
	<bean class="org.springframework.web.servlet.handler.SimpleUrlHandlerMapping">
		<property name="urlMap">
			<map>
				<entry key="/**" value-ref="defaultServletHttpRequestHandler" />
			</map>
		</property>
		<property name="order" value="2147483647" />
	</bean>
	<!-- mvc:default-servlet-handler 태그가 생성하는 빈 설정 끝 -->

	
	<!-- mvc:view-controller 태그가 생성하는 빈 설정 시작 -->
	<bean name="org.springframework.web.servlet.config.viewControllerHandlerMapping"
		class="org.springframework.web.servlet.handler.SimpleUrlHandlerMapping">
		<property name="order" value="1" />
		<property name="urlMap">
			<map>
				<!-- mvc:view-controller 마다 한 개의 entry 생성 -->
				<entry key="/index">
					<bean class="org.springframework.web.servlet.mvc.ParameterizableViewController">
						<property name="viewName" value="index" />
					</bean>
				</entry>
			</map>
		</property>
	</bean>
	<!-- mvc:view-controller 태그가 생성하는 빈 설정 끝 -->


	<!-- mvc:resources 설정 시작 -->
	<!-- mvc:resources 마다 -->
	<bean id="resourceHttpRequestHandler" class="org.springframework.web.servlet.resource.ResourceHttpRequestHandler">
		<property name="locations">
			<list>
				<value>/images/</value>
				<value>/WEB-INF/resources/</value>
			</list>
		</property>
		<property name="cacheSeconds" value="60" />
	</bean>
	
	<bean class="org.springframework.web.servlet.handler.SimpleUrlHandlerMapping">
		<property name="urlMap">
			<map>
				<entry key="/images/**" value-ref="resourceHttpRequestHandler" />
			</map>
		</property>
		<property name="order" value="2147483646" />
	</bean>
	<!-- mvc:resources 설정 끝 -->


	<!-- mvc:interceptors 설정 시작 -->
	<bean class="org.springframework.web.servlet.handler.MappedInterceptor">
		<constructor-arg>
			<list>
				<value>/acl/**</value>
			</list>
		</constructor-arg>
		<constructor-arg>
			<list></list>
		</constructor-arg>
		<constructor-arg>
			<bean class="chap07.common.AuthInterceptor" />
		</constructor-arg>
	</bean>
	
	<bean class="org.springframework.web.servlet.handler.MappedInterceptor">
		<constructor-arg><null /></constructor-arg>
		<constructor-arg><null /></constructor-arg>
		<constructor-arg>
			<bean class="chap07.common.MeasuringInterceptor" />
		</constructor-arg>
	</bean>

	<bean class="org.springframework.web.servlet.handler.MappedInterceptor">
		<constructor-arg>
			<list>
				<value>/acl/**</value>
				<value>/header/**</value>
				<value>/newevent/**</value>
			</list>
		</constructor-arg>
		<constructor-arg>
			<list>
				<value>/acl/modify</value>
			</list>
		</constructor-arg>
		<constructor-arg>
			<ref bean="commonModelInterceptor" />
		</constructor-arg>
	</bean>
	<!-- mvc:interceptors 설정 끝 -->	
	<bean id="commonModelInterceptor"
		class="chap07.common.CommonModelInterceptor" />
	
	<!-- mvc 어노테이션에 의해 추가로 등록되는 빈들 -->
	<bean id="BeanNameUrlHandlerMapping" class="org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping">
		<property name="order" value="2" />
	</bean>
	
	<bean id="HttpRequestHandlerAdapter" class="org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter">
	</bean>
	
	<bean id="SimpleControllerHandlerAdapter" class="org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter">
	</bean>
	<!-- mvc 어노테이션에 의해 추가로 등록되는 빈들 -->
	
	<bean id="viewResolver"
		class="org.springframework.web.servlet.view.InternalResourceViewResolver">
		<property name="prefix" value="/WEB-INF/view/" />
		<property name="suffix" value=".jsp" />
	</bean>

	<bean id="memberService" class="chap07.member.MemberService" />

	<bean class="chap07.event.EventController" />
	<bean class="chap07.event.EventCreationController" />

	<bean class="chap07.member.RegistrationController">
		<property name="memberService" ref="memberService" />
	</bean>
	<bean class="chap07.member.MemberController">
		<property name="memberService" ref="memberService" />
	</bean>

	<bean class="chap07.member.MemberModificationController">
		<property name="memberService" ref="memberService" />
	</bean>

	<bean class="chap07.file.FileController" />
	<bean class="chap07.search.SearchController" />
	<bean class="chap07.etc.SimpleHeaderController" />

	<bean id="aclService" class="chap07.ac.AclService" />
	<bean class="chap07.ac.ACLController">
		<property name="aclService" ref="aclService" />
	</bean>

	<bean id="authenticator" class="chap07.auth.Authenticator">
		<constructor-arg ref="memberService" />
	</bean>
	<bean class="chap07.auth.LoginController">
		<property name="authenticator" ref="authenticator" />
	</bean>

	<bean class="chap07.calculator.CalculationController" />

	<bean class="chap07.exhandler.CommonExceptionHandler" />

	<bean id="messageSource"
		class="org.springframework.context.support.ResourceBundleMessageSource">
		<property name="basenames">
			<list>
				<value>message.error</value>
			</list>
		</property>
		<property name="defaultEncoding" value="UTF-8" />
	</bean>

</beans>
```



### 3.3 서블릿 매핑에 따른 컨트롤러 경로 매핑과 디폴트 서블릿 설정

* 웹 개발 초기에는 확장자를 이용한 매핑 설정을 많이 사용했지만, 최근에는 의미에 맞는 URL을 사용하는 곳이 증가 되는 추세.

```xml
<servlet-mapping>
    <servlet-name>dispatcher</servlet-name>
    <url-pattern>/message/*</url-pattern>
    <url-pattern>/comic</url-pattern>
</servlet-mapping>
```

```java
@Controller
public class SomeController {
	@RequestMapping("/message/list")
	public String message(Model model) {...}
	
	@RequestMapping("/comic")
	public String comic(HttpServletResponse response) {...}
}
```

*   http://localhost:8080/spring4-chap07/message/list
    *   404 에러
    *   RequestMappingHandlerMapping 의 동작 방식
        * 서블릿 매핑이 '/경로/*' 형식이면 '/경로/' 이후 부분을 사용해서 컨트롤러 검색
        * 아니면, 건텍스트 경로 제외한 나머지 경로를 사용해서 컨트롤러 검색

          ​
| 요청 URL                          | 서블릿 매핑 URL 패턴 | 컨트롤러 매핑 경로       |
| ------------------------------- | ------------- | ---------------- |
| /spring4-chap07/message/list.do | /message/*    | /list.do         |
| /spring4-chap07/message/list.do | *.do          | /message/list.do |
| /spring4-chap07/comic           | /comic        | /comic           |

*   DispatcherServlet에 대한 매핑 URL 패턴 설정에 따라 컨트롤러의 경로가 달라지는 것을 원치 않을 경우
    *   mvc namespace 사용하지 않는다면
        * RequestMappingHandlerMapping의 alwaysUseFullPath를 true로 지정.

          ```xml
          <!-- <mvc:annotation-driven/> -->

          <bean name="handlerMapping" class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping">

          <property name="alwaysUseFullPath" value="true" />

          </bean>

          <bean id="handlerAdapter" class="org.springframework.servlet.mvc.method.annotation.RequestMappingHandlerAdapter"/>
          ```
    *   mvc namnespace 사용하는 경우 alwaysUseFullPath와 같은 설정 방법을 제공 안함.
    *   하지만 사용하고 싶다면
        * 서블릿 매핑 설정에서 URL패턴을 '/'로 지정

          ```xml
          <servlet>
            <servlet-name>dispatcher</servlet-name>
            <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
          </servlet>
          <servlet-mapping>
            <servlet-name>dispatcher</servlet-name>
            <url-pattern>/</url-pattern>
          </servlet-mapping>
          ```

        * 스프링 MVC 설정에 디폴트 서블릿 핸들러 설정(==**<mvc:default-servlet-handler/>**==추가.)        

          ```xml
          <mvc:annotation-driven/>
          <mvc:default-servlet-handler/>
          ```

*   URL 매핑 패턴을 <url-pattern>/</url-pattern> 으로 설정
    * jsp 요청을 제외한 나머지 모든 요청을 DispatcherServlet이 받음.
    * /bootstrap/bootstrap.min.css ?
        * 404 응답 코드 전송
        * 컨테이너의 디폴트 서블릿을 이용해서 요청을 처리하도록 함(<mvc:default-servlet-handler/>)


* &lt;mvc:default-servlet-handler/&gt;
    * 디폴트 서블릿 핸드러가 빈으로 등록
        * 요청 URL에 매핑되는 컨트롤러를 검색
            * 존재할 경우, 컨트롤러를 이용해서 클라이언트 요청을 처리
        * 디폴트 서블릿 핸들러가 등록되어 있지 않다면,
            * 404 응답 에러
        * 디폴트 서블릿 핸들러가 등록되어 있다면, 디폴트 서블릿 핸들러에 요청을 전달
            * 디폴트 서블릿 핸들러는 WAS의 디폴트 서블릿(JSP에 대한 요청 처리)에 요청 전달

* 각 WAS는 서블릿 매핑에 존재하지 않는 요청을 처리하기 위한 디폴트 서블릿을 제공
    * JSP에 대한 요청을 처리

```java
@Configuration
@EnableWebMvc
public class SampleConfig extends WebMvcConfigurerAdapter {
	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
}
```
* WebMvcConfigureAdapter 클래스
    * @EnableWebMvc 어노테이션을 이용해서 MVC를 설정할 때 사용하는 클래스




## 04. 컨트롤러 구현

### 4.1 @Controller/@RequestMapping/Model 을 이용한 컨트롤러 구현

* 컨트롤러 구현
    * @Controller 어노테이션을 클래스에 적용
    * @RequestMapping 어노테이션을 이용해 처리할 요청 경로 지정
    * 웹브라우저의 요청을 처리할 메소드 구현, 뷰 이름 리턴

```java
@Controller
@RequestMapping("/event")
public class EventController {
    @RequestMapping("/event/list")
    public String list() {	// 웹 요청을 처리할 메소드
    	return "event/detail";	//뷰 이름 리턴
    }
}
```

*   뷰에서 결과를 보여줄 때 필요한 데이터를 전달하기 위해 사용하는 것이 ==**모델(Model)**==이다.
    * 컨트롤러는 뷰에서 필요로 하는 데이터를 모델에 담아서 전달

    * 뷰는 결과를 생성하는데 필요한 데이터를 모델에서 가져와 사용.

      ```java
      @Controller
      @RequestMapping("/event")
      public class EventController {
        @RequestMapping("/event/list")
          public String list(Model model) {
            List<Event> eventList = getOpenedEventList();
            model.setAttribute("eventList", eventList);
            return "event/detail";
         }
      }
      ```

      ```html
        <ul>
          <c:forEach var="event" items="${eventList}">
            <li>${event.name}</li>
          </c:forEach>
        </ul>
      ```
      ​


* Model 에 데이터를 추가하는데 사용되는 메소드
    * Model addAttribute(String attrName, Object attrValue)
        * 체이닝 메소드
    * Model addAllAttributes(Map<String, ?> attributes)
        * 체이닝 메소드
    * boolean containsAttribute(String attrName)




#### (1) ModelAndView를 사용한 모델/뷰 처리

* ModelAndView
    * 모델 설정과 뷰 이름을 합쳐 놓은 것
    * @Controller를 이용한 컨트롤러 구현이 대세로 자리 잡기전에 리턴 타입은 ModelAndView

```java
@Controller
@RequestMapping("/event")
public class EventController {
	@RequestMapping("/list2")
	public ModelAndView list2(SearchOption option) {
		List<Event> eventList = eventService.getOpenedEventList(option);
		ModelAndView modelView = new ModelAndView();
		modelView.setViewName("event/list");
		modelView.addObject("eventList", eventList);
		modelView.addObject("eventTypes", EventType.values());
		return modelView;
	}
}
```

* 소스 설명
    *  Model을 사용하는 경우 뷰 이름을 리턴하지만, ModelAndView를 사용하는 경우는 `setViewName()`을 이용
    *  Model은 `addAttribute()` 메소드를 사용하는데, ModelAndView를 사용하는 경우는 `addObject()`를 사용




### 4.2 @RequestMapping 을 이용한 요청 매핑

*   @RequestMapping 어노테이션
    * 요청 URL을 어떤 메소드를 처리할지 여부를 결정

      ```
      1. @RequestMapping 을 이용한 경로 지정
      2. @클래스와 메소드에 @RequestMapping 적용
      3. HTTP 전송 방식 지정
      4. @PathVariable을 이용한 경로 변수
      5. Ant 패턴을 이용한 경로 매핑
      6. 처리 가능한 요청 컨텐츠 타입/응답 가능한 컨텐트 타입 한정.
      ```

      ​

#### (1) @RequestMapping을 이용한 경로 지정

* @RequestMapping 어노테이션의 값으로 경로를 지정
* 서블릿 매핑의 URL 패턴에 따라 달라짐
| 컨텍스트 경로 | DispatcherServlet | 실제 URL                                  |
| ------- | ----------------- | --------------------------------------- |
| /chap07 | /                 | http://host:port/chap07/event/list      |
| /chap07 | /main/*           | http://host:port/chap07/main/event/list |

*   @RequestMapping 속성
    * value 속성을 사용한 경로 지정

      ```java
      @Controller
      public class EventController {
      	@RequestMapping(value="/event/create", method=RequestMethod.POST)
      	public String create(...) {
      		...
      	}
      }
      ```

    * 여러 경로를 한 메소드에서 처리

      ```java
      @Controller
      public class HomeController {
        @RequestMapping("/main","/index")
        public String aliasOfHome() {
        	...
        }
      }
      ```

      ​

#### (2) 클래스와 메소드에 @RequestMapping 적용

```java
@Controller
@RequestMapping("/event")
public class EventController {
	@RequestMapping("/list")
	public String list(Model model) {
		...
		return "event/list"
	}
	
	@RequestMapping("/home")
	public String home(Model model) {
		...
	}	
}
```

* 소스 설명
    * 컨트롤러와 메소드에 @RequestMapping 어노테이션 적용
        * 클래스에 적용한 값과 메소드에 적용한 값을 조합해서 매핑 경로 결정
            * list() : /event/list
            * home() : /event/home
        * 컨트롤러 클래스가 특정 경로를 기준으로 그 하위 경로만을 처리한다는 것을 의미.
        * 같은 경로를 공유하는 경우, 
            * 클래스에 @RequestMapping 어노테이션을 적용함으로서 코드에서 직관적으로 ==**공통 경로 확인 가능**==




#### (3) HTTP 전송 방식 지정.

* @RequestMapping 어노테이션은 method 속성을 이용해서 메소드에서 처리할 전송 방식 지정

```java
package chap07.member;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/member/regist")
public class RegistrationController {
	private static final String MEMBER_REGISTRATION_FORM = "member/registrationForm";
	
	private MemberService memberService;

	@RequestMapping(method = RequestMethod.GET)
	public String form(@ModelAttribute("memberInfo") MemberRegistRequest memRegReq) {
		return MEMBER_REGISTRATION_FORM;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String regist(
			@ModelAttribute("memberInfo") MemberRegistRequest memRegReq,
			BindingResult bindingResult) {
		new MemberRegistValidator().validate(memRegReq, bindingResult);
		if (bindingResult.hasErrors()) {
			return MEMBER_REGISTRATION_FORM;
		}
		memberService.registNewMember(memRegReq);
		return "member/registered";
	}

	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}

}
```

*   소스 설명
    * 같은 URL 요청이 들어오더라도 HTTP 전송 방식이 GET이면 form() 메소드, POST이면 regist() 메소드가 처리

*   org.spring.web.bing.annotation.RequestMethod 
    * GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE

      ```
      웹 브라우저는 GET/POST 방식만을 지원
      PUT/DELETE 방식의 요청은 보낼수 없음.
      이런 문제 해결을 위해 스프링은 HiddenHttpMethodFilter 를 제공
      ```




#### (4) @PathVariable을 이용한 경로 변수

```java
@Controller
public class MemberController {
	@RequestMapping("/members/{memberId}")
	public String memberDetail(@PathVariable String memberId, Model model) {
		MemberInfo mi = memberService.getMemberInfo(memberId);
		if (mi == null) {
			return "member/memberNotFound";
		}
		model.addAttribute("member", mi);
		return "member/memberDetail";
	}
}
```

* 소스 설명
    * @RequestMapping 어노테이션 경로에 **=={변수} 형식의 경로 변수 사용==**가능
        * @PathVariable 어노테이션을 사용하면 경로 변수의 값을 파라미터로 전달받음
    * @PathVariable 어노테이션
        * 파라미터 이름과 동일한 이름을 갖는 경로 변수 사용



```java
@Controller
public class MemberController {
	@RequestMapping("/members/{mid}")
	public String memberDetail(@PathVariable("mid") String memberId, Model model) {
		MemberInfo mi = memberService.getMemberInfo(memberId);
		if (mi == null) {
			return "member/memberNotFound";
		}
		model.addAttribute("member", mi);
		return "member/memberDetail";
	}
}
```

* 소스 설명
    * 파라미터 이름이 경로 변수와 다를 경우
        * 경로 변수를 값으로 지정. @PathVariable



```java
@RequestMapping("/membner/{memberId}/orders/{orderId}")
public String memberOrderDetail(
	@PathVariable("memberId") String memberId,
	@PathVariable("orderId") Long orderId, Model model) {
	...
}
```
*   소스 설명
    *   경로 변수 한개 이상 사용 가능
    *   경로 변수의 값을 파라미터의 타입에 맞게 변환
        * 변환할 수 없을 경우, 400 에러 코드를 응답 결과로 전송.
    *   경로 변수에 정규 표현식 사용 가능
        * 경로 변수 이름 뒤에 **==콜론과 정규 표현식을 함께 사용==**
        * 정규 표현식에 매칭되지 않는 경로인 경우 404에러 응답        
        ```java
        @RequestMapping("/files/{fileId:[a-zA-Z]\\d\\d\\d}")
        public String fileInfo(@PathVariable String fileId) {
        ...
        }

        ```
        * 정규 표현식이 사용되는 부분은 업무 영영의 규칙인 경우가 많다.
            * 컨트롤러 경로변수에 정규 표현식 사용보다
            * 서비스와 같은 도메인 영역의 코드에서 파일 ID 검삭하는 것이 유지보수성을 높여준다.



#### (5) Ant 패턴을 이용한 경로 매핑

* @RequestMapping 어노테이션의 값으로 Ant 패턴 사용 가능
* Ant 패턴
    * \* \- 0 개 또는 그 이상의 글자
    * \? \- 1개 글자
    * \*\* \- 0개 또는 그 이상의 디렉토리 경로

* ex)
    * @RequestMapping("/member/?\*.info")
        * /member/로 시작하고 확장자가 .info 로 끝나는 모든 경로
    * @RequestMapping("/faq/f?00.fq")
        * /faq/f 로 시작하고, 1글자가 사이에 위치하고 00.fq로 끝나는 모든 경로
    * @RequestMapping("/folders/\*\*/files")
        * /folders/로 시작하고, 중간에 0개 이상의 중간 경로가 존재하고, /files로 끝나는 모든 경로


*   고민
    * http://host/ctxpath/folders/category1/files

    * http://host/ctxpath/folders/category1/sub1/files

    * http://host/ctxpath/folders/cat2/sub2/sub3/sub4/files

      ```java
      @RequestMapping("/folders/**/files")
      public String list(HttpServletRequest request, Model model) {
        String url = request.getRequestURI();
        String[] folderIds = null;
        if(url.endsWith("/folders/files")) {
            folderIds = new String[0];
        } else {
            String ctxPath = request.getContextPath();
            String path = ctxPath.isEmpty() ? uri : uri.substring(ctxPath.length())_;
            String folderTreePath = 
                path.substring("/folders/".length(), path.length() - "/files".length());
            folderIds = folderTreePath.split("/");		
        }

        model.addAttribute("folderIds", folderIds);
        return "files/filesInFolder";
      } 
      ```

    * ==**o.s.util.AntPathMatcher 클래스 참조 : 패턴 부분 쉽게 찾기.**==  

      ​

#### (6) 처리 가능한 요청 컨텐트 타입/요청 가능한 컨텐트 타입 한정

*   요청 컨텐트 타입을 제한
    * **==consumes  속성==**

      ```java
      @RequestMapping(value="/members", method=RequestMethod.POST,
      				consumes="application/json")
      public Result addMember(@RequestBody NewMember mem) {
      	...
      }
      ```

      ​

*   응답 결과로 요구하는 요청 처리(Accept 요청 헤더)
    * **==produces 속성==**

      ```java
      @RequestMapping(value="/member/{memberId}", method=RequestMethod.GET,
      				produces="application/json")
      @ResponseBody
      public MemberInfo getMember(@PathVariable String memberId) {
      	...
      }
      ```

      ​

## 4.3 HTTP 요청 파라미터와 폼 데이터 처리.

* 웹 브라우저에서 서버에 전송한 데이터를 처리하는 방법
    * HttpServletRequest의 getParameter()를 이용해서 구하기
    * @RequestParam 어노테이션을 이용해서 처리
    * 커맨드 객체를 이용해서 처리

```
1. HttpServletRequest를 이용한 요청 파라미터 구하기
2. @RequestParam 어노테이션을 이용한 요청 파라미터 구하기
3. 커맨드 객체를 이용한 폼 전송 처리하기
4. 커맨드 객체의 중첩 객체 프로퍼티 지원
5. 커맨드 객체의 배열/리스트 타입 프로퍼티 처리
6. GET/POST에서 동일 커맨드 객체 사용하기
```



#### (1) HttpServletRequest를 이용한 요청 파라미터 구하기

* @RequestMapping 메소드에 HttpServletRequest 타입의 인자를 추가, getParameter() 메소드를 이용해서 요청 파라미터 처리.

```java
@Controller
@RequestMapping("/event")
public class EventController {
	@RequestMapping("/detail")
	public String detail(HttpServletRequest request, Model model) throws IOException {
		String id = request.getParameter("id");
		if (id == null)
			return "redirect:/event/list";
		Long eventId = null;
		try {
			eventId = Long.parseLong(id);
		} catch (NumberFormatException e) {
			return "redirect:/event/list";
		}
		Event event = getEvent(eventId);
		if (event == null)
			return "redirect:/event/list";

		model.addAttribute("event", event);
		return "event/detail";
	}
}
```

* 소스 설명
    * request.getParameter("id")로 값을 구함.
    * redirect:/event/list : HttpServletResponse의 리다이렉트를 사용해서 지정한 경로로 리다이렉트




#### (2) @RequestParam 어노테이션을 이용한 요청 파라미터 구하기

```java
@Controller
@RequestMapping("/event")
public class EventController {
	private static final String REDIRECT_EVENT_LIST = "redirect:/event/list";	

	@RequestMapping("/detail2")
	public String detail2(@RequestParam("id") long eventId, Model model) {
		Event event = getEvent(eventId);
		if (event == null)
			return REDIRECT_EVENT_LIST;
		model.addAttribute("event", event);
		return "event/detail";
	}
}
```

*   id 요청 파라미터의 값이 eventId 파라미터를 통해서 전달
*   파라미터 타입에 맞게 변환
*   값이 없거나, long 타입으로 변환할 수 없을 경우 
    * 400 에러 코드 반환
*   요청 파라미터가 필수가 아닌경우
    * **==required 속성==**을 false로 지정
*   요청 파라미터가 존재하지 않을때 null대신 다른 값을 사용하도록 설정
    * 코드에서 null 검사 생략 가능.

    * **==defaultValue 속성==**

      ```java
      @Controller
      public class SearchController {
      	@RequestMapping("/search")
      	public String search(@RequestParam(value = "q", defaultValue="") String query,
      						Model model) {
          	System.out.println("검색어: " + query);
          	return "search/result";
      	}
      }
      ```

      ​



#### (3) 커맨드 객체를 이용한 폼 전송 처리하기

*   요청 폼

    ```html
    <html>
    <head>
    	<title>회원 가입</title>
    </head>
    <body>
      <form method="post">
        <label for="email">이메일</label>: 
        <input type="text" name="email" id="email" value="${memberInfo.email}"/>

        <label for="name">이름</label>: 
        <input type="text" name="name" id="name" value="${memberInfo.name}" />

        <label for="password">암호</label>: 
        <input type="password" name="password" id="password" value="${memberInfo.password}"/>

        <label for="password">확인</label>: 
        <input type="password" name="confirmPassword" id="confirmPassword" 
               value="${memberInfo.confirmPassword}"/>

        <input type="submit" value="가입" />
      </form>
    </body>
    </html>
    ```

    ​

*   @RequestParam 을 이용한 방법 

    ```java
              @RequestMapping(method=RequestMethod.POST)

              public String regist(@RequestParam("email") String email, 
              	@RequestParam("name") String name, 
              	@RequestParam("password") String password,
              	@RequestParam("confirmPassword") String confirmPassword) {
                		...						
              }	
    ```


*   자바 빈 규약에 맞는 프로퍼티 get/set 정의하는 클래스

    ```java
        public class MemberRegistRequest {
          private String email;
          private String name;
          private String password;
          private String confirmPassword;

          ... //get/set 메소드
        }
    ```

    ​

*   요청 파라미터를 값으로 전달받을 객체를 메소드의 파라미터로 지정.

    ```java
            @Controller
              @RequestMapping("/member/regist")
              public class RegistrationController {
                private static final String MEMBER_REGISTRATION_FORM = "member/registrationForm";

                @RequestMapping(method = RequestMethod.POST)
                public String regist(
                		MemberRegistRequest memRegReq) {
                  ...            
                return "member/registered";
              }
            }
    ```

*   소스 설명

    * request 파라미터의 값을 객체의 프로퍼티로 복사할 때
        * 같은 이름을 갖는 요청 파라미터와 프로퍼티를 매핑

*   ==**커맨드 객체**== : MemberRegistRequest 객체와 같이 HTTP 요청 파라미터 값을 전달받을 때 사용되는 객체
    * 커맨드 객체는 **==뷰에 전달할 모델에 자동으로 포함==**. 
      * memberRegReq 커맨드 객체는 **==memberRegistRequst==**라는 이름의 모델로 뷰에 전달(단순 클래스 이름의 첫 글자를 소문자로 변환한 이름)
      * ```xml
        ${memberRegistRequest.name} 님의 회원 가입을 완료했습니다.
        ```

    ​

*   커맨드 객체의 프로퍼티 타입에 맞게 자동 변환

    ```java
            public class MemberRegistRequest {
              private boolean allowNoti;
                ...
            }
    ```
    ```html
            <label>
              <input type="checkbox" name="allowNoti" value="true"/>
            </label>
    ```

        ​

*   같은 이름의 요청 파라미터가 두 개 이상 존재할 경우, 배열을 통해 전달 받음

    * 배열대신 Collection이나 List와 같은 콜렉션 타입 사용 가능.

      ```java
      public class MemberRegReq {
      	private int[] favoriteIds;

      	public void setFavoriteIds(int[] favoriteIds) {
      	    this.favoriteIds = favoriteIds;
      	}

      	public int[] getFavoriteIds() {
      	    return favoriteIds;
      	}    
      }
      ```

      ```java
      public class MemberRegReq {
        private List<Integer> favoriteIds;
        public void setFavoriteIds(List<Integer> favoriteIds) {
            this.favoriteIds = favoriteIds;
        }

        public List<Integer> getFavoriteIds() {
            return favoriteIds;
        }    
      }
      ```

      ​



* 커맨드 객체 사용 이점
    * 다수의 요청 파라미터 값을 한개의 객체에 담기때문에 폼 전송이나 요청 파라미터 개수가 많은 경우에 컨트롤러 코드를 간결하게 유지
    * 스프링이 제공하는 객체 검증, 타입 변환등을 이용하여, 번거로운 코드 줄임




#### (4) 커맨드 객체의 중첩 객체 프로퍼티 지원

```java
public class MemberRegistRequest {

    private String email;
    private String name;
    private String password;
    private String confirmPassword;
    private boolean allowNoti;
    private Address address;
    
    ...

}
```

```java
public class Address {

    private String address1;
    private String address2;
    private String zipcode;
    
    ...

}	
```



* address 프로퍼티까지 값을 채우고 싶다면

  ```html
  <form method="post">

  <label>주소</label>:

  주소1 

  <input type="text" name="address.address1" value="${memberInfo.address.address1}" />

  <form:errors path="memberInfo.address.address1"/> <br/>

  주소2

  <input type="text" name="address.address2" value="${memberInfo.address.address2}" />

  <form:errors path="memberInfo.address.address2"/> <br/>

  <input type="submit" value="가입" />

  </form>

  ```

  ​

#### (5) 커맨드 객체의 배열/리스트 타입 프로퍼티 처리

* 계정별로 읽기/생성/수정/삭제 권한을 관리하는 어플리케이션

  ```
  public class AccessPerm {

      private String id;
      private boolean canRead;
      private boolean canCreate;
      private boolean canModify;
      private boolean canDelete;
      private boolean removed;
      
      ...

  }
  ```

  ​

* 한 계정의 권한 설정 정보를 표현하기 위한 클래스

  ```java
  public class AclModRequest {

      private List<AccessPerm> perms;

      public List<AccessPerm> getPerms() {
      	return perms;
      }
      
      public void setPerms(List<AccessPerm> permissions) {
      	this.perms = permissions;
      }

  }
  ```

  ​


* 한번에 여러 계정에 대한 접근 권한을 변경하기 위한 정보

  ```java
  public class AclService {

      ...
      public void modifyAccessControll(AclModRequest modReq) {
      	for (AccessPerm perm : modReq.getPerms()) {
      		AccessPerm ap = map.get(perm.getId());
      		if (ap != null)
      			ap.copyFrom(perm);
      	}
      }

  }
  ```

  ​


* 접근 권한 정보 변경 기능을 제공하는 코드

* Issue : 요청 파라미터 -> AclModRequest 객체로 변환

  ```java
  @Controller

  public class ACLController {

      @RequestMapping("/acl/modify")
      public String modify(AclModRequest modReq) {
      	List<AccessPerm> perms = new ArrayList<>();
      	for (AccessPerm reqPerm : modReq.getPerms())
      		if (reqPerm.hasData())
      			perms.add(reqPerm);
      	modReq.setPerms(perms);
      
      	aclService.modifyAccessControll(modReq);
      	return "redirect:/acl/list";
      }

  }
  ```

  ​


*   List나 배열처럼 인덱스를 가진 프로퍼티 설정
    *   요청 파라미터 이름에 인덱스 값 지정

    *   `프로퍼티이름[인덱스].프로터피이름`

        ```html
            <input type="hidden" name="perms[0].id" value="roi">
            <input type="checkbox" name="perms[0].canRead" value="true" checked>
            ...
            <input type="hidden" name="perms[1].id" value="roi">
            <input type="checkbox" name="perms[1].canRead" value="true" checked>
        ```

    *   매핑 방식
        * perms[0] -> aclModRequest.perms.get(0) 
        * perms[0].id -> aclModRequest.get(0).setId(파라미터값)
        * perms[1].canRead -> aclModRequest.perms.get(1).setCanRead(파라미터 값)

    *   인덱스 번호가 중간에 빈다면(1번이 없다면.)
        * ==**perms.get(1) 이 null이 아니다.**==
        * 위 코드 예제처럼 `.hasData()` 메소드 사용
        * perms.get(0) : (id:'bkchoi', canRead:'true')
        * perms.get(1) : (id: null, canRead: null)
        * perms.get(2) : (id: 'madvirus', canRead: 'false')



#### (6) GET과 POST 에서 동일 커맨드 객체 사용하기

```java
@Controller
@RequestMapping("/member/regist")
public class RegistrationController {
    private static final String MEMBER_REGISTRATION_FORM = "member/registrationForm";
    private MemberService memberService;

    @RequestMapping(method = RequestMethod.GET)
    public String form(@ModelAttribute("memberInfo") MemberRegistRequest memRegReq) {
    	return MEMBER_REGISTRATION_FORM;
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public String modify(
    		@ModelAttribute("memberInfo") MemberRegistRequest memRegReq) {
    	try {
        	memberService.modifyMemberInfo(modReq);
            return "member/modified";
        } catch(NotMatchPasswordException ex) {
        	return MEMBER_MODIFICATION_FORM_VIEW;
        } catch(MemberNotFoundException ex) {
        	return MEMBER_NOT_FOUND_VIEW;
        }
    }
    
    public void setMemberService(MemberService memberService) {
    	this.memberService = memberService;
    }
}
```

* 소스 설명
  * modify() 메소드와 form() 메소드는 동일한 타입의 커맨드 객체를 사용.
  * 두 메소드에서 사용하는 커맨드 객체의 타입이 같으므로, 뷰코드는 GET요청이나 POST 요청인지 여부에 상관없이 폼 생성 가능.(뷰 공통으로 사용)




### 4.4 @ModelAttribute를 이용한 모델 데이터 처리

```
1. @ModelAttribute 를 이용한 커맨드 객체 이름 지정.
2. @ModelAttribute 어노테이션을 이용한 공통 모델 처리
```



#### (1) @ModelAttribute 를 이용한 커맨드 객체 이름 지정

* 커맨드 객체는 (첫글자를 소문자로 바꾼) 클래스 이름을 사용해서 모델에서 접근할 수 있음.
* ==**@ModelAttribute**==를 이용해서 다른 이름으로 리네임 할수 있다.

```java
@Controller
@RequestMapping("/member/regist")
public class RegistrationController {
    @RequestMapping(method = RequestMethod.POST)
    public String regist(
    		@ModelAttribute("memberInfo") MemberRegistRequest memRegReq) {
    	...		
    	return "member/registered";
    }
}
```

* 소스 설명
    * regist() 메소드에서 memRegReq 파라미터에 ==**@ModelAttribute("memberInfo") 어노테이션 적용.**== 
        * 이 경우 memRegReq 커맨드 객체는 모델에 **"memberInfo" 라는 이름으로 저장**.




```
${memberInfo.name}님의 회원 가입을 완료했습니다.
```

* 소스 설명
    * jsp 에서 memberInfo 라는 이름으로 커맨드 객체에 접근 가능.




#### (2) @ModelAttribute 어노테이션을 이용한 공통 모델 처리

* Case
    * 목록 화면과 상세 보기화면에서 함께 사용되는 데이터가 있다면


```java
@Controller

@RequestMapping("/event")

public class EventController {

    @ModelAttribute("recEventList")
    public List<Event> recommend() {
    	return eventService.getRecommendedEventService();
    }
    
    @RequestMapping("/list")
    public String list(Model model) {
    	List<Event> eventList = eventService.getOpenedEventList(option);
    	model.addAttribute("eventList", eventList);
    	return "event/list";
    }
    
    @RequestMapping("/detail")
    public String detail(HttpServletRequest request, Model model) throws IOException {
    	...
    	return "event/detail";
    }
}
```

* 소스 설명
    * recommend() 메소드의 @ModelAttribute 어노테이션
        * recommend() 메소드의 리턴 결과를 "recEventLIst" 모델 속성으로 추가.
        * /event/list 나 /event/detail 의 뷰코드에서 recEventList 로 recomment() 메소드가 리턴한 객체에 접근
* 같은 값을 같는 @ModelAttribute 어노테이션을 사용하여 @RequestMapping 어노테이션이 적용된 메소드에서 접근 가능.

```java
@Controller
@RequestMapping("/event")
public class EventController {
    @ModelAttribute("recEventList")
    public List<Event> recommend() {
    	return eventService.getRecommendedEventService();
    }
    
    @RequestMapping("/list")
    public String list(@ModelAttribute("recEventList") List<Event> recEventList, Model model) {
    	...
    }
}
```



### 4.5 @CookieValue와 @RequestHeader를 이용한 쿠키 및 요청 헤더 구하기

* 쿠키값 : @CookieValue
* 요청 헤더 : @RequestHeader


```java
@Controller

public class SimpleHeaderController {

    @RequestMapping("/header/simple")
    public String simple(
    		@RequestHeader(value = "Accept", defaultValue = "text/html") String acceptType,
    		@CookieValue(value = "auth", required = false) Integer authValue,
    		Model model) {
    	model.addAttribute("acceptType", acceptType);
    	if (authValue != null)
    		model.addAttribute("auth", authValue);
    	return "header/simpleValue";
    }
}
```

* 소스 설명 
    * @RequestHeader("Accept") : "Accept" 요청 헤더의 값
    * @CookieValue("auth") : 이름이 "auth"인 쿠키
    * required 속성이 없는 경우, 존재하지 않으면, ==400에러 응답==.

* required 속성 : 필수 여부
* defaultValue 속성 : 기본 값 지정.

### 4.6 리다이렉트 처리

*   컨트롤러에서 클라이언트의 요청을 처리한 후에 다른 페이지로 리다이렉트를 원한다면
    * 뷰 이름 앞에 ==**"redirect:"**== 접두어 추가.

    ```java
    @RequestMapping("/header/createauth")

    public String createAuth(HttpServletResponse response,

            Model model) {
        Random random = new Random();
        String authValue = Integer.toString(random.nextInt());
        response.addCookie(new Cookie("auth", authValue));
        return "redirect:simple";
        // return "redirect:http://localhost:8080/spring4-chap07/index.jsp";
    }
    ```

    ​

*   / 로 시작하면 : 웹 어플리케이션 내에서의 절대 경로

*   / 로 시작하지 않으면 : @RequestMapping 어노테이션의 상대 경로

*   redirect: 뒤에 완전한 URL을 적으면 : 해당 URL 로 리다이렉트\

    ```java
              @RequestMapping("/header/createauth")
              public String createAuth(HttpServletResponse response,
                    Model model) {
                Random random = new Random();
                String authValue = Integer.toString(random.nextInt());
                response.addCookie(new Cookie("auth", authValue));
                return "redirect:http://localhost:8080/spring4-chap07/index.jsp";
              }
    ```


*   redirect: 에 경로 변수를 사용할수있다.

    ```java
          @RequestMapping(value = "/files/{fileId:[a-zA-Z]\d{3}}", method = RequestMethod.POST)
          public String updateFile(@PathVariable String fileId) {
            return "redirect:/files/{fileId}";
          }
    ```



## 05. 커맨드 객체 값 검증과 에러 메시지

*   스프링
    * 서버 측의 요청 파라미터 검사 기능 지원

    * 객체의 값을 검증할 수 있는 기능 제공

      ​

### 5.1 Validator와 Errors/BindingResult를 이용한 객체 검증

* org.spring.validation.Validator 인터페이스를 사용하여
    * 객체 검증
    * 에러 메시지 지원

```java
public interface Validator {

    boolean supports(Class<?> clazz);
    void validate(Object target, Errors errors);

}
```

*   소스 설명
    *   supports()
        * Validator 가 해당 타입의 객체를 지원하는 여부 리턴
    *   validate()
        * 실제 값을 검증하는 코드

        * errors 파라미터는 값이 올바르지 않을 경우, 그 내용을 저장하기 위해 사용

          ​

```java
public class MemberRegistValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
    	return MemberRegistRequest.class.isAssignableFrom(clazz);
    }
    
    @Override
    public void validate(Object target, Errors errors) {
    	MemberRegistRequest regReq = (MemberRegistRequest) target;
    	if (regReq.getEmail() == null || regReq.getEmail().trim().isEmpty())
    		errors.rejectValue("email", "required");
    
    	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "required");
    	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "required");
    	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPassword", "required");
    	if (regReq.hasPassword()) {
    		if (regReq.getPassword().length() < 5)
    			errors.rejectValue("password", "shortPassword");
    		else if (!regReq.isSamePasswordConfirmPassword())
    			errors.rejectValue("confirmPassword", "notSame");
    	}
    	Address address = regReq.getAddress();
    	if (address == null) {
    		errors.rejectValue("address", "required");
    	} else {
    		errors.pushNestedPath("address");
    		try {
    			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address1", "required");
    			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address2", "required");
    		} finally {
    			errors.popNestedPath();
    		}
    	}
    	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "birthday", "required");
    }

}
```

*   소스 설명
    *   `supports()`
        * MemberRegistValidator는 MemberRegistRequest 타입의 객체를 지원하도록 구현
    *   `validate()`
        *   target을 MemberRegistRequest 객체로 변환

        *   값이 올바른지 검사해서 그 결과를 errors에 저장
            * regReq의 email 프로퍼티가 null이거나 값이 없으면 errors.rejectValue를 이용하여 에러 있음을 저장
            * regReq의 name 프로퍼티가 null이거나 값이 없으면 에러가 있음을 저장.(ValidationUtils 이용)
            * 중첩 프로퍼티에 대한 검사를 할 경우, errors에 중첩 프로퍼티 진입 지정.(**errors.pushNestedPath("address")**)
            * 중첩 프로퍼티에 대한 검사가 끝나면, error에 중첩 프로퍼티 끝을 지정(**errors.popNestedPath()**)

            ```java
            errors.rejectValue("email", "required");
            ```

            ​


* email 프로퍼티의 값이 잘못되었고, 에러 코드로 **"required"**를 사용함을 의미.

  ```java
  ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "required");
  ```

  ​


* target 객체의 name 프로퍼티 값이 null이거나 길이가 0인 경우 errors 객체에 name 프로퍼티의 에러코드로 required 등록.

  ```java
  @Controller

  @RequestMapping("/member/regist")

  public class RegistrationController {

      ...

      @RequestMapping(method = RequestMethod.POST)
      public String regist(
      		@ModelAttribute("memberInfo") MemberRegistRequest memRegReq,
      		BindingResult bindingResult) {
      	new MemberRegistValidator().validate(memRegReq, bindingResult);
      	if (bindingResult.hasErrors()) {
      		return MEMBER_REGISTRATION_FORM;
      	}
      	memberService.registNewMember(memRegReq);
      	return "member/registered";
      }
      
      public void setMemberService(MemberService memberService) {
      	this.memberService = memberService;
      }

  }
  ```



* 소스 설명
    * regist 메소드는
        * 커맨드 객체인 memRegReq 파라미터와
        * 에러 정보를 보관할 bindingResult 파라미터를 가지고 있음
        * Errors 파라미터나 BindingResult 파라미터는 ==**반드시 커맨드 객체 파라미터 바로 뒤에 위치**==
            * 아니면 Exception 발생
    * 커맨드 객체 검증
        * MemberRegistValidator 객체 생성
        * validate() 메소드 호출
        * 오류가 존재하면, ==**bindingResult.hasErrors()**== 메소드는 **true**를 리턴


### 5.2 Errors 와 BindingResult 인터페이스의 주요 메소드

*   org.spring.validation.Errors 인터페이스의 오류 등록 메소드
    * `reject(String errorCode)`
        * 전체 객체에 대한 글로벌 에러 코드 추가
    * `reject(String errorCode, String defaultMessage)`
        * 전체 객체에 대한 글로벌 에러 코드를 추가. 에러 코드에 대한 메시지가 존재하지 않을 경우 defaultMessage 사용
    * `reject(String errorCode, Object[] errorArgs, String defaultMessage)`
        * 전체 객체에 대한 글로벌 에러 코드를 추가. 메시지 인자로 errorArgs를 전달. 에러 코드에 대한 메시지가 존재하지 않을 경우 defaultMesage 사용
    * `rejectValue(String field, String errorCode)`
        * 필드에 대한 에러 코드를 추가
    * `rejectValue(String field, errorCode, String defaultMessage)`
        * 필드에 대한 에러 코드를 추가. 에러 코드에 대한 메시지가 존재하지 않을 경우, defaultMessage 사용
    * `rejectValue(String field, String errorCode, Object[] errorArgs, String defaultMessage)`
        * 필드에 대한 에러 코드를 추가. 메시지 인자로 errorArgs를 전달. 에러 코드에 대한 메시지가 존재하지 않을 경우, defaultNessage 사용

    ```java
    @RequestMapping(method = RequestMethod.POST)

    public String login(@Valid LoginCommand loginCommand, Errors errors,
                    	HttpServletRequest request) {
        if (errors.hasErrors()) {
            return LOGIN_FORM;
        }
      
        try {
            Auth auth = authenticator.authenticate(loginCommand.getEmail(),
                                                   loginCommand.getPassword());
            HttpSession session = request.getSession();
            session.setAttribute("auth", auth);
            return "redirect:/index.jsp";
        } catch (AuthenticationException ex) {
            errors.reject("invalidIdOrPassword");
            return LOGIN_FORM;
        }
    }
    ```

    ​


* 객체 자체에 문제가 있는 경우, `reject()` 메소드를 사용해서 글로벌 에러 정보 추가
    * errors.reject("invalidIdOrPassword");

* 객체의 개별 프로퍼티(필드)에 대한 에러 정보를 추가할 때 `rejectValue()` 메소드 사용
    * errors.rejectValue("email", "required")

* 글로벌 에러 정보나 특정 필드에 대한 에러 정보는 두번 이상 추가 가능
    * errors.rejectValue("id", "invalidLength")
    * errors.rejectValue("id", "invalidCharacter")

* org.spring.validation.Errors 인터페이스의 에러 발생 여부 확인 메소드
    * boolean hasErrors()
        * 에러가 존재하는 경우 true
        * ==**검증 에러 존재 여부 확인.**==
    * int getErrorCount()
        * 에러 개수를 리턴
    * boolean hasGlobalErrors()
        * reject() 메소드를 이용해서 추가된 글로벌 에러가 존재할 경우 true
    * int getGlobalErrorCount()
        * reject() 메소드를 이용해서 추가된 글로벌 에러 개수 리턴
    * boolean hasFieldErrors()
        * rejectValue() 메소드를 이용해서 추가된 에러가 존재할 경우 true
    * int getFieldErrorCount()
        * rejectValue() 메소드를 이용해서 추가된 에러 개수 리턴
    * boolean hasFieldErrors()
        * rejectValue() 메소드를 이용해서 추가된 에러가 존재할 경우 true
    * int getFieldErrorCount()
        * rejectValue() 메소드를 이용해서 추가된 에러 개수를 리턴
    * boolean hasFieldErrors(String field)
        * rejectValue() 메소드를 이용해서 추가한 특정 필드의 에러가 존재할 경우 true
    * int getFieldErrorCount(String field)
        * rejectValue() 메소드를 이용해서 추가한 특정 필드의 에러 개수 리턴


* org.spring.validation.BindingResult 인터페이스
    *  Error 인터페이스를 상속
    *  에러 메시지를 구하거나 검증 대상 객체를 구하는 추가 기능 정의




#### (1)  ValidationUtils 클래스를 이용한 값 검증

* org.spring.validation.ValidationUtils 클래스 제공하는 메소드
    * 코드를 간결하게 유지
    * 불필요한 실수를 줄임
    * 메소드 목록
        * rejectIfEmpty(Errors, String, String): void
        * rejectIfEmpty(Errors, String, String, Object[]): void
        * rejectIfEmpty(Errors, String, String, Object[], String): void
        * rejectIfEmpty(Errors, String, String, String): void
        * rejectIfEmptyOrWhitespace(Errors, String, String): void
        * rejectIfEmptyOrWhitespace(Errors, String, String, Object[]): void
        * rejectIfEmptyOrWhitespace(Errors, String, String, Object[], String): void
        * rejectIfEmptyOrWhitespace(Errors, String, String, String): void




### 5.3 에러 코드와 메시지

* 에러 코드 : 오류 내용을 알려줄때 사용
* 검증 과정에 추가된 에러 메시지를 사용하기 위해
    * 메시지를 읽어올 때 사용할 MessageSource를 스프링 설정에 등록
    * MessageSource에서 메시지를 가져올 때 사용할 프로퍼티 파일 작성
    * JSP와 같은 뷰코드에서 스프링이 제공하는 태그를 이용해서 에러 메시지 출력



####  MessageSource 등록

```xml
<bean id="messageSource"
    class="org.springframework.context.support.ResourceBundleMessageSource">
    <property name="basenames">
        <list>
            <value>message.error</value>
        </list>
    </property>
    <property name="defaultEncoding" value="UTF-8" />
</bean>
```



#### error.properties(error_언어.properties) 파일

```ini
required=필수 항목입니다.
required.email=이메일을 입력하세요.
minlength=최소 {1} 글자 이상 입력해야 합니다.
maxlength=최대 {1} 글자까지만 입력해야 합니다.
```
* minlegth, required 등은 메시지 코드가 됨.



#### 에러 메시지 코드 생성

* 글로벌 에러 코드인 경우, 아래 순서로 메시지 코드 생성
    1. 에러로드 + "." + 커맨드객체이름
    2. 에러코드

* Errors.rejectValue() 의 경우, 아래 순서로 메시지 코드 생성
    1. 에러코드 + "." + 커맨드객체이름 +"." + 필드명
    2. 에러코드 + "." + 필드명
    3. 에러코드 + "." + 필드타입
    4. 에러코드

* 필드가 List나 목록인 경우, 아래 순서로 메시지 코드 생성
    1. 에러코드 + "." + 커맨드객체이름 + "." + 필드명[인덱스].중첩필드명
    2. 에러코드 + "." + 커맨드객체이름 + "." 필드명.중첩필드명
    3. 에러코드 + "." + 필드명[인덱스].중첩필드명
    4. 에러코드 + "." + 필드명.중첩필드명
    5. 에러코드 + "." + 중첩필드명
    6. 에러코드 + "." + 필드타입
    7. 에러코드



#### 에러 메시지 출력

* 스프링 MVC는 JSP에서 에러 메시지를 출력할수 있는 커스텀 태그 제공

```xml
<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head><title>로그인</title></head>
<body>

<form:form commandName="loginCommand">
<form:errors element="div" />
<label for="email">이메일</label>: 
<input type="text" name="email" id="email" value="${loginCommand.email}">
<form:errors path="email"/> <br>

<label for="password">암호</label>: 
<input type="password" name="password" id="password">
<form:errors path="password"/> <br>
<br/>

<input type="submit" value="로그인">

</form:form>

<ul>
	<li>이메일/암호로 yuna@yuna.com/yuna 입력 또는 sanghwa@sanghwa.com/sanghwa 로 테스트</li>
</ul>
</body>
</html>
```
*   &lt;form:form&gt; + &lt;form:errors&gt; 사용하는 방식
*   &lt;form:form commandName="loginCommand"&gt;
    * loginCommand 커맨드 객체 사용함을 의미
*   &lt;form:errors&gt;
    * 커맨드 객체의 에러코드를 이용해서 에러 메시지 출력
    * 글로벌 에러 코드에 "invalidIdOrPassword"가 가 존재한다면
        * invalidIdOrPassword.loginCommand 메시지 확인
        * invalidIdOrPassword 메시지 확인
*   &lt;form&gt; 태그를 사용하고 싶다면
    * <spring:hasBindErrors> 와 <form:errors> 태그 사용

      ```html
      <%@ page contentType="text/html; charset=utf-8" %>
      <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
      <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
      <%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
      <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
      <!DOCTYPE html>
      <html>
      <head>
        <title>회원 가입</title>
      </head>
      <body>
        <spring:hasBindErrors name="memberInfo" />
        <form method="post">
          <label for="email">이메일</label>: 
          <input type="text" name="email" id="email" value="${memberInfo.email}"/>
          <form:errors path="memberInfo.email"/> <br/>
          
          <label for="name">이름</label>: 
          <input type="text" name="name" id="name" value="${memberInfo.name}" />
          <form:errors path="memberInfo.name"/> <br/>
          
          <label for="password">암호</label>: 
          <input type="password" name="password" id="password"
                 value="${memberInfo.password}"/>
          <form:errors path="memberInfo.password"/> <br/>
          
          <label for="password">확인</label>: 
          <input type="password" name="confirmPassword" id="confirmPassword"
                 value="${memberInfo.confirmPassword}"/>
          <form:errors path="memberInfo.confirmPassword"/> <br/>
          
          <label>주소</label>:
          주소1 
          <input type="text" name="address.address1" value="${memberInfo.address.address1}" />
          <form:errors path="memberInfo.address.address1"/> <br/>
          
          주소2
          <input type="text" name="address.address2" value="${memberInfo.address.address2}" />
          <form:errors path="memberInfo.address.address2"/> <br/>
          
          우편번호
          <input type="text" name="address.zipcode" value="${memberInfo.address.zipcode}" />
          <form:errors path="memberInfo.address.zipcode"/> <br/>
          <label>
              <input type="checkbox" name="allowNoti" 
                     value="true" ${memberInfo.allowNoti ? 'checked' : '' }/>
              이메일을 수신합니다.
          </label>
          <br/>
          
          <label for="birthday">생일</label>: 형식: YYYYMMDD, 예: 20140101
          <input type="text" name="birthday" id="birthday" 
                 value='<fmt:formatDate value="${memberInfo.birthday}" 
                        pattern="yyyyMMdd" />'/>
          <form:errors path="memberInfo.birthday"/> <br/>
          
          <input type="submit" value="가입" />
        </form>
      </body>
      </html>
      ```




### 5.4 @Valid 어노테이션과 @InitBinder 어노테이션을 이용한 검증 실행

* JSR303 표준에 정의된 @Valid 어노테이션을 이용하여 커맨드 객체 검사

  ```
  @Valid 어노테이션을 사용하려면 JSR303 API를 클래스패스에 추가 필요.
  compile 'javax.validation:validation-api:1.0.0.GA'
  ```

  ​



* 스프링 MVC는 JSR303의 ==@Valid 어노테이션==과 스프링 프레임워크의 ==@InitBinder 어노테이션==을 시용해서 스프링 프레임워크 유효성 검사 코드 실행

  ```java
  package chap07.auth;

  import javax.servlet.http.HttpServletRequest;
  import javax.servlet.http.HttpSession;
  import javax.validation.Valid;
  import org.springframework.stereotype.Controller;
  import org.springframework.validation.Errors;
  import org.springframework.web.bind.WebDataBinder;
  import org.springframework.web.bind.annotation.InitBinder;
  import org.springframework.web.bind.annotation.RequestMapping;
  import org.springframework.web.bind.annotation.RequestMethod;

  @Controller
  @RequestMapping("/auth/login")
  public class LoginController {
      private static final String LOGIN_FORM = "auth/loginForm";
      private Authenticator authenticator;
      
      @RequestMapping(method = RequestMethod.GET)
      public String loginForm(LoginCommand loginCommand) {
      	return LOGIN_FORM;
      }
      
      @RequestMapping(method = RequestMethod.POST)
      public String login(@Valid LoginCommand loginCommand, Errors errors,
      		HttpServletRequest request) {
      	if (errors.hasErrors()) {
      		return LOGIN_FORM;
      	}
      	try {
      		Auth auth = authenticator.authenticate(loginCommand.getEmail(),
                                                     loginCommand.getPassword());
      		HttpSession session = request.getSession();
      		session.setAttribute("auth", auth);
      		return "redirect:/index.jsp";
      	} catch (AuthenticationException ex) {
      		errors.reject("invalidIdOrPassword");
      		return LOGIN_FORM;
      	}
      }
      
      @InitBinder
      protected void initBinder(WebDataBinder binder) {
      	binder.setValidator(new LoginCommandValidator());
      }
      
      public void setAuthenticator(Authenticator authenticator) {
      	this.authenticator = authenticator;
      }

  }
  ```


* 소스 설명
    * 커맨드 객체 파라미터에 @Valid 어노테이션 적용
    * login() 메소드 내부에서 Validator 객체의 validate() 메소드를 명시적으로 호출 안함.
    * login() 메소드의 두번째 파라미터인 Errors를 이용해서 에러 체크
    * Validator 가 커맨드 객체를 검증할지의 여부는 `initBinder()` 메소드를 통해 결정
    * WebDataBinder.setvalidator()를 통해 커맨드 객체의 유효성 여부를 검사할 Validator를 설정
    * login() 메소드 실행전에 Validator객체를 이용해서 @Valid 어노테이션 붙은 커맨드 객체를 검증

### 5.5 글로벌 Validator와 컨트롤러 Validator 

*   글로벌 Validator를 사용하여 한개의 Validator를 이용 모든 커맨드 객체 검증
    * [XML설정] &lt;mvc:annotation-driven&gt; 태그의 validator속성에 글로벌 Validator 빈 등록
    ```xml
    <mvc:annotation-driven validator="validator" />
    <bean id="validator" class="custom.CommonValidator"/>
    ```
    *   글로벌 Validator가 커맨드 객체 검증을 지원하지 않으면(글로벌 Validator의 support()가 false 리턴), 글로벌 Validator는 커맨드 객체를 검증하지 않는다.
    *   글로벌 Validator를 사용하지 않고 다른 Validator를 사용하려면, 
        * @InitBinder가 적용된 메소드에서 WebDataBinder의 setValidator() 메소드 이용

          ```java
          @RequestMapping(method = RequestMethod.POST)
            public String login(@Valid LoginCommand loginCommand, Errors errors,
            HttpServletRequest request) {
              if (errors.hasErrors()) {
                  return LOGIN_FORM;
              }
              try {
                  Auth auth = authenticator.authenticate(loginCommand.getEmail(),
                  									loginCommand.getPassword());
                  HttpSession session = request.getSession();
                  session.setAttribute("auth", auth);
                  return "redirect:/index.jsp";
              } catch (AuthenticationException ex) {
                  errors.reject("invalidIdOrPassword");
                  return LOGIN_FORM;
              }
            }

          @InitBinder
          protected void initBinder(WebDataBinder binder) {
              binder.setValidator(new LoginCommandValidator());
          }
          ```

    *   글로벌 Validator와 함께 다른 Validator를 사용하려면,
        * @InitBinder가 적용된 메소드에서 WebDataBinder의 addValidator() 메소드 이용
    *   [JAVA 설정] @EnableWebMvc 어노테이션 사용했다면, ==**WebMvcConfigurerAdapter**==를 상속받은 @Configuration 클래스에서 **글로벌 Validator 객체를 생성하도록 getValidator() 메소드 재정의**
        ```java
                    @Configuration
                    @EnableWebMvc
                    public class SampleConfig extends WebMvcConfigurerAdapter {
                      @Override
                      public Validator getValidator() {
                          return new CommonValidator();
                      }
                    }
        ```

    *   뒷부분에 JSR303 지원 Validator 설명.


### 5.6 @Valid 어노테이션 및 JSR 303 어노테이션을 이용한 값 검증 처리

* Valid 어노테이션(Bean Validation API)
    * @NotNull
    * @Digits
    * @Size

* JSR 303이 제공하는 어노테이션을 이용하여 커맨드 객체의 값을 검증
    * 커맨드 객체에 @NotNull, @Digits등의 어노테이션을 이용하여 검증 규칙 설정
    * `LocalValidatorFactoryBean` 클래스를 이용해서 JSR 303 프로바이더를 스프링의 Validator 로 등록
    * 컨트롤러가 두번째에서 생성한 빈을 Validator 로 사용하도록 설정

#### 1. JSR 303 API와 JSR 303 프로바이더 설치

```gradle
compile 'javax.validation:validation-api:1.0.0.GA'
compile 'org.hibernate:hibernate-validator:4.3.1.Final'
```

* Hibernate Validator 를 사용할 경우, @Range나 @NotEmpty와 같은 추가 어노테이션 사용 가능

#### 2. 커맨드 객체에 검증 규칙 설정

```java
package chap07.member;

import javax.validation.Valid;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

public class MemberModRequest {

	@NotEmpty
	private String id;
	@NotEmpty
	private String name;
	@NotEmpty
	@Email
	private String email;
	private boolean allowNoti;
	@NotEmpty
	private String currentPassword;
	@Valid
	private Address address;
}
```

* Address에는 중첩 객체 검사를 위해 @Valid 어노테이션 사용


#### 3. JSR 303 프로바이더를 사용하도록 설정

* LocalValidatorFactoryBean 클래스를 빈으로 등록
* <mvc:annotation-driven> 태그를 사용하면 기본으로 `LocalValidatorFactoryBean`이 생성한 JSR 303 Validator를 글로벌 Validator로 등록


#### 4. JSR 303 어노테이션을 사용하는 커맨드 객체 앞에 @Valid 어노테이션을 추가

```java
@RequestMapping(method = RequestMethod.POST)
public String modify(@Valid @ModelAttribute("modReq") MemberModRequest modReq, Errors errors) {
    if (errors.hasErrors()) {
        return MEMBER_MODIFICATION_FORM;
    }
    try {
        memberService.modifyMemberInfo(modReq);
        return "member/modified";
    } catch (NotMatchPasswordException ex) {
        errors.rejectValue("currentPassword", "invalidPassword");
        return MEMBER_MODIFICATION_FORM;
    } catch (MemberNotFoundException ex) {
        return MEMBER_NOT_FOUND_VIEW;
    }
}
```

#### 5. 폼뷰 JSP 코드


```xml
<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
	<title>회원 정보 수정</title>
</head>
<body>
  <form:form commandName="modReq">
    <input type="hidden" name="id" value="${modReq.id}" />
    <label for="email">이메일</label>: 
    <input type="text" name="email" id="email" value="${modReq.email}"/> 
    <form:errors path="email"/><br/>

    <label for="name">이름</label>: 
    <input type="text" name="name" id="name" value="${modReq.name}"/> 
    <form:errors path="name"/><br/>

    <label for="address1">주소1</label>: 
    <input type="text" id="address1" name="address.address1" 
           value="${modReq.address.address1}" /> 
    <form:errors path="address.address1"/><br/>
    <label for="address2">주소2</label>:
    <input type="text" id="address2" name="address.address2" 
           value="${modReq.address.address2}" />
    <form:errors path="address.address2"/><br/>
    <label for="zipcode">우편번호</label>:
    <input type="text" id="zipcode" name="address.zipcode" 
           value="${modReq.address.zipcode}" /> <br/>

    <label>
        <input type="checkbox" name="allowNoti" 
               value="true" ${modReq.allowNoti ? 'checked' : ''} />
        이메일을 수신합니다.
    </label>
    <br/>
    <label for="currentPassword">현재 암호</label>: 
    <input type="password" name="currentPassword" id="currentPassword" /> 
    <form:errors path="currentPassword"/><br/>
    <input type="submit" value="수정" />
  </form:form>
</body>
</html>
```

* 오류 메시지는 JSR 303이 제공하는 기본 에러 메시지
    * 에러코드에 해당하는 메시지가 존재하지 않으면, JSR 303 기본 에러 메시지 출력
* 스프링 메시지를 이용하고 싶다면,
    * ==MessageSource가 사용하는 프로퍼티 파일에 다음 규칙을 따르는 메시지 코드 등록==
        1. 어노테이션이름.커맨드객체모델명.프로퍼티명
        2. 어노테이션이름.프로퍼티명
        3. 어노테이션이름


*   예제

    ```java
    public class MemberModRequest {
        @NotEmpty
        private String name;
    ```

    * NotEmpty.modReq.name
    * NotEmpty.name
    * NotEmpty




#### (1) JSR 303의 주요 어노테이션

* `javax.validation.constraints` 패키지에 정의

| 어노테이션                        | 주요 속성(괄호는 기본값)                           | 설명                                       |
| ---------------------------- | ---------------------------------------- | ---------------------------------------- |
| @NotNull                     |                                          | 값이 null이면 안됨                             |
| @Size                        | min: 최소 크기, max: 최대 크기                   | 값의 크기가 min에서 max사이 검사. <br> String의 경우 문자열 길이.  <br> 컬렉션인 경우 요소 개수 검사.  <br> 배열의 경우 배열 길이 검사.  <br>  ==값이 null이면 유효한 것으로 판단== |
| @Min <br> @Max               | value : 최소 또는 최대값                        | 숫자 값이 지정한 값 이상(@Min) 또는 이하(@Max) <br> BigDecimal, BigInteger, 정수 타입 및 래퍼 타입에 적용 <br> ==double, float는 지원하지 않음==. <br> ==값이 null이면 유효한 것으로 판단.== |
| @DecimalMin <br> @DecimalMax | value: 최소 또는 최대값, String                 | 숫자 값이 지정한 값 이상(@Min) 또는 이하(@Max) 인지 검사 <br> BigDecimal, BigInteger, 정수 타입 및 래퍼 타입에 적용 <br> ==값이 null이면 유효한 것으로 판단.== |
| @Digits                      | integer : 정수부분 숫자 길이 <br> fraction : 소수부분의 숫자 길이 | 숫자의 정수 부분과 소수점 부분의 길이가 범위에 있는지 검사, <br> BigDecimal, BigInteger, 정수 타입 및 래퍼 타입에 적용 <br> ==값이 null이면 유효한 것으로 판단.== |
| @Pattern                     | regexp : 정규 표현식                          | 문자열이 지정한 패턴에 일치하는지 검사. <br> 값이 null 인 경우 유효한 것으로 판단 |


* 필수 입력값 검사시에
    * ==**@NotNull과 @Size를 함께 사용**== 해야한다.


* `org.hibernate.validator.constraints`

| 어노테이션     | 주요 속성(괄호는 기본값)                           | 설명                                       |
| --------- | ---------------------------------------- | ---------------------------------------- |
| @NotEmpty |                                          | String인 경우 빈 문자열이 아님 <br> 컬렉션이나 배열인 경우 크기가 1이상 |
| @NotBlank |                                          | @NotEmpty 와 동일 <br> String의 경우 뒤 공백문자 무시 |
| @Length   | min: 최소 길이, int(0) <br> max: 최대 길이, int(int 최대 값) | 문자열의 길이가 min과 max 사이에 있는지 검사             |
| @Range    | min: 최소값, long(0) <br> max: 최대값, long(long의 최대 값) | 숫자 값이 min과 max 사이에 있는지 검사 <br> 값의 타입이 String인 경우 숫자로 변환한 결과 이용해서 검사 |
| @Email    |                                          | 이메일 주소 검사                                |
| @URL      |                                          | URL 주소 검사                                |



## 06. 요청 파라미터의 값 변환 처리

<pre>
1. WebDataBinder/@InitBinder와 PropertyEditor를 이용한 타입 변환
2. WebDataBinder와 ConversionService
   </pre>

### 6.1 WebDataBinder/@InitBinder와 PropertyEditor를 이용한 타입 변환

* WebDataBinder는 커맨드 객체의 값 검증 뿐만 아니라 웹 요청 파라미터로부터 커맨드 객체 생성시 사용
* WebDataBinder는 PropertyEditor와 ConversionService를 사용

* 컨트롤러 클래스마다 다른 변환 규칙 사용
    * 컨트롤러마다 개별적으로 PropertyEditor등록
    * WebDataBinder의 registerCustomEditor() 메소드 사용
    * @InitBinder 메소드에서 이 메소드를 이용해서 필요한 PropertyEditor를 등록


<pre><code class="java">
@Controller
@RequestMapping("/event")
public class EventController {
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		CustomDateEditor dateEditor = new CustomDateEditor(new SimpleDateFormat("yyyyMMdd"), true);
		binder.registerCustomEditor(Date.class, dateEditor);
	}
	
	// option 커맨드 객체의 from 프로퍼티가 Date 타입인 경우
	// yyyyMMdd 형식의 값을 갖는 from 요청 파라미터를 Date로 변환 처리.
	@RequestMapping("/list")
	public String list(SearchOption option, Model model) {
		...
	}

}
</code></pre>

*   프로퍼티마다 다른 커스텀 에디터를 사용하고 싶다면
    * 두번째 파라미터의 값이 true 면 요청 파리미터 값이 null이거나 "" 이면 null으로 할당
    * 두번째 파라미터의 값이 false 면 요청 파리미터 값이 null이거나 "" 이면 에러 코드로 **=="typeMismatch" 추가==**
      <pre><code class="java">
      @InitBinder
      protected void initBinder(WebDataBinder binder) {
        CustomDateEditor dateEditor1 = new CustomDateEditor(new SimpleDateFormat("yyyyMMdd"), true);
        binder.registerCustomEditor(Date.class, "from", dateEditor);

        CustomDateEditor dateEditor2 = new CustomDateEditor(new SimpleDateFormat("HH:mm"), true);
        binder.registerCustomEditor(Date.class, "reserveTime", dateEditor);

    }
    </code></pre>


## 6.2 WebDataBinder와 ConversionService

<pre>
1. @DateTimeFormat 어노테이션을 이용한 날짜/시간 변환
2. @DateTimeFormat 어노테이션의 속성과 설정 방법
3. @NumberFormat 어노테이션을 이용한 숫자 변환
   </pre>

* &lt;mvc:annotation-driven&gt; 설정

```xml
<bean id="formattingConversionService" 
    class="org.springframework.format.support.FormattingConversionServiceFactoryBean">
</bean>
<bean id="configurableWebBindingInitializer" 
    class="org.springframework.web.bind.support.ConfigurableWebBindingInitializer">
    <property name="conversionService" ref="formattingConversionService" />
    <property name="validator" ref="optionalValidatorFactoryBean" />
    <property name="messageCodesResolver"><null/></property>
</bean>
<bean id="requestMappingHandlerAdapter" 
        class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter">
    <property name="contentNegotiationManager" ref="mvcContentNegotiationManager" />
    <property name="webBindingInitializer" ref="configurableWebBindingInitializer" />
    <property name="messageConverters" ref="messageConverters" />
</bean>	
```

* ConfigurableWebBindingInitializer
    * WebDataBinder를 초기화
* RequestMappingHandlerAdapter
    * WebBindingInitializer를 이용해서 클라이언트 요청을 컨트롤러에 전달할 WebDataBinder 객체를 생성하고 초기화.
* ConfigurableWebBindingInitializer
    *  FormattingConversionServiceFactoryBean이 생성한 ConversionService를 설정함.
    *  WebDataBinder를 생성할 때 이 ConversionService 객체를 전달
    *  **==WebDataBinder는 PropertyEditor 뿐만 아니라 ConversionService를 이용해서 요청 파라미터를 알맞은 타입으로 변환==**
* &lt;mvc:annotation-driven&gt;(@EnableWebMvc)
    * **==DefaultFormattingConversionService 를 사용==**.
    * @DateTimeFormat 어노테이션, @NumberFormat 어노테이션 이용(5장 참고)

#### (1) @DateTimeFormat 어노테이션을 이용한 날짜/시간 변환

*   @DateTimeFormat 어노테이션은 요청 파라미터 => java.util.Date 타입, java.time.LocalDate 타입 변환
      <pre><code class="java">
      @DateTimeFormat(pattern="yyyyMMdd")
      public void setBirthday(Date birthday) {
        this.birthday = birthday;
      }
      </code></pre>

*   typeMismatch 메시지 추가.
                    <pre><code class="ini">
                    typeMismatch.birthday=날짜 형식이 올바르지 않습니다.
                    </code></pre>

*   Errors나 BindingResult 타입의 파라미터가 없으면
    * **==타입 변환 실패시 400 응답 에러 발생==**

#### (2) @DateTimeFormat 어노테이션의 속성과 설정 방법

* @DateTimeFormat 어노테이션은 날짜/시간 형식을 지정하기 위해 세 개의 속성 사용
    * 세 개의 속성은 함께 사용 불가, 한 개의 속성만 사용


![Spring7-6.jpg](.\Spring7-6.jpg "" "width:600px")


#### (3) @NumberFormat 어노테이션을 이용한 숫자 변환

* @NumberFormat 어노테이션
    * 특정 형식을 갖는 문자열을 숫자 타입으로 변환할 때 사용
    * 두 속성 중 한 속성만 사용해야 한다.

![Spring7-7.jpg](.\Spring7-7.jpg "" "width:600px")


### 6.3 글로벌 변환기 등록하기.

*   WebDataBinder/@InitBinder를 이용하는 방법
    * 단일 컨트롤러에만 적용

*   전체 컨트롤러에 동일한 변환 방식 적용 (5장 참고)
    * ConversionService를 직접 생성/등록
    * [XML 설정]FormattingConversionServiceFactoryBean을 사용
    ```xml
    <mvc:annotation-driven conversion-service="formattingConversionService" />

    <bean id="formattingConversionService"
    	class="org.springframework.format.support.FormattingConversionServiceFactoryBean">
    	<property name="formatters">
    		<set>
    			<bean class="chap07.common.MoneyFormatter" />
    		</set>
    	</property>
    </bean>
    ```
    * [JAVA 설정] WebMvcConfigurerAdapter 클래스의 addFormatters() 메소드 재정의
      <pre><code class="java">
      @Override
      public void addFormatters(FormatterRegistry registry) {
      registry.addFormatter(new MoneyFormatter());
      }
      </code></pre>

## 07 HTTP 세션 사용하기

* 트래픽이 작서나 단일 서버에서 동작하는 어플리케이션의 경우 서블릿의 HttpSession 을 이용해서 사용자 로그인 상태 유지하는 경우가 많음
* 스프링은 컨트롤러에서 HttpSession 을 처리하기 위한 기능 지원


### 7.1 HttpSession을 직접 사용하기

* 컨트롤러 메소드의 파라미터로 HttpSession 을 지정


```java
@RequestMapping(method = RequestMethod.POST)
public String login(@Valid LoginCommand loginCommand, Errors errors,
        HttpSession session) {
    if (errors.hasErrors()) {
        return LOGIN_FORM;
    }
    try {
        Auth auth = authenticator.authenticate(loginCommand.getEmail(), loginCommand.getPassword());
        session.setAttribute("auth", auth);
        return "redirect:/index.jsp";
    } catch (AuthenticationException ex) {
        errors.reject("invalidIdOrPassword");
        return LOGIN_FORM;
    }
}
```

* 소스 설명
    * HttpSession 타입의 파라미터가 존재하면, 스프링 MVC는 HttpSession을 생성해서 전달
        * 기존에 세션이 존재하면 그 세션을 전달
        * 존재하지 않으면, 새로운 세션을 생성해서 전달
    * ==상황에 따라 생성하고 싶다면, HttpSession 을 파라미터로 받으면 안되고, HttpServletRequest를 받아 직접 생성해야한다.==

```java
@RequestMapping(method = RequestMethod.POST)
public String login(@Valid LoginCommand loginCommand, Errors errors,
        HttpServletRequest request) {
    if (errors.hasErrors()) {
        return LOGIN_FORM;
    }
    try {
        Auth auth = authenticator.authenticate(loginCommand.getEmail(), loginCommand.getPassword());
        HttpSession session = request.getSession();
        session.setAttribute("auth", auth);
        return "redirect:/index.jsp";
    } catch (AuthenticationException ex) {
        errors.reject("invalidIdOrPassword");
        return LOGIN_FORM;
    }
}

```

### 7.2 @SessionAttributes 어노테이션을 이용한 모델과 세션 연동

* 여러 화면에 걸려서 진행되는 작업에 화면과 화면 사이에 데이터를 공유해야 할일 있다면.
    * 1단계 : 이벤트 기본 정보 입력(이름, 기간)
    * 2단계 : 이벤트 참가 가능 대상
    * 3단계 : 내용 확인


* 정보 수정을 위해 단계 이동이 가능해야하며, 저장된 내용이 유지 되어야한다.
    * 각 화면마다 공유할 데이터를 위한 hidden 사용
    * 임시 데이터를 DB에 보관
    * 세션에 임시 데이터 보관


* @SessionAttributes 어노테이션
    * 클래스에 @SessionAttributes 를 적용하고, 세션으로 공유할 객체의 모델 이름을 지정
    * 컨트롤러 메소드에서 객체를 모델에 추가
    * 공유한 모델의 사용이 끝나면 SessionStatus를 사용해서 세션에서 객체 제거

#### 방법 1 

```java
@Controller
@SessionAttributes("eventForm")
public class EventCreationController {
...
```

*   임시 목적으로 사용될 객체를 보관하기 위해
    * 세션 속성의 이름으로 "eventForm"을 사용
*   @SessionAttributes와 같은 이름의 모델 추가

*   첫번째 단계 : 첫번째 단계를 처리하는 컨트롤러 메소드
    * 모델에 객체 추가
      <pre><code class="java">
      @Controller
      @SessionAttributes("eventForm")    
      public class EventCreationController {

    @RequestMapping("/newevent/step1")
    public String step1(Model model) {
    	model.addAttribute("eventForm", new EventForm());
    	return EVENT_CREATION_STEP1;
    }

      @RequestMapping(value = "/newevent/step2", method = RequestMethod.POST)
      public String step2(@ModelAttribute("eventForm") EventForm formData, BindingResult result) {
          new EventFormStep1Validator().validate(formData, result);
          if (result.hasErrors())
              return EVENT_CREATION_STEP1;
          return EVENT_CREATION_STEP2;
      }
      </code></pre>

#### 방법 2

* @ModelAttribute가 적용된 메소드에서 모델 객체를 생성하면
    * @RequestMapping 메소드에서 Model 객체를 추가할 필요 없다.
    * @ModelAttribute가 붙은 메소드에서 매번 새로운 객체를 생성해야하지만
    * ==**@ModelAttribute를 적용된 메소드를 실행하기 전에 세션에 동이리 이름을 갖는 개체가 존재하면, 그 모델 객체를 사용함**==
        * 세션에 이름이 "eventForm"인 객체가 존재하면, formData() 메소드를 실행하지 않고, 세션의 객체를 사용.


* ==**세션을 이용한 객체 공유가 끝나면, SessionStatus 의 setComplete() 메소드를 호출**==
    * SessionStatus.setComplete() 메소드를 실행하면, 세션에서 객체를 제거.
    * 세션에서 제거할 뿐 모델에서는 제거하지 않는다.
    * 뷰코드에서 모델의 값을 사용 가능.

<pre><code class="java">
@Controller
@SessionAttributes("eventForm")
public class EventCreationController {
	@ModelAttribute("eventForm")
	public EventForm formData() {
		return new EventForm();
	}
	
	@RequestMapping("/newevent/step1")
	public String step1() {
		return EVENT_CREATION_STEP1;
	}
		
	@RequestMapping(value = "/newevent/step2", method = RequestMethod.POST)
	public String step2(@ModelAttribute("eventForm") EventForm formData, BindingResult result) {
		new EventFormStep1Validator().validate(formData, result);
		if (result.hasErrors())
			return EVENT_CREATION_STEP1;
		return EVENT_CREATION_STEP2;
	}
	
	RequestMapping(value = "/newevent/done", method = RequestMethod.POST)
	public String done(@ModelAttribute("eventForm") EventForm formData, SessionStatus sessionStatus) {
		sessionStatus.setComplete();
		return EVENT_CREATION_DONE;
	}
}
</code></pre>


## 08. 익셉션 처리

* 컨트롤러에서 익셉션이 발생하면, 에러 화면이 사용자에게 그대로 노출.

1. @ExceptionHandler 어노테이션을 이용한 익셉션 처리
2. @ControllerAdvice 어노테이션을 이용한 공통 익섹셥 처리
3. @ResponseStatus 어노테이션을 이용한 익셉션 처리

### 8.1 @ExceptionHandler를 이용한 익셉션 처리

* 익셉션을 처리할 메소드에 @ExceptionHandler 어노테이션을 지정, 처리할 익셉션 타입을 @ExceptionHandler 어노테이션 값으로 지정
    * HttpServletResponse, HttpServletRequest, HttpSession, Model 등 파라미터로 전달 받을수 있다.
    * ModelAndView 리턴 가능

```java
@Controller
public class CalculationController {
	@RequestMapping("/cal/divide")
	public String divide(Model model,
			@RequestParam("op1") int op1, @RequestParam("op2") int op2) {
		model.addAttribute("result", op1 / op2);
		return "cal/result";
	}

	@ExceptionHandler(ArithmeticException.class)
	public String handleException(HttpServletResponse response) {
		return "error/exception";
	}
}
```

* 소스 설명
    * @RequestMapping 메소드 실행 과정에 ArithmeticException이 발생하면, handlerException() 을 통해서 익셉션을 처리
        * 위의 경우 error/exception 뷰를 사용.


* 익셉션 타입을 지정하면, 해당 타입을 포함해 하위 타입까지 처리
    * @ErrorHandler(RuntimeException.class)
    * RuntimeException뿐만 아니라, 하위 타입인 ArithmeticException까지 처리


* @ExceptionHandler 메소드를 통해서 익셥센으로 처리하면 응답 코드가 **정상 처리를 뜻하는 200 이 된다**.
* 응답 코드 변경
  <pre><code class="java">
  @ExceptionHandler(RuntimeException.class)
  public String handleException(HttpServletResponse response) {
    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    return "error/exception";
  }
  </code></pre>


*   뷰 코드에서 @ExceptionHandler 메소드로 처리한 익셉션 객체를 사용할 수 있다.
    * exception 기본 객체 사용
    ```xml
    <%@ page contentType="text/html; charset=utf-8" %>
    <%@ page isErrorPage="true" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <!DOCTYPE html>
    <html>
    <head>
    <title>에러 발생</title>
    </head>
    <body>

    작업 처리 도중 문제가 발생했습니다.
    <%= exception %>

    </body>
    </html>
    ```

*   @ExceptionHandler 메소드에서 익셉션 객체에 접근하고 싶다면
    * 익셉션 타입의 파라미터를 메소드에 추가
    ```java
    @ExceptionHandler
    public String handleException(ArithmeticException exception) {
    	...
    	return "error/exception";
    }
    ```

> **@ExceptionHandler 처리는 누가 하나?**
> 스프링 MVC는 컨트롤러에서 익셉션이 발생하면, **HandlerExceptionResolver에 처리 위임**
> HandlerExceptionResolver 종류는 여러 가지 존재.
> &lt;mvc:annotation-driven&gt; 태그나 @EnableWebMvc 어노테이션 사용 할 경우** ExceptionHandlerExceptionResolver**
> ExceptionHandlerExceptionResolve : @ExceptionHandler 어노테이션이 적용된 메소드를 이용해서 익셉션 처리하는 기능 제공
>
> MVC 설정 사용할 경우
> 1. ExceptionHandlerExceptionResolve : 발생한 익셉션과 매칭되는 @ExceptionHandler 메소드를 이용해서 익셉션을 처리
> 2. DefaultHandlerExceptionResolve : 스프링이 발생시키는 익셉션에 대한 처리.( 요청 URL에 매핑되는 컨트롤러가 없는경우 -> NoHandlerFoundException -> 404 에러코드로 응답 전송)
> 3. ResponseStatusExceptionResolver : 익셉션 타입에 @ResponseStatus 어노테이션이 적용되어 있을 경우, 응답 코드 전송
>
>
> * DispatcherServlet은 익셉션이 발생하면, 
> * ExceptionHandlerExceptionResolver에 익셉션 처리 요청
> * @ExceptionHandler 메소드가 존재하지 않으면, 그 다음 차례인 DefaultHandlerExceptionResolve 사용.
> * 여기서 처리하지 않으면, 마지막으로 ResponseStatusExceptionResolver를 사용
> * 여기서 처리하지 않으면, 컨테이너가 익셉션을 처리

### 8.2 @ControllerAdvice를 이용한 공통 익셉션 처리

* @ExceptionHandler 어노테이션 : 해당 컨트롤러에서 발생한 익셉션만을 처리
* 다수의 컨트롤러에서 동일 타입의 익셉션을 발생시킬수 있고, 익셉션 처리 코드가 동일하다면?
    * @ControllerAdvice 어노테이션을 이용해서 **익셉션 처리 메소드 중복을 없앰**


```java
@ControllerAdvice("chap07")
public class CommonExceptionHandler {

	@ExceptionHandler(RuntimeException.class)
	public String handleException() {
		return "error/commonException";
	}
}
```

*   소스 설명
    * @ControllerAdvice 어노테이션이 적용된 클래스
        * 지정한 범위의 컨트롤러에서 공통으로 사용될 설정 지정 가능.
        * chap07 패키지 및 그 하위 패키지에 속한 컨트롤러 클래스를 위한 공통 기능 정의
    * @ControllerAdvice가 동작하려면 **해당 클래스를 스프링에 빈으로 등록해야만 함.**
    ```xml
    <bean class="chap07.exhandler.CommonExceptionHandler"/>
    ```

*   @ControllerAdvice 클래스에 있는 @ExceptionHandler와 컨트롤러 클래스에 있는 @ExceptionHandler 우선순위
    * 컨트롤러 클래스에 적용된 @ExceptionHandler
    1. 같은 컨트롤러에 위치한 **@ExceptionHandler 메소드중 해당 익셉션을 처리할 수 있는 메소드 검색**
    2. 같은 클래스에 위치한 메소드가 익셉션을 처리할 수 없는 경우, **@ControllerAdvice 클래스에 위치한 @ExceptionHandler메소드를 검색**    

*   @ControllerAdvice 어노테이션의 속성
| 속성                 | 타입                                   | 설명                           |
| ------------------ | ------------------------------------ | ---------------------------- |
| value basePackages | String[]                             | 공통 설정을 적용할 컨트롤러들이 속하는 기준 패키지 |
| annotations        | Class&lt? extends Annotation[]&gt;[] | 특정 어노테이션이 적용된 컨트롤러 대상        |
| assignableTypess   | Class&lt;?&gt;[]                     | 특정 타입 또는 그 하위 타입인 컨트롤러 대상    |

### 8.3 @ResponseStatus를 이용한 익셉션의 응답 코드 설정

* @ResponseStatus 어노테이션
    * 익셉션 자체에 에러 응답 코드를 설정하고 싶을때 사용


```java
@RequestMapping(value = "/files/{fileId:[a-zA-Z]\\d{3}}", method = RequestMethod.GET)
public String fileInfo(@PathVariable String fileId) throws NoFileInfoException {
    FileInfo fileInfo = getFileInfo(fileId);
    if (fileInfo == null) {
        throw new NoFileInfoException();
    }
    return "files/fileInfo";
}
```

* 위 처럼 NoFileInfoException 이 발생했을때 서버 에러를 뜻하는 500 에러가 아닌 404(존재하지 않음) 코드를 응답으로 전송

```java
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoFileInfoException extends Exception {

	private static final long serialVersionUID = 1L;

}
```


* org.spring.http.HttpStatus 열거 타입에서 주로 사용되는 값들
    * OK(200, "OK")
    * MOVED_PERMANENTLY(301, "Moved Permanently")
    * NOT_MODIFIED(304, "Not Modified")
    * TERMPORARY_REDIRECT(307, "Temporary Redirect")
    * BAD_REQUEST(400, "BNad Request")
    * UNAUTHORIZED(401, "Unauthorized")
    * PAYMENT_REQUIRED(402, "Payment Required")
    * FORBIDDEN(403, "Forbidden")
    * NOT_FOUND(404, "Not Found")
    * METHOD_NOT_ALLOWED(405, "Method Not Allowed")
    * NOT_ACCEPTABLE(406, "Not Acceptable")
    * UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type")
    * TOOO_MANY_REQUESTS(429, "Too Many Requests")
    * INTERNAL_SERVER_ERROR(500, "Internal Server Error")
    * NOT_IMPLEMENTED(501, "Not Implemented")
    * SERVICE_UNAVAILABLE(503, "Service Unavailable")

>  **서비스/도메인/영속성 영역의 익셉션 코드에서는 @ResponseStatus를 사용하지 말것**
>  @ResponseStatus 어노테이션은 그 자체가 HTTP 요청/응답 영역인 UI처리으 의미를 내포


## 09. 컨트롤러 메소드의 파라미터 타입과 리턴 타입

* **==@RequestMapping 어노테이션이 적용된 메소드에서 사용할 수 있는 파라미터 타입==**
| 파라미터                                     | 설명                                       |
| ---------------------------------------- | ---------------------------------------- |
| HttpServletRequest, HttpServletResponse  | 요청/응답 처리를 위한 서블릿 API                     |
| HttpSession                              | HTTP 세션을 위한 서블릿 API                      |
| o.s.ui.Model, o.s.ui.ModelMap, java.util.Map | 뷰에 데이터를 전달하기 위한 모델                       |
| @RequestParam                            | Http 요청 파라미터 값                           |
| @RequestHeader, @CookieValue             | 요청 헤더와 쿠키 값                              |
| @PathVariable                            | 경로 변수                                    |
| 커맨드 객체                                   | 요청 데이터를 저장할 객체                           |
| Errors, BindingResult                    | 검증 결과를 보관할 객체, **커맨드 객체 바로 뒤에 위치해야함**    |
| @RequestBody (파라미터에 적용)                  | 요청 몸체를 객체로 변환. 요청 몸체의 JSON이나 XML을 알맞게 객체로 변환 |
| Writer, OutputStream                     | 응답 결과를 직접 쓸때 사용할 출력 스트림                  |


* **==RequestMapping 어노테이션이 적용된 메소드에서 사용할수 있는 리턴 타입==**
| 리턴 타입        | 설명                                       |
| ------------ | ---------------------------------------- |
| String       | 뷰이름                                      |
| void         | 컨트롤러에서 응답을 직접 생성                         |
| ModelAndView | 모델과 뷰 정보를 함께 리턴                          |
| 객체           | 메소드에 @ResponseBody가 적용된 경우, 리턴 객체를 JSON이나 XML과 같은 알맞은 응답으로 변환 |


## 10. 스프링 MVC 설정

1. WebMvcConfigurer를 이용한 커스텀 설정
2. 뷰 전용 컨트롤러 설정하기
3. 디폴트 서블릿 설정과 동작 방식
4. 정적 자원 설정하기

### 10.1 WebMvcConfigurer 를 이용한 커스텀 설정

* @EnableWebMvc 어노테이션을 이용하는 경우,** o.s.web.servlet.config.annotation.WebMvcConfigurer 인터페이스를 상속받은 @Configuration 클래스**를 구현해야할 때가 있다.
* WebMvcConfigurer
    * MVC 네이스페이스를 이용한 설정과 동일한 설정을 하는데 필요한 메소드 정의
    * **o.s.web.servlet.config.annotation.WebMvcConfigurerAdapter를 상속받아 필요한 메소드만 구현하는 것인 일반적**

```java
//@EnableWebMvc 클래스와 WebMvcConfigurerAdpater 구현 클래스가 하나로 모인 경우
@Configuration
@EnableWebMvc
public class MvcConfiguration extends WebMvcConfigurerAdapter {
	@override
    public void addViewControllers(ViewControllerRegistry registry) {
    	registry.addViewController("/index").setViewName("index");
    }
}
```

```java
// @EnableWebMvc 클래스와 WebMvcConfigurer 구현 클래스가 서로 다를 경우
@Configuration
@EnableWebMvc
@Import(MvcConfiguration.class)
public class Config {
	...
}

@Configuration
public class MvcConfiguration extends WebMvcConfigurerAdapter {
	...
}
```


### 10.2 뷰 전용 컨트롤러 설정하기

* /index 경로로 요청이 들어오면 단순히 "index" 뷰를 보여주고 싶다면.

* [XML 설정]
```xml
<beans ..>
	<mvc:annotation-driven/>
    <mvc:default-servlet-handler/>
	<mvc:view-controller path="/index" view-name="index" />
    ...
</beans>
```
* path  속성의 값은 컨텍스트 경로르 제외한 나머지 경로.

* [Java 설정] : @EnableWebMvc, WebMvcConfigurerAdapter를 상속받은 경우
```java
@Configuration
@EnableWebMvc
public class MvcConfiguration extends WebMvcConfigurerAdapter {
	@Override
    public void addViewControllers(ViewControllerRegistry registry) {
    	registry.addViewController("/index").setViewName("index");
    }
}
```

### 10.3 디폴트 서블릿 설정과 동작 방식

*    web.xml 에서 DispatcherServlet의 경로 매핑을 "/" 로 했다면.
     ```xml
       <servlet>
           <servlet-name>dispatcher3</servlet-name>
           <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
           <init-param>
               <param-name>contextConfigLocation</param-name>
               <param-value>
                   /WEB-INF/sample.xml
               </param-value>
           </init-param>
           <load-on-startup>1</load-on-startup>
       </servlet>
       <servlet-mapping>
           <servlet-name>dispatcher3</servlet-name>
           <url-pattern>/</url-pattern>
       </servlet-mapping>
     ```
*    CSS,JS,HTML,JSP 등의 요청이 DispatcherServlet으로 전달됨 => 404 응답 에러
     * css,js,html,jsp 등의 요청은 WAS가 기본으로 제공하는 디폴트 서블릿이 처리하도록 되어있음.

*    default-servlet-handler
     ```xml
                               <beans...>
                               	<mvc:annotation-driven />
                                   <mvc:default-servlet-handler />
                               </beans>
     ```

     ```java
                               @Configuration
                               @EnableWebMvc
                               public class SampleConfig extends WebMvcConfigurerAdpater {
                               	@Ovveride
                                   public void configureDefaultServletHandling ( DefaultServletHandlerConfigurer configurer) {
                                   	configurer.enable();
                                   }
                               }
     ```



* 디폴트 서블릿 핸들러를 등록하면..
    1. 요청 경로와 일치하는 컨트롤러를 찾는다
    2. 컨트롤러가 존재하지 않으면, 디폴트 서블릿 핸들러에 전달
    3. 디폴트 서블릿 핸들러는 WAS의 디폴트 서블릿에 처리를 위임
    4. 디폴트 서블릿 처리 결과를 응답으로 전송




* 디폴트 서블릿 핸드러 설정에서 디폴트 서블릿의 이름을 지정하고 싶다면
    * 디폴트 서블릿의 이름은 WAS마다 다름
```xml
<mvc:default-servlet-handler default-servlet-name="default"/>
```
```java
@Configuration
@EnableWebMvc
public class SampleConfig extrends WebMvcConfigurerAdapter {
	@Override
    public void configureDefaultServletHandling(
    			DefaultServletHandlerConfigurer configurer) {
        configurer.enable("default");
    }
}
```

> 톰캣, Jetty, JBoss : default
> 웹로직 : FileServlet
> 구글 앱엔진 : _ah_default
> 웹스피어 : SimpleFileServlet



### 10.4 정적 자원 설정하기

*   css, js, 이미
    * 웹 브라우저에서 캐시를 통해 네트워크 사용량, 서버 사용량, 웹브라우저 반응속도 개선 가능
*   스프링 MVC &lt;mvc:resources&gt; 
    * 웹브라우저 캐시 사용 지정
    ```xml
    <mvc:resources mapping="/images/**" location="/images/, /WEB-INF/resources/"
    	cache-period="60" />
    ```
    ```java
    @Configuration
    @EnableWebMvc
    public class SampleConfig extends WebMvcConfigurerAdapter {
    	@Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
        	registry.addResourceHandler("/images**")
            		.addResourceLocations("/img/", "/WEB-INF/resources/")
                    .setCachePeriod(60);
        }
    }
    ```

*   &lt;mvc:resource&gt; 태그의 ㅅ혹성들
    * mapping : 요청 경로 패턴 설정. 컨텍스트 경로를 제외한 나머지 부분의 경로와 매핑
    * location : 웹 어플리케이션 내에서 요청 경로 패턴에 해당하는 자원의 위치. 여러개일 경우 **콤마**로 구분
    * cache-period : 웹브라우저에 캐시 시간 관련 응답 헤더 전송. 초 단위. 0의 경우 웹브라우저가 캐시하지 않음


## 11. HandlerInterceptor 를 이용한 인터셉터 구현

* 요청 경로마다 접근 제어를 다르게 해야한다거나 사용자가 특정 URL을 요청할 때마다 접근 내역을 기록하고 싶다면?
    * 코드 중복 없이 **컨트롤러에 적용**하는 방법
    * AOP? 너무 범용적인 방법.
    * HandlerInterceptor



### 11.1 HandlerInterceptor 인터페이스 구현하기

* o.s.web.servlet.HandlerInterceptor 인터페이스 사용
    * 컨트롤러(핸들러) 실행전 : preHandler
    * 컨트롤러(핸들러) 실행후, 뷰 실행전 : postHandle
    * 뷰 실행 이후 : afterCompletion


* preHandle() 
    * 컨트롤러/핸들러 객체를 실행하기 전에 필요한 기능 구현.
    * handler 파라미터는 웹 요청을 처리할 컨트롤러/핸들러 객체.
    * false를 리턴하면 컨트롤러(또는 다음 HandlerInterceptor)를 실행하지 않음.
* postHandle()
    * 정상적으로 실행된 이후에 추가 기능 구현할 때 사용
    * 컨트롤러가 익셉션을 발생하면 실행되지 않음
* afterCompletion() 
    * 클라이언에 뷰를 전송한 뒤 실행
    * 컨트롤러를 실행하는 과정에서 익셉션이 발생하면, 네번째 파라미터로 전달. 익셉션이 없으면 null


* o.s.web.servlet.handler.HandlerINterceptorAdapter
    * HandlerInterceptor 구현
    * 각 메소드는 아무것도 하지 않음.


* 웹 요청 처리 시간을 측정하는 HandlerInterceptor
  ```java
    public class MeasuringInterceptor extends HandlerInterceptorAdapter {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            System.out.println("MI: preHandle()");
            request.setAttribute("mi.beginTime", System.currentTimeMillis());
            return true;
        }

        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
            System.out.println("MI: afterCompletion()");
            Long beginTime = (Long) request.getAttribute("mi.beginTime");
            long endTime = System.currentTimeMillis();
            System.out.println(request.getRequestURI() + " 실행 시간: " + (endTime - beginTime));
        }

        @Override
        public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
            System.out.println("MI: postHandle()");
        }

    }
  ```

### 11.2 HandlerInterceptor 설정하기

```xml
<mvc:interceptors>
    <mvc:interceptor>
        <mvc:mapping path="/event/**" />
        <mvc:mapping path="/folders/**" />
        <bean class="chap07.common.MeasuringInterceptor" />
    </mvc:interceptor>
</mvc:interceptors>
```
```java
@Configuration
@EnableWebMvc
public class SampleConfig extends WebMvcConfigurerAdapter {
@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(measuringInterceptor())
				.addPathPatterns("/event/**", "/folders/**");
	}
}
```


* &lt;mvc:mapping&gt; : 핸들러 인터셉터를 적용할 요청 경로 패턴 지정
* &lt;bean&gt; : 지정한 경로 패턴에 적용될 핸들러 인터셉터


### 11.3 HandlerInterceptor 의 실행 순서

* 다음 세 핸들러 인터셉터가 있다고 하면
    * MeasuringInterceptor : 실행 시간 측정
    * AuthInterceptor : 로그인 한 사용자만 접근 허용
    * CommonModelInterceptor : 여러 뷰에서 함께 사용될 모델 설정


*   AuthInterceptor
    ```java
      public class AuthInterceptor extends HandlerInterceptorAdapter {
          @Override
          public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
              System.out.println("AI: postHandle()");
          }

          @Override
          public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
              System.out.println("AI: afterCompletion()");
          }

          @Override
          public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
              System.out.println("AI: preHandle()");
              HttpSession session = request.getSession(false);
              if (session == null) {
                  response.sendError(HttpServletResponse.SC_FORBIDDEN);
                  return false;
              }
              if (session.getAttribute("auth") == null) {
                  response.sendError(HttpServletResponse.SC_FORBIDDEN);
                  return false;
              }
              return true;
          }
      }
    ```

*   CommonModelInterceptor
    ```java
                      public class CommonModelInterceptor extends HandlerInterceptorAdapter {
                          @Override
                          public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                              System.out.println("VM: preHandle()");
                              return true;
                          }

                          @Override
                          public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
                              System.out.println("VM: afterCompletion()");
                          }

                          @Override
                          public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
                              System.out.println("VM: postHandle()");
                              modelAndView.addObject("project", "Spring4");
                          }
                      }
    ```

*   이 세개의 핸들러 인터셉터를 다음과 같이 적용
    1. 먼저 /acl/로 시작하는 경로에 AuthInterceptor 적용
    2. 전체 경로에 대해 MeasuringInterceptor를 적용
    3. /acl, /header, /newevent로 시작하는 경로에 대해 CommonModelInterceptor를 적용
    ```xml
    <mvc:interceptors>
        <mvc:interceptor>
            <mvc:mapping path="/acl/**" />
            <bean class="chap07.common.AuthInterceptor" />
        </mvc:interceptor>
        <bean class="chap07.common.MeasuringInterceptor" />
        <mvc:interceptor>
            <mvc:mapping path="/acl/**" />
            <mvc:mapping path="/header/**" />
            <mvc:mapping path="/newevent/**" />
            <mvc:exclude-mapping path="/acl/modify" />
            <ref bean="commonModelInterceptor" />
        </mvc:interceptor>
    </mvc:interceptors>
    ```
    ```java
    @Configuration
    @EnableWebMvc
    public class SampleConfig extends WebMvcConfigurerAdapter {
        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(new AuthInterceptor()).addPathPatterns("/acl/**");
            registry.addInterceptor(measuringInterceptor()).addPathPatterns("/**");
            registry.addInterceptor(commonModelInterceptor())
                    .addPathPatterns("/acl/**", "/header/**", "/newevent/**");	
        }
    }
    ```
    * **==적용하고 싶은 순서대로 설정함==**
    * ex) /acl/list 요청을 한다면
        1. 핸들러 인터셉터 preHandle() 실행
            A. AuthInterceptor.preHandle()
            B. MeasuringInterceptor.preHandle()
            C. CommonModelInterceptor.preHandle()
        2. 컨트롤러 실행
        3. 핸들러 인터셉터 postHandle() 실행
            A. CommonModelInterceptor.postHandle()
            B. MeasuringInterceptor.postHandle()
            C. AuthInterceptor.postHandle()
        4. 핸들러 인터셉터 afterCompletion() 실행
            A. CommonModelInterceptor.afterCompletion()
            B. MeasuringInterceptor.afterCompletion()
            C. AuthInterceptor.afterCompletion()

*   ==preHandle() 메소드는 지정한 순서대로 실행==
*   ==postHandle, afterCompletion() 메소드는 지정한 역순으로 실행==
*   AuthInterceptor.preHandle() 이 false 리턴하면
    * 이후 위치의 모든 과정이 실행 안됨.
*   1.B의 preHandle() 이 false 리턴하면
    * AuthInterceptor의 afterCompletion()만 실행
*   1.C의 preHandle() 이 false 리턴하면
    * 먼저 실행된 두 핸들러 인터셉터의 afterCompletion 실행
*   preHandle()에서 false 리턴하면 컨트롤러는 실행되지 않기떄문에.
    * postHandle() 메소드는 모두 실행되지 않음.
    * **==postHandle() : 컨트롤러가 정상 실행된 후에 적용됨==**


*   특정 경로 패턴에 대해 핸들러 인터셉터를 적용하고 싶지 않다면!
    * &lt;mvc:exclude-mapping path=""/&gt; 태그 또는 excludePathPatterns() 메소드를 이용
    ```xml
    	<mvc:interceptors>
    	<mvc:interceptor>
    		<mvc:mapping path="/acl/**" />
    		<bean class="chap07.common.AuthInterceptor" />
    	</mvc:interceptor>
    	<bean class="chap07.common.MeasuringInterceptor" />
    	<mvc:interceptor>
    		<mvc:mapping path="/acl/**" />
    		<mvc:mapping path="/header/**" />
    		<mvc:mapping path="/newevent/**" />
    		<mvc:exclude-mapping path="/acl/modify" />
    		<ref bean="commonModelInterceptor" />
    	</mvc:interceptor>
    </mvc:interceptors>
    ```
    ```java
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    	registry.addInterceptor(new AuthInterceptor()).addPathPatterns("/acl/**");
    	registry.addInterceptor(measuringInterceptor()).addPathPatterns("/**");
    	registry.addInterceptor(commonModelInterceptor())
    			.addPathPatterns("/acl/**", "/header/**", "/newevent/**")
    			.excludePathPatterns("/acl/modify");
    }
    ```

    ​
## 12. WebApplicationContext 계층

* DispatcherServlet은 자체 서블릿이기때문에 **한 개 이상의 DispatcherServlet 설정 가능**

* ex) 웹페이지를 위한 DispatcherServlet과 REST 기반 웹서비스 연동 DispatcherServlet 설정 한다면

```xml
<servlet>
    <servlet-name>front</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    <init-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>
            /WEB-INF/front.xml
        </param-value>
    </init-param>
</servlet>

<servlet>
    <servlet-name>rest</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    <init-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>
            /WEB-INF/rest.xml
        </param-value>
    </init-param>
</servlet>

```

* 두 DispatcherServlet의 각각 별도의 WebApplicationContext를 생성
    * front.xml에서는 rest.xml에 설정된 빈 객체를 사용할 수 없음


* 일반적인 웹 어플리케이션에서 레이어 구성
  ![spring7-16.jpg](.\spring7-16.jpg "" "width:600px")


* DispatcherServlet이 공통 빈을 필요로 하는 경우
    * **ContextLoaderListener를 사용하여 공통으로 사용될 빈을 설정**

```xml
<context-param>
	<param-name>contextConfigLocation</param-name>
    <param-value>/WEB-INF/service.xml,/WEB-INF/persistence.xml</param-value>
</context-param>
<listener>
	<listener-class>
    	org.springframework.web.context.ContextLoaderListener
    </listener-class>
</listener>
<servlet>
    <servlet-name>front</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    <init-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>
            /WEB-INF/front.xml
        </param-value>
    </init-param>
</servlet>
<servlet>
    <servlet-name>rest</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    <init-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>
            /WEB-INF/rest.xml
        </param-value>
    </init-param>
</servlet>
```

![spring7-17.jpg](.\spring7-17.jpg "" "width:600px")

*   ContextLoaderListener는 contextConfigLocation
    *  컨텍스트 파라미터를 명시하지 않으면 /WEB-INF/applicationContext.xml 을 설정 파일로 사용
    *  파일 위치
    *  classpath: 접두어 사용 설정 파일 명시
    ```xml
    <context-param>
    	<param-name>contextConfigLocation</param-name>
        <param-value>
        	classpath:config/service.xml
            classpath:common.xml
            /WEB-INF/config/message_conf.xml
        </param-value>
    </context-param>
    <listener>
        <listener-class>
            org.springframework.web.context.ContextLoaderListener
        </listener-class>
    </listener>
    ```


*   @Configuration 설정 클래스를 사용한다면
    * contextClass 컨텍스트 파라미터
    * WebApplicationContext 구현체로 AnnotationConfigWebApplicationContext를 지정
    * contextConfigLocation 컨텍스트 파라미터의 값을 사용할 자바 설정 클래스 지정
    ```xml
    <context-param>
    	<param-name>contextClass</param-name>
        <param-value>
        	org.springframework.web.context.support.AnnotationConfigWebApplicationContext
        </param-value>
    </context-param>
    <context-param>
    	<param-name>contextConfigLocation</param-name>
        <param-value>
        	net.madvirus.spring4.chap07.config.ApplicationConfig
        </param-value>
    </context-param>
    <listener>
        <listener-class>
            org.springframework.web.context.ContextLoaderListener
        </listener-class>
    </listener>
    ```


## 13. DelegatingFilterProxy를 이용한 서블릿 필터 등록

* 서블릿 필터에서 스프링 컨테이너에 등록된 빈을 사용하는 경우
    * WebApplicationContextUtils 클래스 사용
    * **서블릿 필터 자체를 스프링 컨테이너에 빈으로 등록하여, DI를 이용한 빈 사용 **(선호함)


* DelegatingFilterProxy 
    * 서블릿 필터를 스프링 빈으로 등록할 때 사용되는 클래스
    * 스프링 컨테이너에 빈으로 등록된 서블릿 필터에 필터 처리를 위임.

```xml
<filter>
    <filter-name>profileFilter</filter-name>
    <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
    <init-param>
        <param-name>targetBeanName</param-name>
        <param-value>webProfileBean</param-value>
    </init-param>
    <init-param>
        <param-name>contextAttribute</param-name>
        <param-value>
        	org.springframework.web.servlet.FrameworkServlet.CONTEXT.dispatcher
        </param-value>
    </init-param>
</filter>

<servlet>
	<servlet-name>dispatcher</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    ...
</servlet>
```
*  profileFilter는 그 요청을 targetBeanName 초기화 파라미터로 지정한 빈에 위임
*  targetBeanName 초기화 파라미터로 지정 안하면, **&lt;filter-name&gt; 에 지정한 이름을 빈 이름으로 사용** (profileFilter)



* DelegatingFilterProxy 가 사용할 빈 객체
    * DispatcherServlet이 생성한 WebApplicationContext
    * ContextLoaderListener이 생성한 루트 WebApplicationContext
* DispatcherServlet이 생성한 스프링 컨테이너에 필터로 사용할 빈 객체가 존재한다면
    * contextAttibute 초기화 파라미터를 이용해서 DispatcherServlet이 컨테이너를 보관할 때 사용하는 속성 이름 지정
    * **==org.springframework.web.servlet.FrameworkServlet.CONTEXT.[서블릿이름]==**
    * contextAttibute 초기화 파라미터를 지정하지 않으면, **"루트 WebApplicationContext"에 등록된 빈을 사용**


*   DelegatingFilterProxy는 기본적으로 Filter.init() 메소드와 Filter.destroy() 메소드에 대한 호출은 위임하지 않는다.
    * 스프링 컨테이너가 라이프 사이클을 관리하기 때문
    * 위임하고 싶다면. targetFilterLifecycle 초기화 파라미터의 값을 true로 지정.
    ```xml
    <filter>
        <filter-name>profileFilter</filter-name>
        <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
        <init-param>
            <param-name>targetBeanName</param-name>
            <param-value>webProfileBean</param-value>
        </init-param>
        <init-param>
            <param-name>contextAttribute</param-name>
            <param-value>
                org.springframework.web.servlet.FrameworkServlet.CONTEXT.dispatcher
            </param-value>
        </init-param>
        <init-param>
            <param-name>targetFilterLifecycle</param-name>
            <param-value>true</param-value>
        </init-param>
    </filter>
    ```


## 14. 핸들러, HandlerMapping, HandlerAdapter


![spring7-4.jpg](.\spring7-4.jpg "" "width:600px")

* DispatcherServlet 가 컨트롤러를 직접 실행하지 않고, HandlerAdapter를 통해서 컨트롤러 실행
    * HandlerAdapter != ControllerAdapter
    * HanderMapping != ControllerMapping
* DispatcherServlet은 웹 요청을 실제로 처리하는 객체의 타입을 @Controller로 어노테이션 구현 클래스로 제한하지 않음
    * 범용적인 의미로 핸들러(Handler)라고 부름
* DispatcherServlet은 핸들러 객체 실제 타입 상관하지 않음
    * 요청 처리 결과로 ModelAndView만 리턴하면 됨.
    * 하지만 모든 핸들러 객체가 ModelAndView 객체 리턴하지 않음.
* HandlerAdapter 
    * 결과를 DispatcherServlet이 요구하는 ModelAndView로 변환


### 14.1 HandlerMapping 우선 순위
* MVC 설정하면 최소 두개 시앙의 HandlerMapping이 등록
    * 우선 순위들이 존재
    * **==우선순위 높은 HandlerMapping에서 요청 처리할 핸들러 객체를 리턴하면, 이 핸들러 객체를 이용==**
    * null을 리턴하면 그 다음 우선 순위 갖는 HandlerMapping을 이용
    * 마지막까지 null을 리턴하면 DispatcherServlet은 404 에러코드

### 14.2 MVC 설정에서의 HandlerMapping과 HandlerAdapter
* &lt;mvc:annotation-driven&gt; 설정, @EnableWebMvc 어노테이션 사용할때 등록되는 HandlerMapping과 HandlerAdapter
| 빈 클래스                          | 설명                                       |
| ------------------------------ | ---------------------------------------- |
| RequestMappingHandlerMapping   | @Controller 적용 빈 객체를 핸들러로 사용하는 HandlerMapping 구현. 적용 우선 순위 높음 |
| SimpleUriHandlerMapping        | &lt;mvc:default-servlet-handler/&gt;, &lt;mvc:view-controller&gt;, &lt;mvc:resources&gt; 태그를 사용할 때 등록되는 HandlerMapping 구현. URL과 핸들러 객체를 매핑. 적용  우선순위 낮음 |
| RequestMappingHandlerAdapter   | @Controller 적용 빈 객체에 대한 어댑터              |
| HttpRequestHandlerAdapter      | HttpRequestHandler 타입의 객체에 대한 어댑터        |
| SimpleControllerHandlerAdapter | Controller 인터페이스를 구현한 객체에 대한 어댑터         |


*   RequestMappingHandlerMapping/Adapter 를 이용해서 @Controller 기반 컨트롤러 객체를 핸들러로 사용.

*   &lt;mvc:default-servlet-handler /&gt;
    * HttpRequestHandler 인터페이스를 구현한 DefaultServletHttpRequestHandler와 SimpleUrlHandlerMapping 클래스를 빈으로 등록
    ```xml
    <bean id="defaultServletHttpRequestHandler"
    	class="org.springframework.web.servlet.resource.DefaultServletHttpRequestHandler">
    </bean>
    <bean class="org.springframework.web.servlet.handler.SimpleUrlHandlerMapping">
    	<property name="urlMap">
        	<map>
            	<entry key="/**" value-ref="defaultServletHttpRequestHandler" />
            </map>
        </property>
    </bean>
    ```


* &lt;mvc:annotation-driven&gt; 태그가 등록하는 RequestMappingHandlerMapping의 우선순위 > &lt;mvc:default-servlet-handler&gt; 태그가 등록하는 SimpleUrlHandlerMapping 우선순위
    * RequestMappingHandlerMapping 먼저 확인 후 SimpleUriHandlerMapping을 확인
* &lt;mvc:default-servlet-handler &gt; 설정이 등록하는 SimpleUrlHandlerMapping은 우선 순위가 낮음
    *  @Controller 클래스에 매핑이 되어 있지 않거나, &lt;mvc:view-controller&gt;,  &lt;mvc:resources&gt; 등에 매핑되어 있지 않은 요청 경로는 
    *  최종적으로 defaultServletHttpRequestHandler가 처리




