# Class

* Function 오브젝트가 바탕
  * 별도로 class가 존재한다기 보다 function을 조금 더 객체지향적으로 사용할 수 있게끔 만들었다고 생각하면 좋을 듯
  * 객체 지향에서 사용하는 Syntax 추가
    * static, super
  * 자바스크립트의 객체지향은 C++이나 자바와 같은 기본적인 객체지향의 개념이라기 보다는 기존과 동일하게 prototype을 기반으로 한다.
    * 스펙의 Object 절 참고



## class 선언문

```javascript
window.onload = function() {
    class Member {
        getName() {
            return "이름";
        }
    }

    const obj = new Member();
    console.log(obj.getName());
};
```

* 기존에 생성자 역할을 하는 function을 정의한 경우 prototype을 정의하고 new 연산자로 인스턴스로 생성할 경우 `__proto__` 프로퍼티 하위에 prototype에 정의한 것들을 할당하게 되는데 class를 사용할 경우 prototype 정의 없이 메소드 선언만 해도 엔진이 알아서 `__proto__` 하위에 메소드를 할당한다.

* Member class의 내용을 확인해보면 function으로 정의한 것과 동일한 구조를 가진다.

  ```javascript
  const Member = class {
  	getName() {
  		return "이름";
  	}
  }
  ```

  * Member:class

    * arguments:(...)

    * caller:(...)

    * length:0

    * name:"Member"

    * prototype:

      * constructor:class
      * getName:ƒ getName()
      * `__proto__`:Object

      

* Class도 function 정의와 동일하기 때문에 property에 메서드를 직접 접근해서 추가가 가능하다.

* prototype에 메소드를 추가하면 모든 인스턴스에서 공유한다.

* Class 정의는 Window 오브젝트에 설정되지 않는다. 즉, 글로벌 오브젝트에 포함되지 않는다.



## constructor

* constructor를 작성하지 않으면 디폴트 constructor가 호출됨
* 빌트인 오브젝트를 반환하는 경우 이를 무시하고 Class의 오브젝트를 반환
* 빌트인 오브젝트 외 오브젝트를 반환할 경우 해당 오브젝트가 반환됨. (주의)

```javascript
class Member {
    constructor(name) {
        this.name = name;
    }
}
```

* 기존에는 constructor가 prototype 하위에 가려져 있었는데, class 사용 시에는 밖으로 드러남



## getter

```javascript
class Member {
    get getName() {
        return "이름";
    }
};

const obj = new Member();
// console.log(obj.getName());	// 오류 발생
console.log(obj.getName);
```

* 메소드 선언 시 get 명시
* obj.getName()으로 호출하면 에러남
* obj.getName으로 호출해야함



## setter

```javascript
class Member {
    get getName() {
        return this.name;
    }

    set setName(param) {
        this.name = param;
    }
};

const obj = new Member();
obj.setName = "이름변경";
console.log(obj.getName);
```

* Class 멤버 변수(프로퍼티) name을 선언하지 않더라도 존재하지 않으면 this.name으로 프로퍼티가 추가됨



## 상속

* ES5에서는 prototype에 Object.create를 사용하여 상속 받을 super 클래스를 할당하는 방식으로 상속

  ```javascript
  Soccer.prototype = Object.create(Sports.prototype, { ... 메소드 선언 생략 ... });
  Soccer.prototype.constructor = Soccer; // Object.create로 인해 constructor가 제거되기 때문에 다시 할당
  ```

* ES6에서는 extends 키워드로 상속 구현

  ```javascript
  class subClass extends superClass {}
  ```

* ES6 상속 예제

  ```javascript
  class Sports {
      constructor(member) {
          this.member = member;
      }
  
      setItem(item) {
          this.item = item;
      }
  }
  
  class Soccer extends Sports {
      setGround(ground) {
          this.ground = ground;
      }
  }
  
  const obj = new Soccer("박지성");
  obj.setItem("축구");
  obj.setGround("상암");
  console.log(obj.member);
  console.log(obj.item);
  console.log(obj.ground);
  ```

* 상속 구조

  * obj:Soccer
  * ground:"상암"
  * item:"축구"
  * member:"박지성"
  * `__proto__`:Sports
    * constructor:class Soccer
    * setGround:ƒ setGround(ground)
    * setItem:ƒ setItem(item)
    * `__proto__`:
      * constructor:class Sports
      * setItem:ƒ setItem(item)
      * `__proto__`:Object

* 같은 이름의 메소드가 존재하는 경우 상속 구조에서 Sub 클래스를 우선적으로 메소드를 찾음.

  * Soccer 클래스 > Sports 클래스 > Object 클래스

* super 키워드 사용 방법은 자바와 동일

* 빌트인 오브젝트 상속도 가능

* Object.setPrototypeOf를 사용해서 상속을 구현할 수 있음

  ```javascript
  Object.setPrototypeOf(Soccer, Sports)
  ```

* ES6는 OOP 구현이 기반을 제공
* OOP는 설계가 필요



## static

* class에 정적 메소드 선언

* prototype에 연결되지 않고 class에 직접 연결됨

* 인스턴스에서 접근이 불가능하고 Class 명을 통해서 접근

  ```javascript
  class Sports {
      static getItem() {
          return "sports";
      }
  }
  
  console.log(Sports.getItem());
  ```

* 코딩하다보니 인스턴스로 들어갈 메소드와 static 메소드의 구분이 어려워짐.

  * 문법적인 부분은 아님
  * 그래서 static 메소드만 모아 놓은 오브젝트를 따로 만들어서 사용하는 것도 괜찮은 방법인 듯

* static 메소드 내의 this는 해당 클래스를 나타냄

  * this로 변수에 접근하면 이는 인스턴스화되지 않는 클래스 내의 프로퍼티가 됨



## hoisting

* class는 호이스팅이 되지 않음

* 즉, class 선언문보다 먼저 사용할 수 없음

* 메소드명에 변수를 사용할 수 있음

  * 대괄호로 표현

    ```javascript
    class Sports {
        static ["get" + Type]() {
            
        }
    }
    ```

    

## DOM Interface 상속

```javascript
class ExtendsImage extends Image {
    constructor() {
        super();
    }
    
    setProperty() {
        this.src = "file/rainbow.jpg";
        this.alt = "그림 설명";
        this.title = "무지개";
    }
}

const obj = new ExtendsImage();
obj.setProperty();
document.querySelector("body").appendChild(obj);
```

* DOM Interface를 상속받아 Custom 한 오브젝트를 만들 수 있음
* 복잡하게 DOM 코딩하지 말고, 이런 방식으로 컴포넌트화 하면 깔끔



