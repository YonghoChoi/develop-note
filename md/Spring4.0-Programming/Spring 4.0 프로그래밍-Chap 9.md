# 09. 스프링MVC : XML/JSON, 파일 업로드, 웹소켓

## 01. XML/JSON 변환 처리

* 스프링 MVC는 XML/JSON 형식을 처리할 방법 제공
    * @RequestBody 어노테이션

### 1.1 @RequestBody/@ResponseBody와 HttpMessageConverter

* HTTP 프로토콜(헤더 + 몸체)
![spring9-1.jpg](.\spring9-1.jpg "" "width:600px")
    * 데이터 형식 : Content-Type
    * application/x-www-form/urlencoded : POST 방식으로 폼 데이터 전송시 사용

* @RequestBody 어노테이션
    * 요청 몸체와 관련
    * 요청 파라미터 문자열을 String 자바 객체로 변환 또는 JSON 요청 몸체를 자바 객체로 변환할 때
* @ResponseBody 어노테이션
    * 응답 몸체와 관련
    * 자바 객체를 응답 몸체로 변환
    * 자바 객체를 JSON 형식또는 XML 형식의 문자열로 변환할 때

_ _ _

```java
@Controller
@RequestMapping("/mc/simple")
public class SimpleConverterController {

	@RequestMapping(method = RequestMethod.GET)
	public String simpleForm() {
		return "mc/simple";
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public String simple(@RequestBody String body) {
		return body;
	}

}
```
* @RequestBody 어노테이션
    * POST 방식으로 전송된 HTTP Request 데이터를 String타입의 Body 파라미터로 전달
* @ResponseBody 어노테이션
    * 리턴값을 HTTP 응답 데이터로 사용
    * simple() return is String.


### 1.2 HttpMessageConverter를 이용한 변환 처리

* 스프링 MVC는 @RequestBody 어노테이션, @ResponseBody 어노테이션이 있으면, **HttpMessageConverter 를 이용**해 자바 객체와 HTTP 요청/응답 몸체 사이의 변환 처리
* 다양한 타입의 HttpMessageConverter 구현체 제공
* &lt;mvc:annotation-driven&gt;, @EnableWebMvc 어노테이션을 사용하면 다수의 HttpMessageConverter 구현 클래스 등록

* 스프링 MVC 기본 HttpMessageConverter
| 클래스 | 설명 |
|--------|--------|
| StringHttpMessageConverter | 요청 몸체를 문자열로 변환, 문자열을 응답 몸체로 변환 |
| Jaxb2RootElementHttpMessageConverter | XML 요청 몸체를 자바 객체로 변환, 자바 객체를 XML 응답 몸체로 변환. <br/> JAXB2 존재시에만 <br/> text/xml <br/> application/xml <br/> application/\*+xml |
| MappingJackson2HttpMessageConverter | JSON 요청 몸체를 자바 객체로 변환, 자바 객체를 JSON 응답 몸체로 변환. <br/> Jackson2 존재시에만 <br/> application/json <br/> application/\*+json |
| MappingJacksonHJttpMessageConverter | JSON 요청 몸체를 자바 객체로 변환, 자바 객체를 JSON응답 몸체로 변환, <br/> Jacson 존재시에만 <br/> application/json <br/> application/\*+json |
| ByteArrayHttpMessageConverter | 요청 몸체를  byte 배열로 변환, byte 배열을 응답 몸체로 변환 |
| ResourceHttpMessageConverter | 요청 몸체를 스프링의 Resource로 변환하거나 Resource를 응답 몸체로 변환 |
| SourceHttpMessageConverter | XML 요청 몸체를 XML Source로 변환, XML Source를 XML응답으로 변환 |
| AllEncompassingFormHttpMessageConverter |  폼 전송 형ㅅ힉의 요청 몸체를 `MultiValueMap` 으로 변환, `MultiValueMap`을 응답 몸체로 변환 <br/> application/x-www-form-urlencoded <br/> multipart/form-data <br/> multipart/form-data 형식의 몸체의 각 부분을 변환할 때에는 각각 해당하는 HttpMessageConverter를 사용 |

### 1.3 JAXB2를 이용한 XML 처리

* JAXB2 : 자바 객체와 XML 사이의 변환을 처리해주는 API
    * Jaxb2RootElementHttpMessageConverter : JAXB2 API 를 이용.  변환
    * Java 6 이후 기본 포함


* Jaxb2RootElementHttpMessageConverter
    * XML -> @XMLrootElement 객체 또는 @XmlType 객체로 읽기
    * @XmlRootElement 적용 객체 -> XML 로 쓰기


* MVC 설정 사용시 Jaxb2RootElementHttpMessageConverter 기본으로 등록됨

_ _ _

```java
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "message-list")
public class GuestMessageList {

	@XmlElement(name = "message")
	private List<GuestMessage> messages;

	public GuestMessageList() {
	}

	public GuestMessageList(List<GuestMessage> messages) {
		this.messages = messages;
	}

	public List<GuestMessage> getMessages() {
		return messages;
	}

}
```
```java
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "id", "message", "creationTime" })
public class GuestMessage {

	private Integer id;
	private String message;
	private Date creationTime;

	public GuestMessage() {
	}

	public GuestMessage(Integer id, String message, Date creationTime) {
		this.id = id;
		this.message = message;
		this.creationTime = creationTime;
	}

	public Integer getId() {
		return id;
	}

	public String getMessage() {
		return message;
	}

	public Date getCreationTime() {
		return creationTime;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

}
```
 * GuestMessageList class
     * "message-list" 인 @XMLRootElement 어노테이션 적용
         * &lt;message-list&gt; Root tag 인 XML 생성

* 컨트롤러
    *  메소드에 @ResponseBody 어노테이션 적용
    *  GuestMessageList 객체 리턴

 ```java
@Controller
public class GuestMessageController {

	@RequestMapping(value = "/guestmessage/list.xml")
	@ResponseBody
	public GuestMessageList listXml() {
		return getMessageList();
	}

	private GuestMessageList getMessageList() {
		List<GuestMessage> messages = Arrays.asList(
				new GuestMessage(1, "메시지", new Date()),
				new GuestMessage(2, "메시지2", new Date())
				);

		return new GuestMessageList(messages);
	}
}
 ```
 * @ResponseBody 어노테이션이 적용된 메소드는 JAXB2가 적용된 클래스
 * HttpMessageConverter 중 Jaxb2RootElementHttpMessageConverter를 사용
 

* XML 몸체를 JAXB2가 적용된 자바 객체로 변환하고 싶다면
    * 컨트롤러 메소드의 파라미터에 @RequestBody 어노테이션 적용

 ```java
@Controller
public class GuestMessageController {
	@RequestMapping(value = "/guestmessage/post.xml", method = RequestMethod.POST)
	@ResponseBody
	public GuestMessageList postXml(@RequestBody GuestMessageList messageList) {
		return messageList;
	}
    ...
}
 ```
 * HTTP 요청 몸체를 GetMessageList 로 변환할 때 
     * HTTP 요청 컨텐트 타입이 application/xml
     * GuestMessageList가 JAXB2 어노테이션 적용 클래스
     * Jaxb2RootElementHttpMessageConeverter 를 이용해서 요청 몸체를 자바 객체로 변환

_ _ _

* Jquery 를 이용하여 XML 컨텐트 타입을 이용해서 XML 요청 몸체 전송하는 예
```xml
<script type="text/javascript" src="js/jquery-1.11.0.min.js"></script>
<script>
    function postXml() {
        var xmlBody = 
            '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>'+
            '<message-list>'+
            '<message><id>1</id><message>메시지</message><creationTime>2014-03-16T13:22:16.767+09:00</creationTime></message><message><id>2</id><message>메시지2</message><creationTime>2014-03-16T13:22:16.767+09:00</creationTime></message>'+
            '</message-list>';
        $.ajax({
            type: "post",
            url: "guestmessage/post.xml",
            contentType: "text/xml",
            data: xmlBody,
            processData: false,
            success: function( response ){
                alert(response);
            },
            error: function(){
                alert( "ERROR", arguments );
            }
        });
    }
</script>
```

### 1.4 Jackson2를 이용한 JSON처리

* **Jackson2** 라이브러리
    * 자바 객체를 JSON으로 변환
    * JSON을 자바 객체로 변환
* 스프링 MVC의 **MappingJackson2HttpMessageConverter**
    * Jackson2를 이용해서 변환 처리

* 의존성 추가
```groovy
compile 'com.fasterxml.jackson.core:jackson-databind:2.3.3'
```

* 자바 객체를 JSON응답으로 변환
```java
@RequestMapping(value = "/guestmessage/list.json")
@ResponseBody
public GuestMessageList2 listJson() {
    return getMessageList2();
}
private GuestMessageList2 getMessageList2() {
    List<GuestMessage> messages = Arrays.asList(
            new GuestMessage(1, "메시지", new Date()),
            new GuestMessage(2, "메시지2", new Date())
            );

    return new GuestMessageList2(messages);
}
```

> Jackson2의 JSON과 자바 객체 사이의 변환 규칙
> ==http://wikifasterxml.com/JacksonDocumentation==


#### 커스텀 HttpMessageConverter 등록하기

* 직접 구현한 HttpMessageConverter 추가
```xml
<mvc:annotation-driven>
	<mvc:message-converters>
    	<bean class="x.y.CustomMessageConverter" />
    </mvc:message-converters>
</mvc:annotation-driven>
```
    * 기본 HttpMessageConverter들을 등록
    * 기본으로 추가 되는 HttpMessageConverter를 등록하고 싶지 않다면 register-defaults= false 추가
    ```xml
    <mvc:annotation-driven>
		<mvc:message-converters register-defaults="false">
    		<bean class="x.y.CustomMessageConverter" />
    	</mvc:message-converters>
	</mvc:annotation-driven>
    ```

* @EnableWebMvc 어노테이션 사용하는 경우
    * WebMvcConfigurerAdapter 클래스 상속받아 **configureMessageConverters()** 메소드 재정의
    ```java
    @Configuration
    @EnableWebMvc
    public class SampleConfig extends WebMvcConfigureAdapter {
    	@Override
        public void configureMessageConverters(
        	List<HttpMessageConverter<?>> converters) {
        	converters.add(new CustomMessageConverter());
        }
    }
    ```
    * 이 경우 기본으로 등록했던 **HttpMessageConverter를 등록하지 않는다**.
    * XML이나 JSON 변환처리를 위해서는 나머지 HttpMessageConverter를 등록해야함
    ```java
    @Configuration
    @EnableWebMvc
    public class SampleConfig extends WebMvcConfigureAdapter {
    	@Override
        public void configureMessageConverters(
        	List<HttpMessageConverter<?> converters) {
        	converters.add(new StringHttpMessageConverter());
            converters.add(new Jaxb2RootElementHttpMessageConverter());
            converters.add(new MappingJackson2HttpMessageConverter());
        }
    }
    ```
    
## 02. 파일 업로드

* 파일 업로드
    * HTML 폼의 enctype 속성을 `multipart/form-data` 로 지정
    
 ```xml
 <form method="post" enctype="multipart/form-data">
 	...
 </form>
 ```

* 스프링에서는 멀티파트 형식을 지원하고 있다.
    * 별도의 처리 없이 멀티파트 형식으로 **전송된 파라미터와 파일 정보를 구할수 있다**.


### 2.1 MultipartResolver 설정

* 멀티파트 지원 기능을 사용하려면 먼저 **MultipartResolver를 스프링 설정 파일에 등록해야 함**
* MultipartResolver
    * 멀티파티 형식 -> 스프링 MVC에서 사용할 수 있도록 변환.
    * @RequestParam 어노테이션 이용 파라미터값과 파일 데이터 사용 가능

* 스프링 제공 기본 MultipartResolver
    * org.springframework.web.multipart.coimmons.CommonsMultipartResolver
        * Commons FileUpload AP 를 이용해서 멀티파트 데이터 처리
    * org.springframework.support.StandardServletMultipartResolver
        * 서블릿 3.0의 Part릉 이용해서 멀티파트 데이터 처리


* 스프링 빈의 이름은 **==multipartResolver==** 이어야만 함
    * DispatcherServlet : 이름이 multipartResolver인 빈을 사용


#### (1) Commons FileUpload 이용


```groovy
compile 'commons-fileupload:commons-fileupload:1.3'
```

```xml
<bean id="multipartResolver"
	class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
</bean>
```

* CommonsMultipartResolver 클래스 프로퍼티
| 프로퍼티 | 타입 | 설명 |
|--------|--------|--------|
| maxUploadSize | long | 최대 업로드 가능한 바이트 크기. -1은 제한 없음. 기본값 -1 |
| maxInMemorySize | int | 디스크에 임시 파일을 생성하기 전에 메모리에 보관할수 있는 최대 바이트. 기본값 1024 바이트 |
| defaultEncoding | String | 요청을 파싱할때 사용할 캐릭터 인코등. 지정하지 않으면 HttpServletRequest.setCharacterEncoding() 메소드로 지정된 캐릭터 셋 사용. 아무값도 없으면 ISO-8859-1 |


#### (2) 서블릿 3의 파일 업로드 기능 사용 

* http://tomcat.apache.org/whichversion.html
![spring9-x.png](.\spring9-x.png "" "width:800px")


* 서블릿 3의 파일 업로드 기능을 사용
    * DispatcherServlet이 서블릿 3의 Multipart를 처리하도록 설정
    * `StandardServletMultipartResolver` 클래스를 **MultipartResolver 로 설정**

_ _ _

* 설정
    * DispatcherServlet 이 멀티파트 처리할 수 있도록 설정 : ==multipart-config== (web.xml)
	```xml
    <web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns="http://java.sun.com/xml/ns/javaee"
	xsi:schemaLocation="http://java.sun.com/xml/ns/javaee 
		http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd"
	id="spring4-chap09" version="3.0">
	<display-name>spring4-chap09</display-name>

	<servlet>
		<servlet-name>dispatcher2</servlet-name>
		<servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
		<init-param>
			<param-name>contextConfigLocation</param-name>
			<param-value>
				/WEB-INF/sample2.xml
			</param-value>
		</init-param>
		<load-on-startup>1</load-on-startup>
		<multipart-config>
			<max-file-size>-1</max-file-size>
			<max-request-size>-1</max-request-size>
			<file-size-threshold>1024</file-size-threshold>
		</multipart-config>
	</servlet>
    ```
	

* &lt;multipart-config&gt; 설정 관련 태그
| 태그 | 설명 |
|--------|--------|
| &lt;location&gt;| 업로드 한 파일이 임시로 저장될 위치 지정 |
| &lt;max-file-size&gt; | 업로드 가능한 파일의 최대 크기를 바이트 단위로 지정. -1 제한 없음. 기본값 -1 |
| &lt;file-size-threshold&gt; | 업로드 한 파일 크기가 이 태그값 보다 크면 location 지정 디렉토리에 임시 파일 생성. 태그 값 이하이면 메모리에 파일 데이터 보관. 바이트 단위. 기본값 0 |
| &lt;max-request-size&gt; | 전체 Multipart 요청 데이터의 최대 제한 크기를 바이트 단위로 지정. -1 제한 없음. 기본값 -1 |


* 스프링 설정에서 StandardServletMultipartResolver 를 빈으로 등록
```xml
<bean id="multipartResolver"
	class="org.springframework.web.multipart.support.StandardServletMultipartResolver" />
```
    * 파일 최대 크기나 임시 저장 디렉토리등은 &lt;multipart-cconfig&gt; 태그를 이용해서 설정하기때문에 **별도 프로퍼티 설정이 없음**



### 2.2 업로드한 파일 접근하기

#### (1) Multipart 인터페이스 사용

* ==**org.springframework.web.multipart.MultipartFile **==
    * 업로드한 파일 정보 및 파일 데이터 표현
    * 업호드한 파일 데이터 읽기


* MuiltipartFile 인터페이스의 주요 메소드
| 메소드 | 설명 |
|--------|--------|
| String getName() | 파라미터 이름을 구한다 |
| String getOriginalFilename() | 업로드한 파일의 이름을 구한다 |
| boolean isEmpty() | 업로드한 파일이 존재하지 않는 경우 true |
| long getSize() | 업로드한 파일의 크기 구한다 |
| byte[] getBytes() throws IOException | 업로드한 파일 데이터를 구한다 | 
| InputStream getInputStream() throws IOException() | 업로드한 파일 데이터를 읽어오는 InputStream 을 구한다 |
| void transferTo(File dest) throws IOException | 업로드한 파일 데이터를 지정한 파일에 저장 |


* MultipartFile.getBytes() 메소드 이용
```java
@RequestMapping(value = "/upload/multipartFile", method = RequestMethod.POST)
public String uploadByMultipartFile(@RequestParam("f") MultipartFile multipartFile,
        @RequestParam("title") String title, Model model) throws IOException {
    if (!multipartFile.isEmpty()) {
   		byte[] bytes = multipartFile.getBytes();
        File file = new File(uploadPath, multipartFile.getOriginalFilename());
        FileCopyUtil.copy(bytes, file);
        model.addAttribute("title", title);
        model.addAttribute("fileName", multipartFile.getOriginalFilename());
        model.addAttribute("uploadPath", file.getAbsolutePath());
        return "upload/fileUploaded";
    }
    return "upload/noUploadFile";
}
```

* MultipartFile.transferTo() 메소드 이용
```java
@RequestMapping(value = "/upload/multipartFile", method = RequestMethod.POST)
public String uploadByMultipartFile(@RequestParam("f") MultipartFile multipartFile,
        @RequestParam("title") String title, Model model) throws IOException {
    if (!multipartFile.isEmpty()) {
        File file = new File(uploadPath, multipartFile.getOriginalFilename());
        multipartFile.transferTo(file);
        model.addAttribute("title", title);
        model.addAttribute("fileName", multipartFile.getOriginalFilename());
        model.addAttribute("uploadPath", file.getAbsolutePath());
        return "upload/fileUploaded";
    }
    return "upload/noUploadFile";
}
```


#### (2) @RequestParam 어노테이션을 이용한 업로드 파일 접근

* @RequestParam 어노테이션 적용된 MultipartFile 타입 파라미터 사용
```java
@RequestMapping(value = "/upload/multipartFile", method = RequestMethod.POST)
public String uploadByMultipartFile(@RequestParam("f") MultipartFile multipartFile,
        @RequestParam("title") String title, Model model) throws IOException {
    if (!multipartFile.isEmpty()) {
        File file = new File(uploadPath, multipartFile.getOriginalFilename());
        multipartFile.transferTo(file);
        model.addAttribute("title", title);
        model.addAttribute("fileName", multipartFile.getOriginalFilename());
        model.addAttribute("uploadPath", file.getAbsolutePath());
        return "upload/fileUploaded";
    }
    return "upload/noUploadFile";
}
```


#### (3) MultipartHttpServletRequest를 이용한 업로드 파일 접근

* MultipartHttpServletRequest 인터페이스 사용
```java
@RequestMapping(value = "/upload/multipartHttpServletRequest", method = RequestMethod.POST)
public String uploadByMultipartHttpServletRequest(
        MultipartHttpServletRequest request, Model model) throws IOException {
    MultipartFile multipartFile = request.getFile("f");
    if (!multipartFile.isEmpty()) {
        File file = new File(uploadPath, multipartFile.getOriginalFilename());
        multipartFile.transferTo(file);
        model.addAttribute("title", request.getParameter("title"));
        model.addAttribute("fileName", multipartFile.getOriginalFilename());
        model.addAttribute("uploadPath", file.getAbsolutePath());
        return "upload/fileUploaded";
    }
    return "upload/noUploadFile";
}
```
    * MultipartHttpServletRequest 인터페이스
        * 멀티파트 요청이 들어올 때 내부적으로 HttpServletRequest 대신 사용되는 인터페이스
    * MultipartHttpServletRequest 인터페이스 파일 관련 주요 메소드
    | 메소드 | 설명 |
	|--------|--------|
	| Iterator&lt;String&gt; getFileNames() | 업로드 된 파일들의 파라미터 이름 목록을 제공하는 Iterator |
    | MultipartFile getFile(String name) | 파라미터 이름이 name인 업로드 파일 정보 구한다 |
    | List&lt;MultipartFile&gt; getFiles(String name) | 파라미터 이름이 name인 업로드 파일 정보 목록을 구한다 |
    | Map&lt;String, MultipartFile&gt; getFileMap() | 파라미터 이름을 키로 파라미터에 해당하는 파일 정보를 값으로 하는 Map |
    
#### (4) 커맨드 객체를 통한 업로드 파일 접근

* 커맨드 객체 이용
    * 커맨드 클래스에 파라미터와 동일한 이름을 가진 **MultipartFile 타입의 프로퍼티 추가**
    ```java
    public class FileCommand {

        private String title;
        private MultipartFile f;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public MultipartFile getF() {
            return f;
        }

        public void setF(MultipartFile f) {
            this.f = f;
        }
    }
    ```
    ```java
    @RequestMapping(value = "/upload/commandObject", method = RequestMethod.POST)
	public String uploadByMultipartHttpServletRequest(
			FileCommand command, Model model) throws IOException {
		MultipartFile multipartFile = command.getF();
		if (!multipartFile.isEmpty()) {
			File file = new File(uploadPath, multipartFile.getOriginalFilename());
			multipartFile.transferTo(file);
			model.addAttribute("title", command.getTitle());
			model.addAttribute("fileName", multipartFile.getOriginalFilename());
			model.addAttribute("uploadPath", file.getAbsolutePath());
			return "upload/fileUploaded";
		}
		return "upload/noUploadFile";
	}
    ```

#### (5) 서블릿 3의 Part 사용하기

* javax.servlet.http.Part 타입 이용
```java
@Controller
public class UploadController2 {

	private String uploadPath = System.getProperty("java.io.tmpdir");

	@RequestMapping("/upload/form.do")
	public String form() {
		return "upload/fileUploadForm2";
	}

	@RequestMapping(value = "/upload/servletPart.do", method = RequestMethod.POST)
	public String uploadByMultipartFile(@RequestParam("f") Part part,
			@RequestParam("title") String title, Model model) throws IOException {
		if (part.getSize() > 0) {
			String fileName = getFileName(part);
			File file = new File(uploadPath, fileName);
			FileCopyUtils.copy(part.getInputStream(), new FileOutputStream(file));
			model.addAttribute("title", title);
			model.addAttribute("fileName", fileName);
			model.addAttribute("uploadPath", file.getAbsolutePath());
			return "upload/fileUploaded";
		}
		return "upload/noUploadFile";
	}

	private String getFileName(Part part) {
		for (String cd : part.getHeader("Content-Disposition").split(";")) {
			if (cd.trim().startsWith("filename")) {
				return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
			}
		}
		return null;
	}
}
```


## 03. 웹소켓 서버 구현 지원
