# operator

### destructuring

* Destructuring Assignment

* 코드 형태

  ```javascript
  let one, two, three;
  [one, two, three] = [1, 2, 3];	// array 분할 할당
  ```

  * 분할 할당의 개념

* 중첩된 array라도 형태만 맞추면 변수에 값 할당 됨

  ```javascript
  [one, two, [three, four]] = [1, 2, [3, 4]]
  ```

* 변수할당이 필요 없는 경우 콤마로 구분해서 pass 가능

  ```javascript
  [one, , , four] = [1,2,3,4]
  ```

* 오브젝트 분할

  ```javascript
  const {one, two} = {one: 10, two: 20}
  ```

  * 같은 프로퍼티명을 사용할 경우 ES5에서는 에러, ES6에서는 마지막 값으로 대체

* 파라미터 분할

  ```javascript
  total({one: 1, two: 2})
  function total({one, two, five = 5}) {
      
  }
  ```

  * 함수의 받는 파라미터에도 destructuring 사용 가능 
  * 디폴트 파라미터 사용 가능



## Object Operator

* Object에 초깃값 설정 (Shorthand property name)

  ```javascript
  const one = 1, two = 2;
  const values = {one, two};
  console.log(values);
  ```

  * 결과 : {one: 1}, {two: 2}



* Object 내에서는 function 키워드를 작성하지 않음

  ```javascript
  const obj = {
      getTotal(param) {
          return param + 123;
      }
  }
  ```

  * 강사님께서는 static function 작성 시 이 형태를 주로 사용



## Descriptor

* 프로퍼티의 값을 설명

* 자바스크립트에서는 프로퍼티나 어트리뷰트(속성)의 차이가 크게 없어서 어트리뷰트를 거의 사용하지 않지만 Descriptor에서는 사용

* 프로퍼티 디스크립터 타입

  * 데이터
    * value
    * writable
  * 악세스
    * get
    * set
  * 공용
    * enumerable
    * Configurable

* API로 제공하는 경우 해당 오브젝트의 사용을 제한을 위해 활용

* 데이터와 악세스 설정을 함께 할 수 없음

* 악세스의 get과 set을 함께 설정할 수 없음

* defineProperty를 사용하기 전에는 오브젝트의 설정은 기본 값이 전부 true임

* defineProperty를 사용하게 되면 전부 디폴트가 false가 되고 지정을 해야 true가 됨

  ```javascript
  // ES5
  var obj = {book: "초기값"}
  Object.defineProperty(obj, "book", {
      get: function(){return "책";},
      enumerable: true
  });
  console.log(obj.book);	// 책이 출력됨
  
  // ES6
  const obj = {
      value: 123,
      get getTotal() {
          return this.value;
      }
  }
  ```

  * enumerable만 true이고 나머지 설정들은 전부 false
  * value, get, set의 경우 undefined
  * obj.book을 실행하면 descriptor 설정에 따라 데이터인지 악세스인지 확인
    * value에 값이 존재하는 경우 데이터 타입
    * get 또는 set이 존재하는 경우 악세스 타입
  * descriptor 내용을 찾아서 get을 호출함
  * 명시적으로 obj.book.get()을 호출하면 에러
    * 엔진이 하는 일임



## 프로퍼티 이름 조합

```
const name = "tennis";
const [name+"func"] = func(){  
};
```



## 거듭제곱

* Exponentiation 연산자
* ** : 곱하기 문자를 연속하여 2개 작성





## default value

- 값을 할당하지 않으면 default 값 할당

- 디폴트 값 적용 순서는 왼쪽에서 오른쪽으로

- 값에 undefined를 설정하면 default value가 적용됨

  