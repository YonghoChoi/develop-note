# Tensorflow 설치

## Docker

* 컨테이너 구동

  ```
  docker run -d -p 8888:8888 -p 6006:6006 --name tensorflow gcr.io/tensorflow/tensorflow
  ```

* 주피터 노트북 실행

  1. 아래 주소로 접속

     ```
     http://localhost:8888
     ```

  2. 페이지에 출력된 가이드에 따라 콘솔창에서 아래 명령 수행

     ```
     jupyter notebook list
     ```

  3. 실행 결과로 출력된 URL로 token과 함께 브라우저에서 접속

     ```
     http://localhost:8888/?token=87ef3deceb1ef78a6c9c445f6b8816319bb5b6b3c12bc62b
     ```

     ​

