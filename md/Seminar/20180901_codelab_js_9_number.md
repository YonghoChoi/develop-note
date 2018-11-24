# Number

* 자바스크립트는 IEEE 754에 정의된 double-precision floating-point format numbers로 숫자 표시

  * 변수 생성 시 타입 지정이 없는 자바스크립트는 엔진이 알아서 소수인지 정수인지 판단
  * 64비트 유동 소수점 형태로 수를 표시
    * RGB 표현의 경우에는 1바이트만으로도 충분하게 표현이 가능한데 64비트는 8바이트이기 때문에 7바이트가 낭비됨
      * 이를 방지위해 typed array가 등장함
      * 숫자 표현의 경우에는 typed array 사용 권장

* safe integer란

  * 지수(e)를 사용하지 않고 나타낼 수 있는 값까지만 표현
    * Number.MAX_SAFE_INTEGER
    * Number.MIN_SAFE_INTEGER
  * 2의 53승 보다 큰 값(Number.MAX_SAFE_INTEGER)의 경우 지수표현

* Number.EPSILON : 미세한 값 차이로 인해 일치되지 않을 때 사용

  * 예를 들어 0.1 + 0.2는 0.3이어야 하는데 실제 값은 0.30000000000000004가 되어서 0.1 + 0.2 === 0.3 이 false가 됨

* 8진수 표현시 ES5에서는 첫자리에 영문 o/O를 작성했었음. 다른 진수 표현(2진수의 경우 0b0101과 같이 표현)과 달라서 혼동되는 문제가 발생

  * ES6에서는 영문자 o/O 앞에 숫자 0 추가

* isNaN()

  * NaN === NaN의 결과가 false
    * NaN은 자바스크립트에서 값임 (숫자가 아니라는 것을 나타내는 값)
    * 값 비교인데 false가 반환되었기 때문에 논쟁이 있었음
  * 이 후 isNaN(), Number.isNaN(), Object.is(NaN, NaN) 이 출현함
    * isNaN()은 글로벌 오브젝트
      * Number에 의존하고 있는 기능을 글로벌 오브젝트에 포함시킨 것은 잘못된 것이라 생각
    * ES6에서 Number.isNaN()이 등장
      * Number에 포함되면서 제자리를 찾음
      * 사용 권장

* Tip

  ```
  빌트인 오브젝트에 내장된 타입들의 프로퍼티나 메소드에 대해서는 외우지말고 MDN 참조해서 필요한 것을 찾아 사용하자.
  - 각 함수 또는 메소드마다 사용 방법이나 반환 값이 미세하게 다를 수 있기 때문에 기억에 의존하지 말고, 정확히 확인해서 사용.
  ```

* isInteger()

  * 소수점 뒷자리가 0인 경우에도 정수로 판단
    * 1.0도 정수로 판단

* isSafeInteger() : 2의 53승 범위 내의 정수인지 확인

* isFinite() : 파라미터가 유한 값인지 확인

  * 처음에 글로벌 오브젝트에 포함됨
  * ES6에서 Number의 메소드로 포함됨
    * 글로벌 오브젝트와 결과가 다름
    * Number.isFinite() 사용 권장



# Math

* Math.trunc() : 소수를 제외한 정수 반환

  * 양수이면 Math.floor()와 같음
  * 음수이면 Math.ceil()과 같음

* 32비트 계산 관련

  * 이전에 C++로 작성된 게임의 경우에는 32비트 정수를 사용하기 때문에 자바스크립트와 호환이 되지 않아서 웹에서 동작하기가 어려웠음
  * 웹 어셈블리를 통해 C++ 바이너리를 컨버팅 할 수 있게 되면서 일부 자바스크립트로 제어가 가능해짐
  * 이 때 32비트와 맞추기 위해 Math.clz32()와 Math.fround() 사용
  * Emscripten 참고

  