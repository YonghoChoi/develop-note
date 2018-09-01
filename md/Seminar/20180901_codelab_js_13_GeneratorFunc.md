# Generator

* Generator function : function* 키워드를 사용한 함수

* Generator function을 호출하면 함수 블록을 실행하지 않고 Generator 오브젝트를 생성해서 반환

  * 오브젝트를 만드는 과정과 블록을 실행하는 부분을 나누어서 관리

* Generator function을 통해 반환된 오브젝트를 사용해서 함수 블록을 실행(next 메소드)

  * bind의 경우에도 이와같이 함수를 실행할 오브젝트를 반환해서 사용한다는 면에서 비슷

  ```javascript
  const sports = function*(one, two) {	// Generator 함수 선언
      console.log("함수 블록");
      yield one + two;
  };
  
  const genObj = sports(10, 20);  // 이 때는 함수가 호출되지 않고 generator object가 반환됨
  const result = genObj.next(); // next 메소드를 호출하여 함수 블록을 실행
  console.log(result);
  ```

* yield : 제너레이터 함수를 멈추거나 재실행

  ```javascript
  function* genFunc1(one) {
      const two = yield one;
      const param = yield two + one;
      yield param + one;
  };
  
  const genObj = genFunc1(10);
  
  let result = genObj.next();
  console.log(result);
  
  result = genObj.next();
  console.log(result);
  
  result = genObj.next(20);
  console.log(result);
  
  function* genFunc2() {
      return 10;
  }
  
  const genObj2 = genFunc2();
  result = genObj2.next();
  console.log(result);
  ```

  * yield 표현식 평가를 완료하면 {value: 값, done: true/false} 형태로 반환
    * yield가 정상적으로 수행되면 done 값은 false
    * next를 호출했을 때 더이상 수행할 yield가 없다면 done 값은 true

  

* next() : yield 또는 return을 만날 때까지 Generator Function 내용을 실행

  * Next 메소드를 수행하면 yield를 만날 때까지 함수 내용을 진행함
    * yield를 만나기 전에 return으로 반환 될 경우 함수처럼 값을 반환하여 {value: 값, done : true}가 됨
    * done이 true 이기 때문에 또 다시 next()를 호출하게되면 value는 undefined가 됨
  * Next 메소드에 인자를 전달하면 현재 수행 중인 yield 구문의 왼쪽 변수에 값이 할당됨
    * 위 예제에서 첫 yield를 수행한 후 next 인자로 20을 전달하면 two에 20이 할당됨
  * Generator 함수를 정의할 때는 yield를 항상 작성해주고, Generator 오브젝트에서 반환된 값에서 done을 체크하여 Generator 오브젝트 수행 완료를 판단

* return() : Generator 오브젝트의 iterator 수행을 종료

  * next를 수행하고 값을 반환한 것과 동일, 이 때 done 값은 true

* throw() : Generator 함수 내에서 Exception을 발생시킴 (마지막으로 수행한 yield 위치에서)

* yield* : 이터러블 오브젝트를 오른편에 선언할 경우 해당 이터러블 오브젝트를 수행