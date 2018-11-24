# Proxy

* 기본적은 오퍼레이션의 동작, 행위를 가로채서 실행

  * 중계 역할

* 추가적인 행위를 첨가 하거나 모든 인터페이스를 외부에 노출시키는 것을 방지하기 위한 용도로 사용

* Tip : ES6는 빌트인으로 13개의 내부 메소드를 제공

  * 객체의 프로퍼티를 참조하면 내부적으로는 getter가 호출됨
  * Proxy를 사용하면 target의 getter메소드를 오버라이딩 해서 추가적인 코드를 작성할 수 있음(trap)

* Proxy 예

  ```javascript
  const target = {food: "밥"};
  const middle = new Proxy(target, {});
  const left = middle.food;
  ```

  * Proxy객체인 middle을 통해서 target의 값 접근

  * Proxy 생성자의 첫번째 인자에는 값을 참조할 대상(target)을 지정

  * Proxy 생성자의 두번째 인자에는 참조 대상의 getter 메소드를 오버라이딩 할 오브젝트(trap)를 지정

    ```javascript
    const handler = {
        get(target, key) {
            return target[key] + ",수저";
        },
        set(target, key){}
    }
    
    const target = {food: "밥"};
    const middle = new Proxy(target, handler);
    const left = middle.food;
    ```

    

* 