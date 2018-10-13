# for-of

* 이터러블 오브젝트 반복

  * Symbol.iterable이 존재해야 가능

* 예제

  ```javascript
  for(const value of [10, 20, 30]) {
      console.log(value);
  }
  ```



* NodeList 엘리먼트를 하나씩 반복하여 전개

* 예제

  ```javascript
  const nodes = document.querySelectorAll("li");
  for(const node of nodes) {
      console.log(node.textContent);
  }
  ```

  * Node는 자바스크립트 영역이 아닌 DOM 영역
  * 자바스크립트는 웹을 표현하기 위한 각 요소들(HTML, CSS, DOM 등)을 통합해서 처리할 수 있는 아키텍처를 가지고 있음

* for-in과 for-of의 차이

  * For-in : 오브젝트에서 열거 가능한 프로퍼티가 대상
  * For-of : 이터러블 오브젝트가 대상
    * property에 연결된 대상은 제외