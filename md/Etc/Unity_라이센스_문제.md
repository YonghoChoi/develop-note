# Unity 라이센스 문제

## 현상

* Unity 로 프로젝트 구동 중 유니티가 그냥 꺼짐

* 정상 동작하는 경우의 로그

  ```
  LICENSE SYSTEM [201777 13:36:22] No start/stop license dates set

  LICENSE SYSTEM [201777 13:36:22] Next license update check is after 2017-06-30T09:28:14

  ... 생략 ...
  ```

* 라이센스 문제 발생 로그

  ```

  LICENSE SYSTEM [2017710 11:50:32] No start/stop license dates set

  LICENSE SYSTEM [2017710 11:50:32] Next license update check is after 2017-02-17T02:06:39


  LICENSE SYSTEM [2017710 11:50:32] 00331-20020-00000-AA371 != 00331-20020-00000-AA290

  ... 생략 ...
  ```

  ​

## 해결

1. 기존 Unity 프로그램 제거
2. C:\Users\사용자\ProgramData\Unity 디렉토리 제거
3. 재설치



## 참고

https://support.unity3d.com/hc/en-us/articles/205698949-I-have-just-installed-Windows-10-Why-is-my-license-failing-to-activate-