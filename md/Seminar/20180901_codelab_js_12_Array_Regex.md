# Array

* from()

  * 이터러블 오브젝트를 Array로 변환
    * Array-like 포함

* entries() : Array를 이터레이터 오브젝트로 생성하여 반환

  ```javascript
  const values = [10, 20, 30];
  const iterator = values.entries();
  
  for (const [key, value] of iterator) {
      console.log(key, ": ", value);
  }
  ```

* find()

  * find()와 filter()는 모두 Array에서 특정 값을 찾는 메소드이지만 find는 값과 일치하는 것을 찾으면 찾기를 중단하지만, filter는 값과 일치하는 것을 찾은 후에도 배열 끝까지 찾음

  * 첫번째 인자는 콜백 함수

    * 실제 값의 비교는 콜백함수에서 수행하고 반환되는 값에 따라 find 메소드가 발견 여부를 판단
    * ES6 들어서면서 콜백함수의 활용도가 높아짐
    * Find 메소드 자체는 실제 작업을 콜백 함수에 위임

    ```javascript
    result = [1, 2, 1].find(
    	function(value, index, all) {	// value는 현재 값, index는 현재 인덱스, all은 배열 전체 값
            return value === 1 && value === this.key;
    	},
    	{key: 1}
    );
    ```

    

    

# 정규표현식

- 플래그
  - u(unicode) : 매치 대상을 유니코드로 인식
  - y(sticky) : lastIndex 값을 설정
    - lastIndex는 기본값이 0이기 때문에 정규표현식의 패턴매칭을 0번 인덱스부터 시작함.
    - 만일 매치 시킬 문자열의 인덱스를 알고 있다면 lastIndex값을 설정해서 패턴 매칭할 위치를 지정할 수 있다.



