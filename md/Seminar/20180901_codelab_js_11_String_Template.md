# String

* String.raw : Template와 유사

  * Template과 달리 유니코드 또는 개행과 같은 것도 문자열로 인식

  



# Template

* tagged template

  * template에서 문자열과 값을 구분해서 인자로 전달

  * Template 함수의 첫번째 인자로 문자열 배열, 두번째 인자부터는 값에 매핑됨

    ```
    `1+2=${one + two}이고, 1-2=${one-two}이다.`
    ```

    * 1+2= 는 문자열 배열의 0번 인덱스
    * ${one + two}는 표현식이기 때문에 두번째 인자값에 매핑됨
    * 이고, 1-2= 는 문자열 배열의 1번 인덱스
    * ${one - two}는 표현식이기 때문에 세번째 인자값에 매핑됨
    * 이다. 는 문자열 배열의 2번 인덱스

  * Rest 파라미터 사용 가능

    * function restParam(text, ...values)