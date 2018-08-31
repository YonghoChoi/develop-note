## Iteration

* 반복을 의미
* 반복하기 위한 프로토콜 필요
  * 프로토콜은 규약을 의미
* 반복을 처리하기 위한 함수를 가지고 있는 오브젝트여야 함
* 구성
  * iterable 프로토콜
  * Iterator 프로토콜
* 빌트인 오브젝트는 기본적으로 이터러블 프로토콜을 가지고 있기 때문에 반복 가능
  * String, Array, TypedArray, Map, Set
  * Arguments, DOM NodeList
* 이터러블 오브젝트는 빌트인 오브젝트 외에 이터러블 프로토콜이 설정된 오브젝트를 의미
* 이터러블 오브젝트 조건
  * Symbol.iterator()가 있어야 함
    * 실제로 반복을 수행하는 메소드
  * Array를 상속받으면 이터러블 오브젝트가 됨
  * 개발자 코드로 이터러블 프로토콜 정의 가능



### iterator protocol

* iterator의 next 메소드를 통해 값 순회
  * 반환 값은 {value: 1, done : false}의 형태
    * value에 undefined가 들어갈 수 있기 때문에 value로 반복의 끝을 판단하면 안됨
    * done이 true가 되면 반복이 끝