# Docker

## Command

* run : 이미지를 컨테이너로 실행한 뒤 명령 수행

  ```shell
  docker run <옵션> <이미지 이름> <실행할 파일>
  ```

  ```shell
  # -i : interactive, -t : Pseudo-tty
  # ubuntu 이미지를 test라는 이름의 컨테이너로 생성한 뒤 ubuntu 이미지 안의 /bin/bash를 실행.
  $ docker run -it --name test ubuntu /bin/bash
  ```

  ```shell
  # -d : background run, -p : port forwarding
  $ docker run -d -p 8080:8080 tomcat
  ```
  ​

* attach : 해당 이름의 docker 컨테이너로 접속

  ```shell
  $ docker attach CONTAINER_ID
  ```





* logs : docker 컨테이너의 로그 출력

  ```shell
  # -f : polling
  $ docker logs -t CONTAINER_ID
  ```

  * 톰캣 이미지를 백그라운드에서 수행 후 logs 명령으로 로그를 확인할 수 있다.  

    ​


* exec : docker 컨테이너에서 명령 수행


  ```shell
  # interactive 가능한 tty로 /bin/bash 수행
  $ docker exec -it CONTAINER_ID /bin/bash
  ```

  * 톰캣 이미지를 받아 실행하면 바로 실행되며 로그가 출력 되는데 exec 명령을 수행하여 docker 컨테이너에 접속해서 설정을 편집할 수 있다.



* restart : 해당 이름의 docker 컨테이너 재시작

  ```shell
  $ docker restart <이름>
  ```





* start/stop : docker 컨테이너 시작/중지

  ```shell
  $ docker start CONTAINER_ID
  ```

  ```shell
  $ docker stop CONTAINER_ID
  ```

  ​

* build : 지정된 경로의 Dockerfile 기반으로 이미지 생성.

  ```shell
  $ docker build <옵션> <Dockerfile 경로>
  ```

  ```shell
  $ docker build --tage hello:0.1 .
  ```
  ​

* commit : 컨테이너에 변경된 내용을 이미지에 반영. (새로운 이미지가 생성됨)

  ```shell
  $ docker commit CONTAINER_ID NEW_IMAGE_NAME
  ```

  * 기본적으로 pull을 해서 사용하는 도커 이미지에 사용자가 커스터마이징을 하려면 먼저 이미지를 통해 컨테이너를 실행 시키고 해당 컨테이너에 각종 설정을 수행한 후 docker commit 명령을 이용하여 svn이나 git을 사용하듯이 변경 된 내용을 해당 이미지에 적용할 수 있다. 
  * 적용된 이미지를 docker hub에 올려 공유한다면 어디서든 같은 환경의 서버를 구축할 수 있게된다.




* push : commit 명령으로 변경된 이미지를 docker hub에 업로드

  ```shell
  $ docker push IMAGE_NAME
  ```

  * 이미지명이 docker hub의 repository 이름이 되므로 이름이 맞지 않는 경우에는 docker tag 명령으로 이름을 설정 후 push를 한다.



## Docker Compose



### Docker Compose 수행 절차

1. Dockerfile 정의.
2. docker-compose.yml 파일에 앱을 구성하는 서비스들을 정의.
   * 독립된 환경에서 각 서비스들이 함께 동작됨.
3. docker-compose up 수행.



### docker-compose.yml 파일의 예



```yaml
version: '2'
services:
  web:
    build: .
    ports:
    - "5000:5000"
    volumes:
    - .:/code
    - logvolume01:/var/log
    links:
    - redis
  redis:
    image: redis
volumes:
  logvolume01: {}
```



* 정의 된 서비스는 web과 redis 두개 이다.
* docker-compose up 을 수행하면 현재 디렉토리 경로의 Dockerfile을 build 한다.
* web 서비스의 5000번 port가 호스트의 5000번 port로 포워딩 된다.
* docker의 /code가 호스트의 현재 경로로 마운트 되고, docker의 /var/log가 호스트의 logvolume01로 마운트 된다.
* web 서비스는 redis 서비스로 링크된다.
  * links를 지정한 서비스에서 해당 이름으로 접근이 가능. (ip 주소가 해당 이름으로 매핑)
  * redis:redis-1 과 같이 이름을 줄 수도 있음.
  * 위의 내용을 예로 들면 web container에서 ping redis 명령으로 redis container와 연결이 되었는지 확인해볼 수 있다.
* redis는 이미지 버전을 따로 명시하지 않았기 때문에 latest 버전으로 pull 된다.



### docker-compose 사용



* 실행

  ```shell
  # .yml 파일이 있는 위치에서 실행해야 한다.
  # -d : 백그라운드 실행
  $ docker-compose up -d
  ```

* 종료

  ```shell
  $ docker-compose stop
  ```

  ​

* docker-compose로 컨테이너를 동작시키면 이후 부터 docker 명령으로 기존과 동일하게 컨테이너를 관리할 수 있다.



## ps 및 images 제거

### Linux or Mac

* 현재 떠 있는 모든 컨테이너 stop 시키기

  ```shell
  $ docker stop $(docker ps -q)
  ```

* 종료한 컨테이너들 제거

  ```shell
  $ docker rm $(docker ps --filter=status=exited --filter=status=create -q)
  ```

* 쓰이지 않는 이미지들(dangling) 제거

  ```shell
  $ docker rmi $(docker images -a --filter=dangling=true -q)
  ```

* 모든 이미지 제거

  ```shell
  $ docker rmi $(docker images -a -q)
  ```

### Windows

* 현재 떠 있는 모든 컨테이너 stop 시키기

  ```shell
  > FOR /f "tokens=*" %i IN ('docker ps -q') DO docker stop %i
  ```

* 종료한 컨테이너들 제거

  ```shell
  > FOR /f "tokens=*" %i IN ('docker ps -a -q') DO docker rm %i
  ```

* 쓰이지 않는 이미지들(dangling) 제거

  ```shell
  > FOR /f "tokens=*" %i IN ('docker images -a --filter=dangling=true -q') DO docker rmi %i
  ```

* 모든 이미지 제거

  ```shell
  > FOR /f "tokens=*" %i IN ('docker images -a -q') DO docker rmi %i
  ```

## Dockerfile


* EXPOSE : 외부에서 연결할 수 있도록 port 오픈

  ```shell
  EXPOSE 3306
  ```
  ​

* Dockerfile을 수정한 후에는 반드시 docker build 명령을 수행하여 변경된 내용을 빌드한후 run 해야 적용된다.




### Docker 이미지 Dockerhub에 push 하기

먼저 http://hub.docker.com 에 계정이 생성되어 있어야 하고, push 할 저장소가 생성되어야 한다. 저장소는 hub.docker.com 페이지에서 로그인 후 간단하게 생성가능하다. 

push를 하기 위해서는 로컬에서 docker로 로그인이 되어 있어야 하는데 docker login 명령으로 login을 할 수 있다. 이 후 docker tag 명령으로 저장소와 같은 이름으로 push할 이미지를 tag하고 push를 수행하면 끝. 



요약하면

1. http://hub.docker.com 로그인 후 저장소 생성

2. 로컬에서 docker에 로그인

   ```shell
   $ docker login
   ```

3. tag 명령으로 이미지명을 저장소 명과 동일하게 맞춤.

   ```shell
   $ docker tag <image 명> <저장소경로>
   ```

4. push 수행

   ```shell
   $ docker push <tag명>
   ```


## 개발환경 셋팅

### jenkins

* 젠킨스를 docker 이미지로 받아 실행하면 보안상의 이슈로 인하여 root 계정을 사용하지 못하고 jenkins 계정만 사용이 가능하도록 Dockerfile에 설정되어 있다. 그래서 apt 명령을 수행하지 못하므로 설치해야할 프로그램이 있는 경우 Dockerfile을 이용하여 이미지가 만들어지는 과정에서 설치를 수행해야 한다.

* jenkins 설정을 한 후 item을 생성하여 작업을 한 후 docker commit 명령으로 변경된 내용으로 새로운 이미지를 만든 후 다시 docker run을 해보면 내용이 남지 않고 초기화 된다. 그 이유는 jenkins Dockerfile을 살펴보면 알 수가 있는데 /var/jenkins_home 디렉토리가 VOLUME으로 지정되어 있어서 컨테이너에 저장되지 않고 로컬에 저장되기 때문이다. 

* Dockerfile에 VOLUE으로 지정했는데 docker run 시 -v 옵션으로 로컬 경로와 매핑을 해주지 않는 경우 호스트의 "/var/lib/jenkins/volumes/MOUNT_NAME/_data" 경로가 docker 내 볼륨과 매핑이 된다.(docker inspect 명령을 통해 경로 확인 가능)


## Tips

### WIndows에서 호스트(Hyper-v) 머신에 접근하는 방법

```shell
#get a privileged container with access to Docker daemon
docker run --privileged -it --rm -v /var/run/docker.sock:/var/run/docker.sock -v /usr/bin/docker:/usr/bin/docker -v /var/lib:/var/lib alpine sh
```
