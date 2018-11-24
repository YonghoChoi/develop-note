# Symbol

## primitive data

* 오브젝트 안에 있는 값

  * 값이기 때문에 메소드를 갖고 있지 않음

    ```javascript
    const num = 123;
    ```

    * num에는 123만 할당될 뿐 아무것도 첨부되지 않음

* ES5의 primitive data

  * string, number, boolean, null, undefined
  * 밖으로 노출됨

* ES6에서 symbol이 추가됨

  * 밖으로 노출되지 않음
  * 값을 노출시킬 필요가 없다면 symbol을 사용 (html의 hidden과 유사)

* primitive는 wrapper 오브젝트가 있음

  * String, Number, Boolean, Symbol
  * 인스턴스지만 console.log로 값을 출력해보면 primitive data가 출력됨
  * undefined, null은 wrapper 오브젝트가 없기 떄문에 값일 수 밖에 없음



## Symbol

* Symbol 사용 형태

  ```javascript
  const sym = Symbol("설명");
  ```

* 설명이나 주석 용도로 사용

* 프로그램 내 유일한 값 제공

  * Symbol을 생성한 형태 그대로가 값이 됨

    * 위 예제에서는 `Symbol(설명)` 이 값

  * Object 프로퍼티 키로 사용

    ```javascript
    const sym = Symbol("123");
    const obj = {[sym]: "456"};
    ```

    * symbol-keyed property라고 부름
    * obj.sym 형태로 사용 불가능
      * sym 자체가 값이기 때문에 .으로 참조할 수 없음
      * obj[sym] 형태로 사용해야함

* new 사용 불가

  * Symbol()은 값을 반환하므로 값을 생성한다는 표현이 적합할 듯
    * new 연산자를 사용하지 않을 뿐이지 생성자 역할을 함

* 생성한 Symbol 값 변경 불가

  * 변경하게 되면 유니크한 특성이 변질될 수 있기 때문에 값이 변경되면 안됨

* 생성한 Symbol에 프로퍼티 설정 불가

  * Strict mode에서 에러

* 템플릿에 사용 불가

  ```javascript
  const sym = Symbol('123');
  `${sym}` // 에러
  ```

* 외부에서 절대 변경할 수 없고 알 수도 없기 때문에 생성한 함수 내에서만 사용 가능

  * 클로저와 결합하면 정보 은닉에 유용하게 사용할 수 있을 듯

* Symbol은 [[Enumerable]]: false 이기 때문에 for-in으로 열거할 수 없음

  * Object.getOwnPropertySymbols()로 열거 가능

* JSON.stringify() 사용 시 Symbol 값이 문자열로 반환되지 않음

  * 즉, Symbol을 키로 사용하는 프로퍼티의 경우에는 JSON 변환에서 제외됨



## Well-Known Symbols

* 11개의 Well-Known Symbol이 존재
* 오버 라이드 용도로 많이 사용
* 사용자가 Well-Known Symbol 을 사용하면 호출시 해당 Symbol을 우선적으로 실행함



### toStringTag

* 인스턴스의 toString() 메소드를 호출할 경우 해당 인스턴스에 toString 메소드가 구현되어 있지 않으면 Object의 toString이 호출됨. 그러므로 서로 다른 인스턴스들의 결과값이 [object Object]로 동일하게 출력되기 때문에 인스턴스 타입을 명확하게 알 수 없음

* Symbol.toStringTag로 구분 가능

  ```javascript
  const Sports = function() {};
  const sportsObj = new Sports();
  console.log(sportsObj.toString()); // [object Object] 반환
  Sports.prototype[Symbol.toStringTag] = "Sports-Function";
  console.log(sportsObj.toString());	// [object Sport-Function] 반환
  ```

  * Tip : new Sports()를 할 때 넘길 인자가 없으면 new Sports만 써도 됨. New 연산자가 Sports의 constructor를 호출하기 때문에 가능. 하지만 일반적이지 않기 때문에 소괄호 붙여줄 것을 권장

  ```javascript
  class Sports {
      get [Symbol.toStringTag]() {
          return "Sports-Class";
      }
  }
  
  const sportsObj = new Sports();
  console.log(sportsObj.toString()); 	// [object Sports-Class] 반환
  ```

* Map과 같이 일부 빌트인 오브젝트들에는 toStringTag가 정의되어 있음

* Tip : with문은 사용하지 않는 것이 좋음

  ```javascript
  const sports = {
      soccer: "축구"
  }
  
  with(sports) {
      console.log(soccer);	// sports 참조 없이 사용 가능
  }
  ```



### species

* Symbol.species는 constructor 함수 반환
  * 즉, 인스턴스를 반환함

* 오버라이드하면 다른 인스턴스를 반환할 수 있음

* species 를 사용하는 오브젝트의 경우 메소드 호출 반환값은 새로운 인스턴스

  ```javascript
  class ExtendArray extends Array {
  	static get [Symbol.species]() {
  		return Array;	// 생성자의 반환값으로 Array 오브젝트가 반환됨
  	}
  };
  
  const oneInstance = new ExtendArray(1,2,3);
  const twoInstance = oneInstance.slice(1,2);	// twoInstnace는 Array 오브젝트가 됨
  
  console.log(oneInstance instanceof ExtendArray);	// true
  console.log(twoInstance instanceof Array);			// true
  console.log(twoInstance instanceof ExtendArray)		// false
  ```

  

* Symbol.species가 포함된 빌트인 오브젝트

  * Array, Map, Set, Promise
  * RegExp, ArrayBuffer, TypedArray



### RegExp

* 정규 표현식을 사용할 수 있는 String 메소드
  * match(), replace(), search(), split()
  * 4개의 메소드에 대응하는 symbol 함수 제공
  * ex) match 함수가 호출되면 Symbol.match 작성 여부를 확인한 후 작성되어 있으면 Symbol.match에 정의된 내용이 수행됨



## Symbol Method

### for()

- Global Symbol Registry라는 저장소가 존재함

  - 공유 영역이기 때문에 다른 오브젝트에서도 접근 가능

- Symbol.for()를 사용하면 Global Symbol Registry에 Symbol 값을 저장

  - Symbol()은 로컬 영역에 저장
  - Symbol.for()는 글로벌 영역에 저장

- {key: value} 형태로 저장

- For 메소드의 파라미터로 전달되는 문자열이 Key가 되고 Symbol()로 생성한 값이 value가 됨

- 키가 중복될 수 있으므로 주의

  - 같은 key가 존재하면 등록된 값을 반환
    - Symbol() === Symbol()의 결과는 false
    - Symbol.for("test") === Symbol.for("test")의 결과는 true
  - key값이 길어지더라도 유일하게 정의

- Symbol.keyFor()를 사용하면 Symbol을 인자로 전달해서 값을 얻어낼 수 있음

  ```javascript
  const symTest = Symbol.for("test");
  console.log(Symbol.keyFor(symTest));	// test가 출력됨
  ```

  