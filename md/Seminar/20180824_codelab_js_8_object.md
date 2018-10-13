# Object

* Object.is()
  * 오브젝트의 비교가 목적이 아니라 값의 비교가 목적임
  * == : 타입은 비교하지 않고 값만 비교 (사용 권장하지 않음)
  * === : 값과 값 타입 모두 비교
  * Object.is()와 === 차이
    * +0 === -0은 true지만 Object.is()는 false
      * 사람 입장에서는 같다고 생각할 수 있지만 기계적으로는 틀림
        * 음/양 표현을 하는 비트 플래그 값이 다름
    * NaN === NaN은 false, Object.is()는 true
      * === 비교로는 문제가 있음
      * NaN도 값인데 ===로 비교 시 false라는 것은 잘못됨
* Object.assign()
  * 첫번째 인자의 오브젝트에 두번째 인자를 복사
    * String의 경우 분할되어 배열 형식으로 들어감
  * 프로퍼티 디스크립터는 복사하지 않음
  * 오브젝트에 오브젝트를 할당하기 위한 용도로 사용
    * 변수에 오브젝트를 할당하면 프로퍼티가 연동됨
      * 한쪽의 프로퍼티값을 바꾸면 다른 곳도 바뀜
    * 연동되지 않게 하려면 별도 처리 필요 (deep copy)
      * 방법1 : For 루프를 돌면서 프로퍼티 단위로 복사
      * 방법2 : assign copy
        * 1 depth만 프로퍼티 연동되지 않음
        * `{key: {two: value}}` 형태는 value 값이 연동됨
        * 여러 오브젝트를 전달하여 merge 용도로 사용할 수도 있음



##  proto

* prototype과 `__proto__`의 차이

  * prototype은 prototype.메소드명으로 호출
  * `__proto__` 메소드는 바로 호출 가능

* 예제

  ```javascript
  const Sports = function(){
      this.member = 11;   // 인스턴스 프로퍼티
  };
  Sports.prototype.getMember = function(){};
  
  const sportsObj = new Sports();
  console.log(sportsObj.__proto__ === Sports.prototype);
  ```

  * new 연산자를 사용하게되면 prototype에 연결되어 있는 것들만 가지고 인스턴스를 생성
  * prototype에 연결되어 있던 것들을 인스턴스 생성 시 `__proto__` 하위로 가져옴 
  * 인스턴스 하위에 빌트인 `__proto__` 오브젝트가 존재함. 이는 모든 오브젝트에 포함됨 (상속)
  * 인스턴스 생성 시 prototype은 원본 오브젝트를 참조함 
  * 원본을 변경하면 생성한 모든 인스턴스들은 변경된 내용을 실행 함

* `__proto__` 는 ES5 까지는 표준이 아니었음. ES6부터 표준에 추가

  * 개발자가 직접 사용하는 것은 권장하지 않음
  * `__proto__` 의 내용은 원본의 prototype의 내용이기 때문에 변경이 필요한 경우 원본을 수정할 것을 권장
  * 엔진이 사용



## setPrototypeOf()

* 파라미터에 오브젝트 또는 인스턴스 작성
* Object.isExtensible()이 false이면 Type Error
* 빌트인 오브젝트의 경우 prototype에 두번째 파라미터를 첨가하지 않고 해당 인스턴스가 반환됨
* `__proto__`에 첨부는 권장하지 않음
  * prototype에 첨부하는 Object.create() 사용 권장
  * ES6의 class를 사용하면 이런 문제들도 전부 해결됨
* 