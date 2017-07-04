기본 웹 컨테이너가 예외를 수신하면 예외를 발생시키는 JSP 페이지에 언급 된 오류 페이지가 있는지 여부를 먼저 확인합니다. (물론 예외가 JSP의 try-catch 블록 내에서 발생하면 해당 catch 블록의 우선순위가 더 높습니다.) JSP에 대해 오류 페이지가 지정되면 'Exception' 오브젝트가 해당 오류 페이지로 전달됩니다. 

JSP 페이지가 오류 페이지를 지정하지 않고 try 구문을 사용한 경우 catch 블록에도 명시되지 않았다면, 웹 컨테이너는 Deployment Descriptor(web.xml)에서 해당 예외에 대한 오류 페이지를 찾기 시작합니다. 해당 예외에 대한 오류 페이지가 발견되지 않으면 컨테이너는 예외의 수퍼 클래스에 대한 오류 페이지를 찾고 컨테이너가 적절한 오류 페이지를 찾을 때까지 또는 Throwable 클래스 (Java의 전체 예외 처리 메커니즘의 최상위 수퍼 클래스)에 도달 할 때까지 계속됩니다.

JSP의 에러 페이지는 아래와 같이 web.xml 파일에 지정할 수 있습니다. 

```xml
<!-- redirecting an error to a servlet -->
<error-page>
  <error-code>404</error-code>
  <location>/errorServlet</location>
</error-page>
...
```

Servlet에서 에러 처리 시에 Exception 클래스 대신 그보다 상위 클래스인 Throwable 클래스를 사용하는데 그 이유는 드물긴 하지만 JVM 오류나 native 메소드 오류는 Exception으로는 처리할 수 없기 때문입니다.

error handler (JSP / Servlet)로 전달 된 모든 JSP 예외는 실제로 웹 컨테이너에 의해 "javax.servlet.jsp.jspException"라는 객체가 request의 attribute로 저장되어 handler로 request 객체가 전달됩니다. JspException 클래스의 생성자는 다음과 같습니다. 

* public JspException (java.lang.String msg) - 일반적으로 사용자 정의 메시지를 서버 로그에 기록하거나 이를 사용자에게 표시하기 위한 것입니다.
* public JspException (java.lang.String message, java.lang.Throwable rootCause) - 'Exception' 암시적 객체가 not null이면 rootCause 매개 변수로 전달됩니다.
* public JspException (java.lang.Throwable rootCause) - null 이외의 'Exception' 암시적 객체가 매개 변수로 전달됩니다.

생성자의 예외 객체의 타입을 보면 위에서 설명한 것과 같이 Throwable 타입으로 선언되어 있는 것을 볼 수 있습니다. 

이렇게 예외를 던지는데 JspException 클래스로 한번더 래핑할 이유가 있을까? 꼭 그럴 필요는 없지만 몇가지 이유를 생각해보면 조건에 따라 예외를 생성할 수 있는 유연성이 생깁니다. 예를 들어 예외 객체 없이 사용자가 원하는 메시지만 전달을 하는 경우 이를 통해 메시지를 가공하여 로그를 작성하거나 사용자에게 표시할 수 있습니다. 또한 오류 조건에 대해 추가 텍스트를 쉽게 사용할 수 있다는 장점이 있습니다. 오류를 사용자가 더 이해하기 쉽도록 사용자 정의 메시지를 지정할 수 있습니다. 결국 반드시 사용해야만 하는 이유는 없지만 약간의 유연성을 제공한다는 것입니다.

