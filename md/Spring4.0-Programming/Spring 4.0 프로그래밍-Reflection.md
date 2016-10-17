[TOC]



# [리플렉션 API를 사용하여 동적으로 교체 가능한 코드 작성하기](http://javacan.tistory.com/entry/21)

## 리플렉션과 런타임 코드 교체

* 자바에서는 프로그램이 사용하는 클래스의 기능과 관련된 정보는 컴파일 타임에 정해지며, 철저하게 이러한 규칙들을 검사하고 확인한다.

```java
class Callee {
  ...
  public void doit() throws CanDoException {
    ...
  }
  ...
}
```

```java
class Caller {
  Callee callee;
  
  public void someMethod() {
    ...
    callee.doit();
    ...
  }
}
```

* Caller를 컴파일 할때 컴파일러는 CanDoException을 잡아서(catch) 처리하거나 또는 메소드의 throws 구문에 추가해야 한다는 에러 메시지를 출력할 것이다.
  * 클래스가 제공하는 규칙(여기서는 doit() 메소드의 선언)에 따라서 에러 메시지를 출력.
* 컴파일 타임에 클래스가 어떤 기능을 하는지 알 수 없는 경우 어떻게 해야 할까?
* 런타임에 그러한 기능들을 발견할 수 있을까?
* 자바에서는 런타임에 클래스를 분석할 수 있도록 하기 위해 리플렉션 API를 제공.
  * java.lang.reflect 패키지에 정의.
  * 런타임에 클래스가 제공하는 기능들(메소드와 필드)을 알 수 있도록 해준다.
* 자바빈 프레임워크는 빈을 빈박스와 같은 빈을 인식하는 콘테이너에 설치하기 위해서 리플렉션 메커니즘을 사용한다.



## 예시 - 댐 수위 검사 어플리케이션

- 댐에서 수위를 검사하여 방수량을 결정하는 어플리케이션

* 댐에 있는 센서로부터 수위에 대한 데이터가 이 어플리케이션에 전달되면, 이 어플리케이션은 특정한 알고리즘을 사용하여 방수량을 조절할 필요가 있는지 판단하게 된다.



### 요구사항

- 중단 없이 24시간 지속적으로 운행되어야 한다.
- 데이터가 입력되면 특정한 알고리즘을 사용하여 데이터를 처리한다.



### 리플렉션이 필요한 이유

* 수위와 방수량 조절과 관련된 알고리즘은 언제든지 변할 수 있다.
* 알고리즘을 변경하기 위해서는 어플리케이션의 동작을 중지시켜야 한다.
  * 이 어플의 경우 24시간 운행되어야 하므로 중지시킬 수가 없다.
  * 좋지 않은 알고리즘을 계속 사용해야만 한다.
* 리플렉션을 사용하면 런타임에 동적으로 코드를 교체할 수 있다.



### 리플렉션 적용

* Algorithm 인터페이스

  ```java
  interface Algorithm {
    public void process();
  }
  ```

  ​

* Class.forName()을 이용하여 인스턴스 생성 방법

  ```
  Algorithm algo;
  ...
  String className = ...;
  algo = (Algorithm)Class.forName(className).newInstance();
  algo.process();
  ```
  * Class.forName(String) 메소드는 파라미터로 받은 이름의 클래스와 관련된 Class 클래스 객체를 생성.
  * newInstance() 메소드는 Class 클래스가 나타내는 클래스의 인스턴스를 생성

  ​

* Class.forName()을 이용한 알고리즘 교체

  ```java
  import java.io.*;

  public class WaterLevelTracer implements Runnable {
    private final Algorithm DEFAULT_ALGORITHM = new Algo1();
    
    private Algorithm algo;
    private Thread thread;
    
    public WaterLevelTracer(Algorithm algo) {
      if (algo == null) {
        this.algo = DEFAULT_ALGORITHM;
      } else {
        this.algo = alog;
      }
    }
    
    public void start() {
      thread = new Thread(this);
      thread.start();
      
      try {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
        while(true) {
          System.out.println("교체할 Algorithm 클래스를 입력하세요.");
          String className = br.readLine();
          
          if(className.length() == 0) {
            continue;
          } else if (className.equals("!quit")) {
            thread.interrupt();
            break;
          }
          
          // 알고리즘 런타임에 동적으로 교체
          algo = (Algorithm)((Class.forName(className)).newInstance());
        }
      } catch(Exception ex) {
        ex.printStackTrace();
      }
    }
    
    public void run() {
      try {
        while(true) {
          Thread.sleep(3000);
          algo.process();
        }
      } catch (InterruptedException ex) {}
    }
    
    public static void main(String[] args) {
      new WaterLevelTracer(null).start();
    }
  }
  ```
  * 쓰레드를 생성하여 3초 간격으로 지정된 알고리즘의 process() 메소드를 수행
  * start() 메소드를 통해 키보드로부터 알고리즘으로 사용할 클래스의 이름을 입력받은 후 Class.forName() 메소드를 사용하여 동적으로 알고리즘을 교체

  ​

* 다른 알고리즘 구현

  ```java
  public class Algo1 implements Algorithm {
     public void process() {
        System.out.println("Algo1에서 처리");
     }
  }

  public class Algo2 implements Algorithm {
     public void process() {
        System.out.println("Algo2에서 처리");
     }
  }
  ```



* 수행 결과

  ```
  c:\test>java WaterLevelTracer
  교체할 Algorithm 클래스를 입력하세요.
  Algo1에서 처리
  Algo1에서 처리
  Algo2
  교체할 Algorithm 클래스를 입력하세요.
  Algo2에서 처리
  Algo2에서 처리
  Algo2에서 처리
  !quit
  ```

  * "Algo2"를 키보드를 통해 입력한 이 후 WaterLevelTracer의 알고리즘으로 "Algo2" 사용



### 리플렉션 API의 사용

* 위 WaterLevelTracer 클래스 예제에서는 컴파일 타임에 process() 메소드를 제공한다는 사실을 인지
* 컴파일 타임에 클래스에 대한 정보를 알지 못한 상태에서 런타임에 코드를 교체하려면 
  * Class 클래스의 forName(), newInstance(), getMethod(), getMethods(), getInterfaces(), getField()등의 메소드 사용

```java
Class klass = Class.forName(className);
Method[] methods = klass.getDeclaredMethods();
Class[] params;
for (int i = 0 ; i < methods.length ; i++) {
   params = methods[i].getParameterTypes();
   if (methods[i].getReturnType().getName().equals("ReturnValue") &&
       params.length() == 1 && params[0].getName().equals("InputParam") ) {
      
      retVal = (ReturnValue) (methods[i].invoke(klass.newInstance(), paramObj);
   }
}
```

* forName() 메소드를 통해서 파라미터로 전달한 className에 해당하는 Class 객체를 구한다.
* getDeclareMethods() 메소드를 호출하여 Class 객체에 선언되어 있는 메소드 리스트를 구한다.
  * 각각의 메소드는 Method 클래스를 통해서 표현
* for문을 사용하여 각 Method의 시그너처를 구한다.
  * getParameterTypes(), getReturnType() 
* 파라미터 타입이 InputParam이고, 리턴 타입이 ReturnValue 인 것을 발견할 경우 Method.invoke(Object, Object[])를 호출함으로써 Method와 관련된 메소드를 호출하게 된다.
* 파라미터의 타입과 리턴 타입만 명시되어 있을 뿐 인터페이스나 클래스 이름을 전혀 알지 못하는 데도 알고리즘을 교체하여 사용할 수 있다.



## java.lang.reflect.Proxy

JDK 1.3에 새롭게 추가된 다이나믹 프록시 API는 동적인 프록시가 갖는 기능을 제공하기 위해서 위와 비슷한 검색 매커니즘을 사용하고 있다.

* 다이나믹 프록시 API를 사용하여 프록시 객체 생성
  * 프록시 객체는 처리해야 하는 어떤 인터페이스에 대한 정보를 갖고 있다.
  * 프록시 객체는 처리할 인터페이스의 특정한 메소드를 호출한다.
  * 프록시 객체는 최종적으로 호출되는 메소드를 포함하고 있는 객체에 대한 정보를 갖는다.
  * 최종적으로 사용되는 객체는 InvocationHandler 인터페이스를 구현한다.



### Invocation Handler

InvocationHandler 인터페이스는 한개의 메소드만을 갖고 있다.

```java
public Object invoke(Object proxy, Method m, Object[] args) throws Throwable
```

* 파라미터 proxy는 생성된 프록시 객체에 대한 레퍼런스.
* invoke() 메소드는 런타임에 호출되며, 클라이언트 뿐만아니라 구현자도 invoke() 메소드에 대해 알지 못한다.



#### InvocationHandler 구현 예제

```java
public class MyHandler implements java.lang.reflect.InvocationHandler {
   // 이는 딜리게이트 객체이며, 사용자가 정의한 객체가 될 수 있다.
   private Object delegate;
   
   public MyHandler (Object obj) {
      this.delegate = obj ;
   }
   
   public Object invoke (Object proxy, Method  m, Object[] args) throws Throwable {
      try {
         
         // 실제로 딜리게이트 객체의 메소드가 호출될 것이다.
         
      } catch(InvocationTargetException ex) {
         throw ex.getTargetException();
      } catch(Exception ex) {
         throw ex;
      }
      // 특정한 값 리턴
   }
}
```

딜리게이트 객체가 세 개의 인터페이스 A,B,C를 구현한다고 가정. 이 때, 다이나믹 프록시 API를 사용하면 이 인터페이스들 중의 하나의 타입과 관련된 프록시 객체를 생성할 수 있다.

```java
A a = (A) java.lang.reflect.Proxy.newProxyInstance(
                                  A.class.getClassLoader(),
                                  new Class[] { A.class, B.class, C.class },
                                  new MyHanlder(delegate);
```

* 인터페이스 A에 doSomething() 메소드가 선언되어 있다고 가정
  * 클라이언트가 a.doSomething()을 실행하면 그 호출은 실제로 InvocationHandler 인스턴스에 전달된다.
  * 위의 경우에는 MyHandler 객체에 포워딩 된다.
  * 실제로 호출이 포워딩되기 전에 리플렉션 API를 사용하여 newProxyInstance() 메소드의 두번째 파라미터를 통해서 프록시에 제공된 모든 인터페이스를 검사함으로써 그 Method에 대한 레퍼런스를 구한다.
  * 이 Method 레퍼런스와 그것에 대한 파라미터는 핸들러의 invoke() 메소드에 전달된다.
  * 핸들러의 invoke() 메소드는 실제로 딜리게이트 객체에 있는 메소드를 호출하거나 또는 그 외의 원하는 것을 실행할 것이다.



## 결론

* 런타임에 동적으로 클래스를 교체할 수 있는 가장 좋은 방법은 리플렉션 API를 사용하는 것이다.(필자 생각)
* 리플렉션 API를 사용함으로써 컴파일 타임에 클래스가 제공하는 자세한 정보를 알 수 없는 상황에서 런타임에 동적으로 클래스를 교체할 수 있게 된다.
* 관련 링크
  * [Java Tutorial - Reflection API](http://java.sun.com/docs/books/tutorial/reflect/index.html)
  * [Java World - Explore the Dynamic Proxy API](http://www.javaworld.com/javaworld/jw-11-2000/jw-1110-proxy.html)
  * [Java World - Untangle your servlet code with reflection](http://www.javaworld.com/javaworld/jw-12-2000/jw-1221-reflection.html)