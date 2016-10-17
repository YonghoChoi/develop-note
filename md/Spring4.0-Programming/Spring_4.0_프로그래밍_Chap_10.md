# 10. 스프링 MVC: 기타 설정

## 01. 서블릿 3 기반 설정

* 서블릿 3 버전부터 web.xml 파일 대신 **자바 코드를 이용한 서블릿 필터 등록 가능**
    * 자바 코드를 이용한 스프링 DispatcherServlet 등록
    * org.springframework.web.WebApplicationInitializer 인터페이스 상속 구현
    ```java
    public interface WebApplicationInitializer {
		void onStartup(ServletContext servletContext) throws ServletException;
	}
    ```
    ```java
    public class SpringServletConfig implements WebApplicationInitializer {

        @Override
        public void onStartup(ServletContext servletContext) throws ServletException {
            XmlWebApplicationContext servletAppContext = 
                    new XmlWebApplicationContext();
            servletAppContext.setConfigLocation("/WEB-INF/dispatcher.xml");

            DispatcherServlet dispatcherServlet = 
                    new DispatcherServlet(servletAppContext);
            ServletRegistration.Dynamic registration = 
                    servletContext.addServlet("dispatcher", dispatcherServlet);
            registration.setLoadOnStartup(1);
            registration.addMapping("*.do");
        }

	//	org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer
	}
    ```
    ```xml
    <servlet>
		<servlet-name>dispatcher</servlet-name>
		<servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
		<init-param>
			<param-name>contextConfigLocation</param-name>
			<param-value>
				/WEB-INF/dispatcher.xml
			</param-value>
		</init-param>
		<load-on-startup>1</load-on-startup>
	</servlet>
    <servlet-mapping>
		<servlet-name>dispatcher</servlet-name>
		<url-pattern>*.do</url-pattern>
	</servlet-mapping>
    ```

* @Configuration을 이용한 설정, 루트 컨텍스트 설정
    * 코딩할 내용이 너무 많다
    * 추상 클래스 제공 
	![spring10-1.png](.\spring10-1.png)
    * ==XML -> AbstractDispatcherServletInitializer==
    ```java
    public class SpringServletConfig2 extends AbstractDispatcherServletInitializer {
        // abstract 메서드 재정의
        @Override
        protected WebApplicationContext createServletApplicationContext() {
            XmlWebApplicationContext servletAppContext =
                    new XmlWebApplicationContext();
            servletAppContext.setConfigLocation("/WEB-INF/dispatcher.xml");
            return servletAppContext;
        }

        // 상위 클래스는 "dispatcher" 리턴
        @Override
        protected String getServletName() {
            return "dispatcher2"; // 기본 값은 "dispatcher"
        }

        // abstract 메서드 재정의
        @Override
        protected String[] getServletMappings() {
            return new String[] { "*.do" };
        }

        // 상위 클래스는 true 리턴
        @Override
        protected boolean isAsyncSupported() {
            return super.isAsyncSupported();
        }

        // abstract 메서드 재정의
        @Override
        protected WebApplicationContext createRootApplicationContext() {
            XmlWebApplicationContext rootAppContext =
                    new XmlWebApplicationContext();
            rootAppContext.setConfigLocation("/WEB-INF/root.xml");
            return rootAppContext;
        }

        // 상위 클래스는 기본으로 null 리턴
        @Override
        protected Filter[] getServletFilters() {
            CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
            encodingFilter.setEncoding("utf-8");
            Filter[] filters = new Filter[] { encodingFilter };
            return filters;
        }
    }
    ```
        * createServletApplicationContext()
            * DispatcherServlet이 사용할 WebApplicationContext 리턴
        * getServeletName()
            * DispatcherServlet의 서블릿 이름 리턴. 재정의 안하면 'dispatcher' 사용
        * getServletMappings()
            * 생성할 DispatcherServlet 매핑될 경로 리턴
        * isAsyncSupported()
            * DispatcherServlet이 비동기 지원하는지 여부. 기본은 true
        * createRootApplication()
            * 루트 컨텍스트 생성. 필요없다면 null 리턴
        * getServletFilters() 
            * DispatcherServlet에 적용할 서블릿 필터 객체
    * **DispatcherServlet 객체를 직접 생성할 필요 없음. AbstractDispatcherServletInitializer에서 생성.**

	* ==@Configuration 기반 자바 설정 -> AbstractAnnotationConfigDispatcherServletInitializer==
	```java
    public class SpringServletConfig3
		extends AbstractAnnotationConfigDispatcherServletInitializer {

        @Override
        protected Class<?>[] getRootConfigClasses() {
            return new Class<?>[] { RootConfig.class };
        }

        @Override
        protected Class<?>[] getServletConfigClasses() {
            return new Class<?>[] { WebConfig.class };
        }

        // 상위 클래스는 "dispatcher" 리턴
        @Override
        protected String getServletName() {
            return "dispatcher3"; // 기본 값은 "dispatcher"
        }

        // abstract 메서드 재정의
        @Override
        protected String[] getServletMappings() {
            return new String[] { "*.do" };
        }

        // 상위 클래스는 true 리턴
        @Override
        protected boolean isAsyncSupported() {
            return super.isAsyncSupported();
        }

        // 상위 클래스는 기본으로 null 리턴
        @Override
        protected Filter[] getServletFilters() {
            CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
            encodingFilter.setEncoding("utf-8");
            Filter[] filters = new Filter[] { encodingFilter };
            return filters;
        }
    }
    ```
        * getRootConfigClasses()
            * 루트 컨텍스트
        * getServletConfigClasses()
            * 스프링 설정 클래스 목록
    * ==AnnotationConfigWebApplicationContext 객체 생성은 상위 클래스인 AbstractAnnotationConfigDispatcherServletInitializer 에서 이뤄짐
    



   
