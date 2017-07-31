## private registry 구성

테스트용으로 구성해본 private registry 이므로 도메인과 SSL 인증서가 없다는 가정하에 진행하도록 한다. 도메인이 있는 경우 /etc/hosts 파일에 도메인을 지정하는 작업을 생략해도 된다. 

인증서 발급과정과 설치 과정은 [가장 빨리 만나는 docker](http://www.pyrasis.com/book/DockerForTheReallyImpatient/Chapter06/01/04)의 내용을 참고 하였다.



## 테스트용 도메인 설정 및 인증서 생성

1. /etc/hosts 파일을 수정하여 도메인 추가

   ```shell
   $ vi /etc/hosts

   127.0.0.1       localhost
   127.0.1.1       hiveDev2
   192.168.10.211  registry.hive.com

   # The following lines are desirable for IPv6 capable hosts
   ::1     localhost ip6-localhost ip6-loopback
   ff02::1 ip6-allnodes
   ff02::2 ip6-allrouters
   ```

2. 서버의 개인 키 생성

   ```shell
   $ openssl genrsa -out server.key 2048
   ```

3. 인증서 서명 요청

   ```shell
   $ openssl req -new -key server.key -out server.csr
   ```

   * 여기서 주의할 점은 Common Name을 입력하는 부분에 hosts에 지정했던 도메인을 입력해야 한다. 이 내용이 다를 경우 제대로 동작하지 않음.

4. 서버의 공개 키 생성

   ```shell
   $ openssl x509 -req -days 365 -in server.csr -signkey server.key -out server.crt
   ```

5. 인증서 설치

   ```shell
   $ sudo cp server.crt /usr/share/ca-certificates/
   $ echo "server.crt" | sudo tee -a /etc/ca-certificates.conf
   $ sudo update-ca-certificates
   ```

6. 도커 서비스 재시작

   ```shell
   $ sudo service docker restart
   ```

registry에 접속해야할 클라이언트들에서도 5,6번 과정을 동일하게 수행해야 한다.

윈도우의 경우 가상 머신을 사용하기 때문에 위와 동일한 과정으로는 불가능하다. 그러므로 아래 클라이언트가 윈도우인 경우 인증서 설치 과정을 참고하여 인증서를 설정한다.



### htpasswd를 사용하여 사용자 인증

이제 registry를 구동시킬 호스트에서 사용자가 로그인 할 수 있도록 하기 위해 apache2-utils의 htpasswd를 사용한다.

1. 설치

   ```shell
   $ sudo apt-get install apache2-utils
   ```

2. 사용자 계정 생성

   ```shell
   $ htpasswd -c .htpasswd yongho
   New password:<비밀번호 입력>
   Re-type new password:<비밀번호 입력>
   Adding password for user yongho
   ```



### registry 컨테이너와 https를 위한 nginx 컨테이너 구동

1. nginx config 파일 작성

   nginx 이미지로 컨테이너를 생성하면 /etc/nginx/conf.d/default.conf 파일에 server 설정을 한다. 호스트에 아래의 설정으로 파일을 생성한다. (컨테이너 생성시 마운트해서 사용)

   ```
   worker_processes  1;

   events {
       worker_connections  1024;
   }

   http {
       server {
           listen       443;
           server_name  registry.hive.com;

           ssl on;
           ssl_certificate /etc/server.crt;
           ssl_certificate_key /etc/server.key;

           proxy_set_header Host           $http_host;
           proxy_set_header X-Real-IP      $remote_addr;
           proxy_set_header Authorization  "";

           client_max_body_size 0;

           chunked_transfer_encoding on;

           location / {
               proxy_pass          http://registry-hive:5000;
               proxy_set_header    Host  $host;
               proxy_read_timeout  900;

               auth_basic            "Restricted";
               auth_basic_user_file  .htpasswd;
           }
       }
   }
   ```

2. registry 컨테이너 구동

   ```shell
   $ sudo docker run -d --name registry-hive \
       -v /tmp/registry:/tmp/registry \
       registry:0.8.1
   ```

   * registry의 최신 버전으로 사용할 경우 404 에러가 발생할 수 있으므로 여기서 테스트한 내용을 적용하기 위해서는 위의 버전을 사용해야 한다.

3. nginx 컨테이너 구동

   ```shell
   $ sudo docker run -d --name nginx-registry \
       -v ~/nginx.conf:/etc/nginx/nginx.conf \
       -v ~/.htpasswd:/etc/nginx/.htpasswd \
       -v ~/server.key:/etc/server.key \
       -v ~/server.crt:/etc/server.crt \
       --link registry-hive:registry-hive \
       -p 443:443 \
       nginx
   ```

   * link에 지정된 이름은 nginx 설정에 포함되어 있으므로 네이밍이 다르면 안됨.

4. 로그인 테스트

   ```shell
   $ docker login registry.hive.com
   ```

   ​

### 클라이언트가 윈도우인 경우 인증서 설치

윈도우에서는 docker가 가상머신에서 돌고 있기 때문에 호스트에 인증서를 설치하는 것이 아니라 docker-machine에 설치를 수행해야 한다.

1. 인증서 복사

   ```shell
   $ docker-machine scp server.crt default:/home/docker/
   ```

   * server.crt 파일이 있는 위치에서 scp 명령으로 docker가 구동중인 머신으로 복사한다.

2. 도커 머신에 접속

   ```shell
   $ docker-machine ssh default
   ```

3. cert 파일을 저장할 디렉토리를 registry 도메인 명으로 생성 후 복사

   ```shell
   $ mkdir /etc/docker/certs.d
   $ mkdir /etc/docker/certs.d/registry.hive.com
   $ cp /home/docker/server.crt /etc/docker/certs.d/registry.hive.com/ca.cert
   ```

   * cert 파일의 이름을 ca.cert로 지정해야 함.

4. 도커 머신 재시작

   ```shell
   $ docker-machine restart default
   ```

5. registry 로그인

   ```shell
   $ docker login registry.hive.com
   ```




