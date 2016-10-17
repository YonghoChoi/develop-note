# 08. 스프링 MVC : 뷰구현

![spring8-1.jpg](.\spring8-1.jpg)


## 01. ViewResolver 설정

* 스프링 컨트롤러는 뷰에 의존적이지 않다.

'''java
@Controller
public class HelloController {
	@RequestMapping("/hello.do")
    public String hello() {
    	//처리 결과를 뷰 이름 지정.
    	return "hello";
    }
}
'''


* ViewResolver
    * 컨트롤러가 지정한 뷰 이름으로부터 응답 결과 화면 생성하는 View 객체를 구할때 사용
| ViewResolver 구현 클래스 | 설명 |
|--------|--------|
| InternalResourceViewResolver | 뷰 이름으로부터 JSP나 Tiles 연동을 위한 View 객체를 리턴       |
| VelocityViewResolver | 뷰 이름으로부터 Velocity 연동을 위한 View 객체 리턴 |
| VelocityLayoutViewRevoler | VelocityViewResolver와 동일한 기능 제공. 추가로 Velocity의 레이아웃 기능 제공|
| BeanNameViewResolver | 뷰이름과 동일한 이름을 갖는 빈 객체를 View 객체로 생성 |

### 1.1 ViewResolver 인터페이스

```java
public interface ViewResolver {
	View resolveViewName(String viewName, Locale locale) throws Exception;
}
```

* 소스 설명
    * 뷰 이름과 지역화를 위한 Locale 파라미터
    * 매핑되는 View 객체 리턴
    * 매핑되는 View 객체가 존재하지 않으면 null 리턴


### 1.2 View 객체

* 뷰 객체
    * 응답 결과를 생성하는 역할

```java
public interface View {
	String RESPONSE_STATUS_ATTRIBUTE = View.class.getName() + ".responseStatus";
	String PATH_VARIABLES = View.class.getName() + ".pathVariables";
	String SELECTED_CONTENT_TYPE = View.class.getName() + ".selectedContentType";
    
	String getContentType();
	void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception;
}
```
* 소스 설명
    * getContentType() 메소드
        * text/html 와 같은 응답 결과의 컨텐트 타입 리턴
    * render() 메소드
        * 실제로 응답 결과 생성
        * 첫번째 파라미터로 컨트롤러가 생성한 **모델 데이터**

### 1.3 InternalResourceViewResolver 설정

* InternalResourceViewResolver 클래스
    * InternalResourceView 타입의 뷰 객체 리턴
    * JSP나 HTML 파일과 같이 **웹 어플리케이션 내부 자원을 이용**해서 응답 결과 생성
    * JSTL이 존재할 경우 JstlView 객체 리턴(JstlView 클래스는 스프링의 지역화 관련 설정이 JSTL 커스텀 태그에 적용됨)


* InternalResourceViewResolver 설정
```xml
<bean
    class="org.springframework.web.servlet.view.InternalResourceViewResolver">
    <property name="prefix" value="/WEB-INF/view/"/>
    <property name="suffix" value=".jsp"/>
</bean>
```
    * 컨트롤러가 지정한 뷰 이름으로부터 실제로 사용될 뷰 선택
    * 뷰 이름 앞뒤로 prefix, suffix 프로퍼티의 값이 실제 사용될 자원의 경로


### 1.4 BeanNameViewResolver 설정

* BeanNameViewResolver 클래스
    * **뷰 이름과 동일한 이름을 갖는 빈을 뷰로 사용**
    * 커스텀 View클래스를 뷰로 사용해야 할 때 이용

* 예제 : 파일 다운로드를 위한 정보를 읽어 뷰에 전달

```java
@Controller
public class DownloadController implements ApplicationContextAware {

	@RequestMapping("/download.do")
	public ModelAndView download(HttpServletRequest request, HttpServletResponse response) {
    	File downloadFile = getFile(request);
        return new ModelAndView("download", "downloadFile", downloadFile)
    }

	...
}
```

```xml
<bean id="viewResolver" class="org.springframework.web.servlet.view.BeanNameResolver"/>
<bean id="download" class="net.madvirus.spring4.charp08.view.DownloadView" />
```
  * DownloadView 클래스를 "dowload" 이름으로 빈에 등록
  * ViewResolver로 BeanNameViewResolver 클래스 사용

### 1.5 다수의 ViewResolver 설정하기

* DispatcherServlet은 두 개 이상의 ViewResolver를 가질수 있음.
* 적용 순서
    * ViewResolver 구현 클래스가 org.springframework.core.Ordered 인터페이스를 구현했다면, ==order 프로퍼티에 우선순위 값 지정==
    * ViewResolver 구현 클래스에 @Order 어노테이션이 있다면, ==@Order 어노테이션 값을 우선순위 값으로 사용==
* 우선순위 값이 작을수록 우선 순위가 높음. 지정하지 않으면 가장 낮은 우선순위 - Integer.MAX\_VALUE
* Ordered 인터페이스가 구현되지 않거나, @Order 어노테이션을 지정하지 않으면, 가장 낮은 우선 순위.

> 스프링 제공 모든 ViewResolver는 ==Ordered 인터페이스 상속받고 있음.== 
> 따라서 order프로퍼티로 우선순위 지정가능


* DispatcherServlet의 우선순위 결정
    * 우선 순위 높은(order 값이 작은) ViewResolver에게 뷰 이름에 해당하는 View 객체 요청
    * null을 리턴하면, 그 다음 우선순위를 갖는 ViewResolver에 View를 요청


* order 프로퍼티를 이용한 우선순위 지정한 설정의 예
```xml
<bean class="org.springrramework.web.servlet.view.BeanNameViewResolver" p:order="0"/>
<bean
	class="org.springframework.web.servlet.view.IntergealResourceViewResolver"
    p:prefix="/WEB-INF/viewjsp/"
    p:suffix=".jsp"
    p:order="1" />
```



* 우선순위 결정시 주의할 점
    * **==InternalResouceViewResolver는 마지막 우선순위를 갖도록 지정해야 한다.==**
    * InternalResourceViewResolver는 항상 뷰 이름에 매핑되는 View객체를 리턴한다.(null을 리턴하지 않는다)
        * 우선순위가 낮은 ViewResolver가 사용되지 않는다.


## 02. HTML 특수 문자 처리 방식 설정

* JSP를 뷰로 사용할 경우 커스텀 태그를 이용하여 메시지 출력 가능
```xml
<title><spring:message code="login.form.title"/></title>
```

* &lt;spring:message&gt; 커스텀 태그는 HTML escaping 처리됨
    * defaultHtmlEscape 컨텍스트 파라미터를 통해 설정 가능
    * 기본 값은 **ture**이다.
    ```xml
    <context-param>
    	<param-name>defaultHtmlEscape</param-name>
        <param-value>false</param-value>
    </context-param>
    ```


## 03. JSP를 이용한 뷰 구현

* JSP  뷰를 사용하려면, **==InternalResourceViewResolver==** 를 사용
```xml
<bean id="viewResolver" 
	class="org.springrramework.web.servlet.view.InternalResourceResolver">
    <property name="prefix" value="/WEB-INF/view/"/>
    <property name="suffix" value=".jsp"/>
</bean>
```

> **/WEB-INF 디렉토리의 하위 디렉토리에 JSP 파일 위치시키는 이유**
> * 클라이언트가 뷰를 위한 JSP에 직접 접근하는 것을 막기 위함
> * /WEB-INF 디렉톨리는 특수한 디렉토리로 웹 컨테이너는 클라이언트가 /WEB-INF 경로 직접 접근을 제한


* Spring은 view를 위한 커스텀 태그를 제공
    * 메시지 국제화를 위한 &lt;spring:message&gt; 커스텀 태그
    * 커맨드 객체와 HTML &lt;form;&gt; 태그 사이의 연동을 위한 커스텀 태그
    * 에러 메시지 출력을 위한 커스텀 태그

### 3.1 메시지 출력을 위한 설정

* 커스텀 태그를 이용한 메시지 출력을 하려면 ==MessageSource를 등록==해야함
```xml
<bean id="messageSource"
	class="org.springframework.context.support.ResourceBuindloeMessageSource">
    <property name="basenames">
    	<list>
        	<value>message.label</value>
            <value>message.error</value>
        </list>
    </property>
    <property name="defaultEncoding" value="UTF-8"/>
</bean>
```
    * message 패키지에 위치한 label\_[언어].properties, error\_[언어].properties 사용
    * src/main/resources 의 message 폴더에 위치


### 3.2 메시지 출력을 위한 &lt;spring:message&gt; 커스텀 태그

* &lt;spring:message&gt;
    * MessageSource로부터 메시지를 가져와 출력해주는 커스텀 태그

```xml
<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head><title><spring:message code="login.form.title"/></title></head>
<body>

<form:form commandName="loginCommand">
<form:hidden path="securityLevel"/>
<form:errors element="div" />
<p>
	<label for="email"><spring:message code="email" /></label>: 
	<input type="text" name="email" id="email" value="${loginCommand.email}">
	<form:errors path="email"/>
</p>
<p>
	<label for="password"><spring:message code="password" /></label>: 
	<input type="password" name="password" id="password">
	<form:errors path="password"/>
</p>
<p>
    <label for="loginType"><spring:message code="login.form.type" /></label>
    <form:select path="loginType" items="${loginTypes}" />
<input type="submit" value="<spring:message code="login.form.login" />">
</form:form>

<ul>
	<li><spring:message code="login.form.help" /></li>
</ul>
</body>
</html>
```
```bash
#label.properties
email=이메일
password=암호

login.form.title=로그인 폼
login.form.login=로그인
login.form.help=이메일/암호로 yuna@yuna.com/yuna 입력 테스트
login.form.type=로그인 유형

greeting={0} 회원님, {1}
```
* &lt;%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %&gt;
* &lt;spring:message code="email" /&gt;
* {숫자} 형식을 이용하여 변하는 부분 명시
    ```xml
    <spring:message code="greeting" arguments="${me},${greeting}"/>
    ```


* &lt;spring:message&gt; 태그는 주어진 코드에 해당 메시지가 존재하지 않으면, **==익셉션을 발생==**
    * 기본 메시지 (text속성)
    ```xml
    <spring:message code="no_code" text="코드가 없습니다."/>
    ```


* htmlEscape 속성
    * '&lt;'나 '&amp;'와 같이 HTML에서 특수하게 처리되는 문자기 포함되어 있을 경우 처리
    * 속성값을 지정하지 앟으면, **defaultHtmlEscape 컨텍스트 파라미터에서 지정한 값** 사용


* javaScriptEscape 속성
    * &lt;spring:message&gt; 태그가 생성한 문자열을 변수 값으로 사용하기 원할때
    * **true** : 작은 따옴표나 큰 따옴표와 같은 문자를 \' 나 \"와 같은 특수 문자로 치환
    ```xml
    <script type="text/javascript">
    var value = '<spring:message code="title" javaScriptEscape="true"/>'
    </script>
   	...
    <input type="submit"
    	value="<spring:message code="login.form.submit" javaScriptEscape="false"/>">
    ```


* 메시지를 바로 출력하지 않고 request나 session 의 속성을 저장 가능
    * var 속성 : 메시지를 저장할 변수 이름
    * scope 속성 : 메시지를 저장할 범위 지정. page(기본값), request, session, application
    ```xml
    <spring:message code="login.form.password" var="label" scope="request"/>
    ${label} :
    ```


### 3.3 스프링이 제공하는 폼 관련 커스텀 태그

* 스프링
    * 입력 폼 값을 커맨드 객체에 저장
    * 커맨드 객체의 값을 입력 폼에 출력해주는 JSP 커스텀 태그 제공
    ```xml
    <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
    ```

#### (1) &lt;form&gt; 태그를 위한 커스텀 태그 : &lt;form:form&gt;

* &lt;form:form&gt; 커스텀 태그는 &lt;form&gt; 태그를 생성할 때 사용
* method 속성
    * 기본값 : post
    * 전송 방식
* action 속성 
    * 기본값 : 현재 요청 URL
    * 폼 데이터 전송할 URL
* commandName : 
    * 기본값 : command
    * 커맨드 객체의 이름
* enctype :
    * 전송될 데이터의 인코딩 타입


#### (2) &lt;input&gt; 태그를 위한 커스텀 태그 : &lt;form:input&gt;, &lt;form:password&gt;, &lt;form:hidden&gt;

* &lt;input&gt; 태그와 관련된 기본 커스텀 태그
| 커스텀 태그 | 설명 |
|--------|--------|
| &lt;form:input&gt; | text타입의 &lt;input&gt; 태그 |
| &lt;form:password&gt; | password 타입의 &lt;input&gt; 태그 |
| &lt;form:hidden&gt; | hidden 타입의 &lt;input&gt; 태그 |


* path 속성
    * 바인딩 될 커맨드 객체의 프로퍼티 지정
    ```xml
    <form:form commandName="memberInfo">
    <p>
    	<form:label path="userId">회원 ID</form:label>
        <form:input path="userId"/>
        <form:errors path="userId"/>
    </p>
    </form:form>
    ```


#### (3) &lt;select&gt; 태그를 위한 커스텀 태그 : &lt;form:select&gt;,&lt;form:options&gt;,&lt;form:option&gt;

* &lt;select&gt; 태그와 관련된 커스텀 태그
| 커스텀 태그 | 설명 |
|--------|--------|
| &lt;form:select&gt; | &lt;select&gt;태그를 생성한다. &lt;option&gt; 태그를 생성하는 데 필요한 콜렉션을 전달받을수도 있다.        |
| &lt;form:options&gt; | 지정한 콜렉션 객체를 이용하여 &lt;option&gt; 태그를 생성한다. |
| &lt;form:option&gt; | 한 개의 &lt;option&gt; 태그를 생성한다. |

* &lt;select&gt; 태그는 선택 옵션 제공할 때 사용
* 보통 @ModelAttribute 어노테이션을 이용해서 &lt;select&gt;  태그에서 사용될 옵션 목록 전달
```java
@ModelAttribute("loginTypes")
protected List<String> referenceData() throws Exception {
	List<String> loginTyhpes = new ArrayList<String>();
    loginTypes.add("일반회원");
    loginTypes.add("기업회원");
    loginTypes.add("헤드헌터회원");
    return loginTypes;
}
```
* 이 경우 &lt;form:select&gt; 커스텀 태그를 사용하면 뷰에 전달할 객체를 이용하여 간단하게 &lt;select&gt;와 &lt;option&gt; 태그 생성 가능
```xml
<form:form commandName="login">
	<form:errors/>
	<p>
		<label for="login"><spring:message code="login.form.type"/></label>
		<form:select path="loginType" items="${loginTypes}"/>
	</p>
</form:form>
```

* &lt;form:options&gt; 태그를 사용해도 동일한 작업을 수행할수 있다.
    * &lt;form:options&gt; 태그는 &lt;option&gt; 태그처럼 ==**콜렉션에 포함되지 않는 값을 갖는 &lt;option&gt; 태그를 함께 추가할 때 사용**==
```xml
<form:select path="loginType"/>
	<option value="">---선택하세요--</option>
	<form:options items="${loginTypes}"/>
</form:select>
```

* &lt;form:option&gt; 커스텀 태그
	* &lt;option&gt; 태그를 직접 지정할 때 사용
	```xml
	<form:select path="loginType">
		<form:option value="일반회원"/>
		<form:option value="기업회원">기업</form:option>
		<form:option value="헤드헌터회원" label="헤드헌터"/>		
	</form:select>
	```
    
    * value속성
		* &lt;option&gt; 태그의 value 속성  값을 지정할
		* &lt;form:option&gt; 커스텀 태그의 몸체 내용을 입력하지 않으면, value 속성에 지정한 값이 텍스트로 사용
		* label 속성을 사용한 경우에는 label 속성에 명시한 값이 텍스트로 사용
    * 변환 코드
	```xml
	<select id="loginType" name="loginType">
		<option value="일반회원">일반회원</option>
		<option value="기업회원">기업</option>
		<option value="헤드헌터회원">헤드헌터</option>
	</select>
	```
        
    * **itemValue** 속성과 **itemLabel** 속성
        * option 태그를 생성하는데 사용하는 콜렉션 객체가 존재하는 경우
        * &lt;form:select&gt;, &lt;form:options&gt; 커스텀 태그에서 사용 가능.
    
    ```java
    public class Code {
    	private String code;
        private String label;
        
        //get, set 메소드        
    }
    ```
    ```xml
    <form:select path="jobCode">
    	<option value="">--- 선택하세요 ---</option>
        <form:options items="${jobCodes}" itemLabel="label" itemValue="code"/>
    </form:select>
    ```


#### (4) checkbox 타입 &lt;input&gt; 태그를 위한 커스텀 태그 : &lt;form:checkboxses&gt;, &lt;form:checkbox&gt;

| 커스텀 태그 | 설명 |
|--------|--------|
| &lt;form:checkboxes&gt; | 커맨드 객체의 특정 프로퍼티와 관련된 checkbox 타입의 &lt;input&gt; 태그 목록을 생성       |
| &lt;form:checkbox&gt; | 커맨드 객체의 특정 프로퍼티와 관련된 한 개의 checkbox 타입 &lt;input&gt; 태그를 생성 |

* 한 개 이상의 값을 커맨드 객체의 특정 프로퍼티에 저장하고 싶은 경우, 배열이나 List 타입을 사용하여 값을 저장
```java
public class MemberRegistRequest {
	private String[] favoriteOs;
    
    public String[] getFavoriteOs() {
    	return favoriteOs;
    }
    public void setFavoriteOs(String[] favoriteOs) {
    	this.favoriteOs = favoriteOs;
    }
    
    ...
}
```
```xml
<p>
	<form:label path="favoriteOs">선호 OS</form:label>
    <form:checkboxes items="${favoriteOsNames}" path="favoriteOs"/>
    <form:errors path="favorioteOs"/>
</p>
```
```xml
<span>
	<input id="favoriteOs1" name="favoriteOs" type="checkbox" value="윈도우XP"/>
    <label for="favoriteOs1">윈도우 XP</label>
</span>
<span>
	<input id="favoriteOs2" name="favoriteOs" type="checkbox" value="윈도우7"/>
    <label for="favoriteOs2">윈도우 7</label>
</span>
<input type="hidden" name="_favoriteOs" value="on"/>
```
* &lt;input&gt; 태그의 value 속성의 값과 체크박스 설명 텍스트와 같다.
    * 다르다면, itemValue, itemLabel 속성을 사용하여 설정한다.
    ```xml
    <p>
    	<form:label path="favoriteOs">선호 OS<form:label>
        <form:checkboxes items="${favoritesOsCodes}" path="favoriteOs"
        	itemValue="code" itemLabel="label"/>
        <for,:errors path="favoriteOs"/>
    <p>
    ```


* &lt;form:checkbox&gt;
    * 한개의 checkbox 타입의 &lt;input&gt; 태그를 생성할 때 사용.
```xml
<form:checkbox path="favoriteOs" value="WIN2000" label="윈도우2000"/>
<form:checkbox path="favoriteOs" value="WINXP" label="윈도우XP"/>
```


* &lt;form:checkbox&gt; 커스텀 태그
    * 바인딩 되는 값의 타입에 따라 처리 방식이 달라짐
    * (1) 바인딩 되는 타입이 **Boolean** : "true" => checked 속성 설정
    ```java
    public class MemberRegistRequest {
    	private boolean allowNoti;
        
        public boolean isAllowNoti() {
        	return allowNoti;
        }
        public void setAllowNoti(boolean allowNoti) {
        	this.allowNoti = allowNoti;
        }
        ...
    }
    ```
    ```xml
    <form:checkbox path="allowNoti" label="이메일을 수신합니다."/>
    ```
    ```xml
    <!-- alowNoti가 false인 경우 -->
    <input id="allowNoti1" name="allowNoti" type="checkbox" value="true"/>
    <label for="allowNoti1">이메일을 수신합니다.</label>
    <input type="hidden" name="_allowNoti" value="on"/>
    
    <!-- allowNoti가 true인 경우 -->
    <input id="allowNoti1" name="allowNoti" type="checkbox" value="true" checked="checked" />
    <label for="allowNoti1">이메일을 수신합니다.</label>
    <input type="hidden" name="_allowNoti" value="on"/>    
    ```    
    
	* (2) 바인딩 되는 타입이 **배열**이나 **Collection**인 경우
	```java
    public class MemberResgistRequest {
    	private String[] favoriteOs;
        
        public String[] getFavoriteOs() {
        	return favoriteOs;
        }
        public void setFavoriteOs(String[] favoriteOs) {
        	this.favoriteOs = favoriteOs;
        }
        ...
    }
    ```
    ```xml
    <form:checkbox path="favoriteOs" value="윈도우XP" label="윈도우XP"/>
    <form:checkbox path="favoriteOs" value="윈도우7" label="윈도우7"/>
    <form:checkbox path="favoriteOs" value="윈도우8" label="윈도우8"/>
    ```

	* (3) 임의 타입의 프로퍼티와 바인딩 되는 경우 : value 속성의 값과 프로퍼티의 값이 일치하는 경우 **checked 속성 설정**

#### (5) radio 타입 &lt;input&gt; 태그를 위한 커스텀 태그 : &lt;form:radiobuttons&gt;, &lt;form:radiobutton&gt;

| 커스텀 태그 | 설명 |
|--------|--------|
| &lt;form:radiobuttons&gt; | 커맨드 객체의 특정 프로퍼티와 관련된 radio 타입의 &lt;input&gt; 태그 목록 생성        |
| &lt;form:radiobutton&gt; | 커맨드 객체의 특정 프로퍼티와 관련된 한 개의 radio 타입 &lt;input&gt; 태그를 생성 |

* &lt;form:radiobuttons&gt;
    * items 속성을 이용하여 값으로 할 콜렉션 전달
    * path 속성을 이용하여 값을 바인딩 할 커맨드 객체의 프로퍼티 지정.

 ```xml
<p>
	<form:label path="tool">주로 사용하는 개발툴</form:label>
    <form:radiobuttons items="${tools}" path="tool"/>
</p>
 ```
 ```xml
 <span><input id="tool1" name="tool" type="radio" value="Eclipse"/>
 	<label for="tool1">Eclipse</label></span>
 <span><input id="tool2" name="tool" type="radio" value="Intellij"/>
 	<label for="tool2">Intellij</label></span>
 <span><input id="tool3" name="tool" type="radio" value="NetBeans"/>
 	<label for="tool3">NetBeans</label></span>
 ```

* &lt;form:button&gt;
    * 1개의 radio 타입 &lt;input&gt; 태그를 생성할 때 사용.
    * **value 속성**과 **label 속성**을 이용 값과 텍스트 설정


#### (6) &lt;textarea&gt; 태그를 위한 커스텀 태그 : &lt;form:textarea&gt;

* 사용 예
```xml
<p>
	<form:label path="etc">기타</form:label>
    <form:textarea path="etc" cols="20" rows="3"/>
</p>
```
```xml
<p>
	<label for="etc">기타</label>
    <textarea id="etc" name="etc" rows="3" cols="20"></textarea>
</p>
```


#### (7) CSS 및 HTML 태그와 관련된 공통 속성

* 입력폼 관련해서 제공하는 스프링 커스텀 태그는 HTML의 CSS 및 이벤트 관련 속성 제공

* CSS
    * cssClass : HTML의 class 속성 값
    * cssErrorClass : 폼 검증 에러가 발생했을 때 사용할 HTML의 class 속성 값
    * cssStyle : HTML의 style 속성 값

* HTML 태그
    * id, title, dir
    * disabled, tabindex
    * onfocus, onblur, onchange
    * onkeydown, onkeypress, onkeyup
    * onmousedown, onmousemove, onmouseup
    * onmouseout, onmouseover

* htmlEscape 속성
    * HTML 특수 문자를 변환
    * **defaultHtmlEscape 컨텍스트 파라미터 기본 **


### 3.4 값 포맷팅 처리

* 스프링의 폼 관련 커스텀 태그
    * MVC에 등록한 ==**PropertyEditor**==나 **==ConversionService==**를 이용해서 변환


* Controller 클래스에서 Date 타입을 위한 PropertyEditor 로 CustomDateEditor 를 등록했다면
```java
@Controller
@RequestMapping("/member/regist")
public class RegistrationController {
	@RequestMapping(method=RequestMethod.POST)
    public String regist(
    	@ModelAttribute("memberInfo") MemberRegistRequest memRegReq,
        BindingResult bindingResult) {
    	new MemberRegistValidator().validate(memRegReq, bindingResult);
        
        if(bindingResult.hasErrors()) {
        	return MEMBER_REGISTRATION_FORM;
        }
        
        memberService.registNewMember(memRegReq);
        return "member/registered";
    }
    
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
    	CustomDateEdtiro dateEditor = 
        	new CustomDateEdtiro(new SimpleDateFormat("yyyyMMdd"), true);
        binder.registerCustomEditor(Date.class, dateEditor);
    }
}
```
    * WebDateBinder에 등록한 PropertyEditor
        * 요청 파라미터의 값을 Date타입으로 변환하는 데 사용
        * 스프링 커스텀 태그에서 커맨드 객체의 Date 타입 프로퍼티 값을 문자열로 변환하는 데 사용

       

* &lt;mvc:annotation-driven&gt;이나 @EnableWebMvc를 이용해서 설정
    * **ConversionService로 DefaultFormattingConversionService를 사용**.
    * @DateTimeFormat 어노테이션을 사용하면, PropertyEditor를 등록하지 않아도 됨.
    ```java
    public class MemberRegistRequest {
    	//스프링 커스텀 태그에서 타입 변환할 때 사용
        @DateTimeFormat(pattern="yyyyMMdd")
        private Date birthday;
    	...
    }
    ```
    
* (1) 커스텀 포맷터 등록하기
    * WebDataBinder/@initBinder를 이용한 타입 변환을 위해 PropertyEditor를 등록하는 방법은 **단일 컨트롤러에만 적용**
    * 모든 JSP에 동일한 변환 방식을 적용하고 싶을때 : **ConversionService 직접 생성해서 Formatter로 등록**
        * **FormattingConversionServiceFactoryBean**
        * 5장 참고.
        
    ```xml
    <mvc:annotation-driven conversion-service="formattingConversionService" />
    
    <bean id="formattingConversionService"
    	class="org.springframework.format.support.FormattingConversionServiceFactoryBean">
        <property name="formatters">
        	<set>
            	<bean class="net.madvirus.spring4.chap08.MoneyFormatter"/>
            </set>
        </property>
    </bean>
    ```


### 3.5 스프링이 제공하는 에러 관련 커스텀 태그

* Validator에서 Errors를 이용해 에러 정보 저장
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
    * Errors와 BindingResult를 이용해서 에러 정보 추가
    * &lt;form:errors&gt; 커스텀 태그를 이용 에러 메시지 출력.(path 속성을 이용)

 ```xml
<form:form commandName="memberInfo">
<p>
    <label for="email">이메일</label>
    <form:input path="email"/>
    <form:errors path="email"/>
</p>
...
</form>
 ```
 	* "email" 프로퍼티와 관련된 모든 에러 메시지를 출력
 	* 에러 메시지 결정 : 7장 참고(에러 코드, 필드 이름, 커맨드 클래스 이름)


* 에러 메시지 생성시 두 개의 속성 사용
    * element : 각 에러 메시지를 출력할때 사용될 HTML 태그. 기본 값 span
    * delimeter : 각 에러 메시지를 구분할 때 사용될 HTML 태그. 기본 값 &lt;br/&gt;
    ```xml
    <form:errors path="userId" element="div" delimeter=""/>
    ```


#### 3.6 &lt;spring:htmlEscape&gt; 커스텀 태그와 htmlEscape 속성


* defaultHtmlEscape 
    * HTML 특수 문자를 엔티티 레퍼런스로 치환할 지의 여부 결정


* JSP 페이지 별 설정
	* `<spring:htmlEscape>` 커스텀 태그 사용
	```xml
    <spring:htmlEscape defaultHtmlEscape="true"/>
    ...
    <spring:message ../>
    <form:input ../>
    ```
    * &lt;spring:htmlScape&gt; 커스텀 태그 설정 이후
        * 스프링이 제공하는 커스텀 태그는 **defaultEscape 속성에서 지정한 값을 기본 값으로 사용**



#### 3.7 &lt;form:form&gt;의 RESTful 지원

```java
@Controller
public class ArticleController {

	@RequestMapping(value="/article/{id}", method=RequestMethod.GET)
	public String read(@PathVariable("id") Integer id, Model model) {
		...
		return "article/read";
	}
	
	@RequestMapping(value="/article/{id}", method=RequestMethod.DELETE)
	public String delete(@PathVariable("id") Integer id, Model model) {
		...
		return "article/delete";
	}
	
	@RequestMapping(value="/article/{id}", method=RequestMethod.PUT)
	public String modify(@PathVariable("id") Integer id, Model model) {
		...
		return "article/modify";
	}
	
	@RequestMapping(value="article", method=RequestMethod.POST) 
	public String write(Model model) {
		...
		return "article/write";
	}
}
```
 * 웹브라우저 GET, POST 만 지원
 * DELETE, PUT 방식 요청 전송 불가


 * 스프링 
     * 웹브라우저에서 PUT, DELETE 지원 가능하도록 지원
     * web.xml 파일에 ==HiddenHtmlMethodFilter== 적용
     * &lt;form:form&gt; 태그의 ==method 속성에 put 또는 delete 이용==

 ```xml
<web-app>
...
	<filter>
    	<filter-name>httpMethodFilter</filter-name>
        <filter-class>org.springframework.web.filter.HiddenHttpMethodFilter</filter-class>
    </filter>
    <filter-mapping>
    	<filter-name>httpMethodFilter</filter-name>
        <servlet-name>dispatcher</servlet-name>
    </filter-mapping>
...
</web-app>
```
 ```xml
<form:form method="delete">
...
</form:form>
```
 ```xml
<form id="article" action="/chap08/article/1" method="post">
<input type="hidden" name="_method" value="delete"/>
...
</form>
```

* hidden 타입의 &lt;input&gt; 태그 추가 생성
* HiddenHttpMethodFilter 는 요청 파라미터에 **_method** 파라미터가 존재할 경우
    * ==_method 파라미터의 값을 요청 방식으로 사용==


## 04. HTML 이외의 뷰 구현

* 파일 다운로드 기능
* 동적으로 엑셀이나 PDF파일 생성


### 4.1 파일 다운로드 구현을 위한 커스텀 View

```java
@Controller
public class DownloadController implements ApplicationContextAware {

	private WebApplicationContext context = null;

	@RequestMapping("/file/{fileId}")
	public ModelAndView download(@PathVariable String fileId, HttpServletResponse response) throws IOException {
		File downloadFile = getFile(fileId);
		if (downloadFile == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		return new ModelAndView("download", "downloadFile", downloadFile);
	}

	private File getFile(String fileId) {
		String baseDir = context.getServletContext().getRealPath(
				"/WEB-INF/files");
		if (fileId.equals("1"))
			return new File(baseDir, "객체지향JCO14회.zip");
		return null;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = (WebApplicationContext) applicationContext;
	}

}
```
  
* HTML 응답이 아닌 경우 그에 알맞은 전용 View 클래스 구현
* ==BeanNameViewResolver==를 이용해서 커스텀 뷰 클래스 사용하도록 설정
```xml
<bean class="org.springframework.web.servlet.view.BeanNameViewResolver" />
<bean id="download"
class="net.madvirus.spring4.chap08.file.DownloadView" />
```
```java
public class DownloadView extends AbstractView {

	public DownloadView() {
		setContentType("application/download; charset=utf-8");
	}

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		File file = (File) model.get("downloadFile");

		response.setContentType(getContentType());
		response.setContentLength((int) file.length());

		String userAgent = request.getHeader("User-Agent");
		boolean ie = userAgent.indexOf("MSIE") > -1;
		String fileName = null;
		if (ie) {
			fileName = URLEncoder.encode(file.getName(), "utf-8");
		} else {
			fileName = new String(file.getName().getBytes("utf-8"),
					"iso-8859-1");
		}
		response.setHeader("Content-Disposition", "attachment; filename=\""
				+ fileName + "\";");
		response.setHeader("Content-Transfer-Encoding", "binary");
		OutputStream out = response.getOutputStream();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			FileCopyUtils.copy(fis, out);
		} finally {
			if (fis != null)
				try {
					fis.close();
				} catch (IOException ex) {
				}
		}
		out.flush();
	}
}
```
  * 다운로드를 위해 "application/octet-steam" 과 같은 다운로드 타입 설정 필요
  * 다운로드 받는 파일 이름을 Content-Disposition 헤더의 값을 설정
  * 스프링 유틸리티 클래스 `FileCopyUtils` 를 이용 FileInputSteam => response OutputStream 출ㄹ력


### 4.2 AbstractExcelView 클래스를 이용한 엑셀 다운로드 구현

* 스프링의 엑셀 형식 뷰 데이터 생성 View 클래스
    * ==AbstractExcelView : POI API 이용하여 엑셀 응답 생성==
    * AbstractJExcelView : JExcel API를 이용하여 엑셀 응답 생성


* POI 를 이용한 AbstractExcelView
```groovy
compile 'org.apache.poi:poi:3.9'
```
```java
protected abstract void buildExcelDocument(
			Map<String, Object> model, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response)
			throws Exception;
```
    * AbstractExcelView를 상속 받은뒤 `buildExcelDocument` 메소드 재정의
 _ _ _    
    
 ```java
public class PageRankView extends AbstractExcelView {

	@SuppressWarnings("unchecked")
	@Override
	protected void buildExcelDocument(Map<String, Object> model,
			HSSFWorkbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition", "attachment; filename=\"pagerank.xls\";");

		HSSFSheet sheet = createFirstSheet(workbook);
		createColumnLabel(sheet);

		List<PageRank> pageRanks = (List<PageRank>) model.get("pageRankList");
		int rowNum = 1;
		for (PageRank rank : pageRanks) {
			createPageRankRow(sheet, rank, rowNum++);
		}
	}

	private HSSFSheet createFirstSheet(HSSFWorkbook workbook) {
		HSSFSheet sheet = workbook.createSheet();
		workbook.setSheetName(0, "페이지 순위");
		sheet.setColumnWidth(1, 256 * 20);
		return sheet;
	}

	private void createColumnLabel(HSSFSheet sheet) {
		HSSFRow firstRow = sheet.createRow(0);
		HSSFCell cell = firstRow.createCell(0);
		cell.setCellValue("순위");

		cell = firstRow.createCell(1);
		cell.setCellValue("페이지");
	}

	private void createPageRankRow(HSSFSheet sheet, PageRank rank,
			int rowNum) {
		HSSFRow row = sheet.createRow(rowNum);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(rank.getRank());

		cell = row.createCell(1);
		cell.setCellValue(rank.getPage());

	}
}
 ```
     * buildExcelDocument() 메소드의 파라미터 **HSSFWorkbook 객체를 이용**해서 엑셀 데이터 생성
 _ _ _
 
 ```xml
 <bean id="pageRank" class="net.madvirus.spring4.chap08.stat.PageRankView>
 </bean>
 
 <bean class="org.springframework.web.servlet.view.BeanNameViewResolver">
 	<property name="order" value="1"/>
 </bean>
 ```
     * "pageRank" 라는 이름으로 PageRankView 빈 설정(컨트롤러에서 사용)
 _ _ _

 ```java
@Controller
public class PageRankStatController {

	@RequestMapping("/pagestat/rank")
	public String pageRank(Model model) {
		List<PageRank> pageRanks = Arrays.asList(
				new PageRank(1, "/board/humor/1011"),
				new PageRank(2, "/board/notice/12"),
				new PageRank(3, "/board/phone/190")
				);
		model.addAttribute("pageRankList", pageRanks);
		return "pageRank";
	}
}
 ```
    * 뷰 이름을 "pageRank"를 리턴하여 PageRankView를 뷰로 사용

### 4.3 AbstractPdfView 클래스를 이용한 PDF 다운로드 구현

* 스프링은 **iText API** 를 이용해서 PDF 생성하는 `AbstractPdfView`클래스 제공
 
* dependency 추가
    * 사용하지 않는 암호화 관련 기능 제거.
    ```groovy
    compile 'com.lowagie:itext:2.1.7'

    configurations {
        all.collect { configuration ->
            configuration.exclude group: 'com.lowagie', module: 'bcmail-jdk14'
            configuration.exclude group: 'com.lowagie', module: 'bcprov-jdk14'
            configuration.exclude group: 'com.lowagie', module: 'bctsp-jdk14'
        }
    }
    ```
    
 ```java
protected abstract void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
			HttpServletRequest request, HttpServletResponse response) throws Exception;
 ```
     * AbstractPdfView 클래스 사용받은 이후에 위 메소드를 알맞게 재정의
     * com.lowagie.text.Document 클래스는 iText 제공 클래스
         * PDF 문서를 생성하는데 필요한 데이터 추가하여 PDF 문서 생성

 ```java
 public class PageReportView extends AbstractPdfView {
	private String fontPath = "c:\\windows\\Fonts\\malgun.ttf";

	@SuppressWarnings("unchecked")
	@Override
	protected void buildPdfDocument(Map<String, Object> model,
			Document document, PdfWriter writer, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List<PageRank> pageRanks = (List<PageRank>) model.get("pageRankList");
		Table table = new Table(2, pageRanks.size() + 1);
		table.setPadding(5);

		BaseFont bfKorean = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H,
				BaseFont.EMBEDDED);

		Font font = new Font(bfKorean);
		Cell cell = new Cell(new Paragraph("순위", font));
		cell.setHeader(true);
		table.addCell(cell);
		cell = new Cell(new Paragraph("페이지", font));
		table.addCell(cell);
		table.endHeaders();

		for (PageRank rank : pageRanks) {
			table.addCell(Integer.toString(rank.getRank()));
			table.addCell(rank.getPage());
		}
		document.add(table);
	}

	public void setFontPath(String fontPath) {
		this.fontPath = fontPath;
	}
} 
 ```
 _ _ _

 ```xml
<bean id="pageReport" class="net.madvirus.spring4.chap08.stat.PageReportView">
</bean>
 ```
  * Bean 등록
 _ _ _


## 05. Locale 처리

* &lt;spring:message&gt; 커스텀 태그는 웹 요청과 관련된 언어 정보(locale)를 이용해서 알맞은 언어의 메시지 출력
* **LocaleResolver** 를 이용해서 웹 요청과 관련된 Locale을 추출


### 5.1 LocaleResolver 인터페이스

* org.springframework.web.servlet.LocaleResolver
```java
public interface LocaleResolver {
	Locale resolveLocale(HttpServletRequest request);
	void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale);
}
```
    * resolveLocale() 
        * 요청과 관련된 Locale 리턴
    * DispatcherServlet 은 등록되어있는 LocaleResolver 의 resolveLocale() 메소드를 호출하여 Locale 구함
 _ _ _
 

### 5.2 LocaleResolver의 종류

* org.springframework.web.servlet.i18n 패키지
| 클래스 | 설명 |
|--------|--------|
| AcceptHeaderLocaleResolver | 웹 브라우저가 전송한 **Accept-Language** 헤더로부터 Locale을 선택. setLocale()메소드 지원하지 않음|
| CookieLocaleResolver | 쿠키를 이용해서 Locale 정보를 구함. setLocale() 메소드는 쿠키에 Locale 정보를 저장 |
| SessionLocaleResolver | 세션으로부터 Locale 정보를 구함. setLocale() 메소드는 세션에 Locale 정보 저장 |
| FixedLocaleResolver | 웹 요청에 상관없이 특정한 Locale로 설정. setLocale() 메소드를 지원하지 않음 |


* LocaleResolver를 직접 등록할 때 빈의 이름을 **"localeResolver"**로 등록


#### (1) AcceptHeaderLocaleResolver

* LocaleResolver 를 별도 설정하지 않으면 AccpetHeaderLocaleResolver 를 기본 사용
* **Accpet-Language 헤더**로 부터 Locale 정보 추출

#### (2) CookieLocaleResolver

* **쿠키**를 이용해서 Locale 정보 저장
* setLocale() 메소드 : Locale  정보를 담은 쿠키를 생성
* resolveLocale() 메소드 : 쿠키로부터 Locale 정보 가져와 Locale을 설정
* defaultLocale 프로퍼티 값을 기본 Locale로 사용
    * null 이면, Accept-Language 헤더로부터 Locale 정보 추출
* CookieLocaleResolver 쿠키 설정 관련 프로퍼티
| 프로퍼티 | 설명 |
|--------|--------|
| cookieName |  사용할 쿠키 이름      |
| cookieDomain | 쿠키 도메인 |
| cookiePath | 쿠키 경로. 기본값은 "/" |
| cookieMaxAge | 쿠키 유효 시간 |
| cookieSecure | 보안 쿠키 여부. 기본값 false |


#### (3) SessionLocaleResolver

* HttpSession 에 Locale 정보 저장
* setLocale() 메소드 : Locale 정보를 세션에 저장
* resolveLocale() 메소드 : 세션으로 부터 Locale을 가져와 웹 요청의 Locale 설정
* defaultLocale 프로퍼티 값을 기본 Locale로 사용
    * null 이면, Accept-Language 헤더로부터 Locale 정보 추출


#### (4) FixedLocaleResolver

* 웹 요청에 상관없이 defaultLocale 프로퍼티로 설정한 값을 웹 요청을 위한 Locale로 사용
* setLocale() 메소드를 지원하지 않음
    * UnsupportedOperationException 예외 발생


### 5.3 LocaleResolver 등록

* DispatcherServlet 은 **이름이 "localeResolver"인 빈**을 LocaleResolver 로 사용
```xml
<bean id="localeResolver"
		class="org.springframework.web.servlet.i18n.SessionLocaleResolver" />
```


### 5.4 LocaleResolver를 이용한 Locale 변경

```xml
<bean class="net.madvirus.spring4.chap08.locale.LocaleChangeController">
    <property name="localeResolver" ref="localeResolver" />
</bean>
<bean id="localeResolver"
    class="org.springframework.web.servlet.i18n.SessionLocaleResolver" />
```
```java
@Controller
public class LocaleChangeController {

	private LocaleResolver localeResolver;

	@RequestMapping("/changeLanguage")
	public String change(@RequestParam("lang") String language,
			HttpServletRequest request, HttpServletResponse response) {
		Locale locale = new Locale(language);
		localeResolver.setLocale(request, response, locale);
		return "redirect:/index.jsp";
	}

	public void setLocaleResolver(LocaleResolver localeResolver) {
		this.localeResolver = localeResolver;
	}

}
```
* LocaleResolver를 이용해서 Locale을 변경하면, 이후 요청에 대해서는 지정한 Locale을 이용하여 메시지 로딩
_ _ _


* RequestContextUtils 클래스를 웹 요청과 관련된 LocaleResolver 를 구할수 있음
```java
@Controller
public class LocaleChangeController2 {

	@RequestMapping("/changeLanguage2")
	public String change(@RequestParam("lang") String language,
			HttpServletRequest request, HttpServletResponse response) {
		Locale locale = new Locale(language);
		LocaleResolver localeResolver = RequestContextUtils
                .getLocaleResolver(request);

		localeResolver.setLocale(request, response, locale);
		return "redirect:/index.jsp";
	}
}
```

### 5.5 LocaleChangeInterceptor를 이용한 Locale 변경

* LocaleChageINterceptor 클래스를 사용하여 웹 요청 파라미터를 이용해서 쉽게 Locale 변경 가능

```xml
<mvc:interceptors>
    <bean class="org.springframework.web.servlet.i18n.LocaleChangeInterceptor">
        <property name="paramName" value="language" />
    </bean>
</mvc:interceptors>
```
* **paramName 프로퍼티는 Locale 언어를 변경할 때 사용될 요청 파라미터 이름 지정**
    * paramName 프로퍼티로 설정한 요청 파라미터가 존재할 경우, 파리미터 값을 이용 Locale 생성
    * LocaleResolver를 이용해서 Locale 변경

 ```xml
http://localhost:8080/spring4-chap08/auth/login?language=en
  ```

