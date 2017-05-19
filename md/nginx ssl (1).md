* nginx 설치

  ```shell
  $ apt-get install update
  $ apt-get install nginx
  ```

* /etc/nginx/sites-enabled/default 수정

  ```shell
  location / {
  	proxy_set_header   X-Real-IP $remote_addr;
  	proxy_set_header   Host      $http_host;
  	proxy_pass         http://127.0.0.1:8080;

  	try_files $uri $uri/ =404;
  }
  ```

  * proxy_pass에 tomcat 서버 URL 설정

* nginx 재시작

  ```shell
  $ sudo service nginx restart
  ```







* gcloud SDK 설치

* gcloud init 수행

* compute engine instance 정보 확인

  ```shell
  $ gcloud compute instances list
  ```

  ​

* gcloud compute copy-files 수행

  ```shell
  $ gcloud compute copy-files <복사할로컬파일> <계정명>@<인스턴스명>:<원격지경로> --zone <ZONE>
  ```

  ​

인증서 파일을 복사한 후 nginx 설정을 변경한다.



```shell
server {
        listen 80 default_server;
        listen [::]:80 default_server ipv6only=on;

        # Make site accessible from http://localhost/
        server_name yongho-choi.com;
        rewrite ^ https://$server_name$request_uri;
}


server {
        listen 443;
        server_name yongho-choi.com;

        root html;
        index index.html index.htm;

        ssl on;
        ssl_certificate <인증서 경로>/<파일명>.crt;
        ssl_certificate_key <키 경로>/<키명>.key;

        ssl_session_timeout 5m;

        ssl_protocols SSLv3 TLSv1 TLSv1.1 TLSv1.2;
        ssl_ciphers "HIGH:!aNULL:!MD5 or HIGH:!aNULL:!MD5:!3DES";
        ssl_prefer_server_ciphers on;

        location / {
                try_files $uri $uri/ =404;
        }
}
```



설정 완료 후 nginx를 재시작하면 https로 리다이렉트 된다.



* 정상 동작하는 nginx 설정

  ```
  server {
          listen 80;

          return 301 https://$host$request_uri;
  }


  # HTTPS server
  #
  server {
          listen 443;

          ssl on;
          ssl_certificate /home/yonghogcloud1/ssl/1_yongho-choi.com_bundle.crt;
          ssl_certificate_key /home/yonghogcloud1/ssl/yongho1037.key;

          ssl_session_timeout 5m;

          ssl_protocols SSLv3 TLSv1 TLSv1.1 TLSv1.2;
          ssl_ciphers "HIGH:!aNULL:!MD5 or HIGH:!aNULL:!MD5:!3DES";
          ssl_prefer_server_ciphers on;

          location / {
                  proxy_redirect off;
                  proxy_set_header   X-Real-IP $remote_addr;
                  proxy_set_header   Host      $http_host;
                  proxy_set_header   X-Forwarded-For $proxy_add_x_forwarded_for;
                  proxy_set_header   X-Forwarded-Proto $scheme;
                  proxy_pass         http://172.18.0.2:8080;

          }

          location ~ /\.ht {
           deny  all;
          }
  }
  ```

  ​



![]()

