## Arrow Function

* 코드 형태 : (param) => {함수코드}
  * 자바 람다와 유사
  * 람다와 조금 다른 표현들
    * (param1, param2, ...rest) => {코드}
      * rest는 변수명. 관례적으로 rest 사용
      * rest변수에 배열형태로 값이 추가됨
    * (param1, parma2=123) => {코드}
      * Default 파라미터
* Memo : 자바스크립트에서의 함수는 무조건 return이 있음. 생략하면 undefined 반환
  * () => {} 는 undefined 반환
* 함수와 유사하지만 new로 인스턴스 생성 불가
  * 함수는 new로 인스턴스 생성 가능함
    * new 연산자를 사용하여 인스턴스를 생성하면 대상의 prototype 하위의 construct를 찾아감
    * construct에는 해당 오브젝트의 생성 정보를 포함하고 있어서 인스턴스 생성이 가능함
    * 인스턴스 생성시 prototype 하위의 프로퍼티들로 인스턴스를 생성
    * 오브젝트에서 prototype이 아닌 다른 프로퍼티에 선언된 변수나 함수들은 인스턴스에 포함되지 않음
  * Array function에는 prototype 변수가 없음
    * 그래서 인스턴스 생성이 불가능
* Arrow 함수에서는 arguments 사용 불가
  * 일반 함수의 경우에는 함수가 호출되면 arguments 오브젝트가 생성됨
    * 함수에서 선언된 파라미터 수보다 전달받은 인자의 수가 더 많은 경우 arguments 오브젝트를 통해 인자 값 사용 가능
  * 함수의 실행이 끝나면 arguments 오브젝트를 제거
  * Arrow 함수에서는 사용이 안되기 때문에 rest 파라미터 사용 권장



## Arrow와 This

* 함수 내에서의 this는 함수를 호출한 오브젝트를 의미

* 관례적으로 인스턴스로 생성할 오브젝트는 첫글자를 대문자로 선언

  * 첫글자가 대문자인 오브젝트는 prototype 정의를 포함함

* constructor에 arrow function을 사용할 수 있지만 this의 스코프 범위가 혼동될 수 있으므로 권장하지 않음

* 예제

  ```javascript
  const Sports = function() {
      this.count =20;
  };
  Sports.prototype = {
  	plus: function() {
        this.count += 1;  
  	},
      get: function() {
      	setTimeout(function() {		// setTimeout은 window의 메소드
              console.log(this === window);	// 결과는 true. setTimeout 함수는 window.setTimeout으로 호출한 것이기 때문에 함수 내에서 사용하는 this의 주체는 windows가 됨
              console.log(this.plus);		// this가 window이기 때문에 undefined임            
      	}, 1000);
      },
      arrow: function() {
          setTimeout(() => {
              this.plus();	// arrow 함수 내의 this는 인스턴스가 됨. 즉, 여기서 this는 Sports 인스턴스
              console.log(this.count);	// 정상 출력됨
          }, 1000);
      },
      add: () => {
          this.count += 1;	// 여기서의 this는 window를 가리킴. 그래서 값이 NaN.
          					// prototype 내의 this는 동일한 인스턴스를 가리켜야하는데 prototype 내 arrow 함수의 this는 window를 가리키기 때문에 이런식으로 사용하면 안됨
      }
  };
  
  const sportsObj = new Sports();
  sportsObj.arrow();
  ```

  

## Lambda Function

- 자바스크립트에서의 람다는 익명 함수를 의미함