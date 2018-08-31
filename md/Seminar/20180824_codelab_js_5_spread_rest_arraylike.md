## spread

* 문법 : [...iterable]

* 이터러블 오브젝트를 하나씩 전개

* 예제

  ```javascript
  const two = [21,22];
  const five = [51,52];
  const one = [11, ...two, 12, ...five]
  ```

  * array안에 array가 또다시 들어가는 것이 아니라 분리시켜서 하나의 array를 만듦
  * 결과는 [11, 21, 22, 12, 51, 52]

* 문자열 분리

  ```javascript
  const str = "music";
  const chars = [...str];
  console.log(charas)
  ```

  * 결과는 ['m','u','s','i','c']

* 파라미터로 전달

  ```javascript
  const two = [21,22];
  const five = [51,52];
  two.push(...five);
  ```

* 오브젝트 spread

* ```javascript
  const one = [{name1: 11}, {name2: 22}];
  const two = [{name3: 33}, ...one];
  ```

* function spread (가장 많이 사용됨)

  ```javascript
  function get(one, two, three){};
  const values = [10,20,30];
  get(...values);
  ```

  * 배열의 순서대로 함수의 파라미터에 각각 할당됨



## rest 파라미터

문법 : function(param, paramN, ...rest)

* 호출받는 function 파라미터에 ...에 이어서 파라미터 이름 작성
* 파라미터 값들을 모아서 배열 형태로 사용
  * Array.isArray로 타입검사를 해보면 true 반환. 즉, 배열타입임
* spread로 분리시키고 rest로 다시 묶는 식으로 활용
* Rest 파라미터를 사용하면 arguments 오브젝트를 만들지 않음
  * 명확하게 명시할 수 있는 Rest 파라미터 사용 권장
  * argument는 보이지 않는 곳에서 생성됨



## Array-like

* 배열처럼 반복이 가능한 오브젝트

* Argument 오브젝트가 바로 Array-like 오브젝트임

* 프로토콜을 지켜야함

  * 프로퍼티 키 값이 0부터 순서 값

    * 순서 값의 순서가 지켜지지 않으면 오류

  * 전체 프로퍼티수를 length로 작성

    ```javascript
    const values = {0: value, 1: value, length: 2}
    for(const key in values) {	// for-in의 경우 작성 순서가 아닌, 숫자 > 영문 > length 순서로 전개
        console.log(key, values[key]);
    }
    
    for(let k = 0; k < values.length; k++){ // 작성한 순서대로 전개하려면 for문 사용
        console.log(values[k]);
    }
    ```

