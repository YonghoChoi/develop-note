# Map

```javascript
const newMap = new Map([["key1", "value1"]]);
```

* Object와 같이 key, value 형태로 저장되지만 key로 인스턴스를 지정할 수 있음

  * key가 인스턴스의 메모리 주소가 되는 것임

    ```javascript
    const newMap = new Map();
    newMap.set({}, "test1");
    newMap.set({}, "test2");
    ```

    * 두 {}의 주소값이 다르기 때문에 각각 set 됨

* ES5에서의 Object는 key 값을 만든 순서대로 읽혀지는 것이 보장되지 않음

  * Map은 for-in 문 사용할 경우 만든 순서대로 읽혀짐
  * 배열 형태로 들어가기 때문에 순서대로 읽혀지는게 가능

* 배열 형태로 들어가기 때문에 forEach, filter, for-in 등등 다양하게 사용가능

  * forEach

    ```javascript
    newMap.forEach((value, key, map) => {
       console.log(key, ":", value) 
    });
    ```

* Tip : 강사님은 함수를 외부로 노출하지 않음. class 내에서 사용하거나 Singleton 오브젝트 내에서 사용

  * this를 중요하게 생각함
  * 이벤트에는 bind 함수를 사용



## WeakMap

* object만 key값으로 사용 가능

  * primitive 타입은 사용 불가능

* Map에서는 object를 키로 사용하는 경우 key를 잃어버리면 메모리에 남게됨 (memory leak)

  * 로컬 변수에 오브젝트를 할당하고 map에 추가한 후 로컬 변수가 제거되면 다음번에 map에서 key로 참조할 수 없게 됨

* WeakMap은 키로 사용 중인 오브젝트를 참조할 수 없게 되면 자동으로 GC

* 메모리 효율 향상이 목적

* 제공되는 메소드도 제한적

  * set, get, has, delete (CRUD)

* 아래 코드로 테스트 해보니 설명대로 동작 안함... (확인 필요)

  ```javascript
  const newMap = new Map();
  (function() {
      const obj = {key: "value"};
      newMap.set(obj, "GC");
  }());
  
  const newWeakMap = new WeakMap();
  (function() {
      const obj = {key: "value"};
      newWeakMap.set(obj, "GC");
  }());
  
  setInterval(function() {
      console.log(newWeakMap);
      console.log(newMap);
  }, 1000);
  ```

  * newWeakMap 에 키로 저장된 obj가 제거될 것으로 예상했지만 그대로 남아있음.
  * setInterval로 기다려봤는데도 제거가 안됨
  * GC 시간이 안되어서 계속 남아있는 것으로 예상하지만 확실치 않음.

