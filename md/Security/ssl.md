# 암호화

## SSL

###  대칭키 암호화

```shell
$ openssl enc -e -des3 -salt -in plaintext.txt -out ciphertext.bin
```

* openssl을 이용하여 des3 방식으로 암호화
* plaintext.txt 파일을 암호화하여 ciphertext.bin 파일을 생성
* 복호화를 위해서는 암호화 시 사용한 암호가 필요. 
  * 원격지에 있는 대상에게 암호를 알려줘야 원격지에서 복호화를 할 수 있음.
  * 이 때, 암호를 알리는 과정에서 보안의 취약점 발생



### 공개키 암호화

대칭키 암호화의 취약점을 보완



#### private key 생성

```shell
$ openssl genrsa -out private.pem 1024
Generating RSA private key, 1024 bit long modulus
..................................................++++++
......++++++
e is 65537 (0x10001)
```

* rsa 방식으로 공개키 암호화를 생성
* 마지막 인자의 1024의 수치가 높아질 수록 암호화의 복잡도가 증가. 그에 비례해서 cpu 사용률도 증가.



#### public key 생성

```shell
$ openssl rsa -in private.pem -out public.pem -outform PEM -pubout
writing RSA key
```

* 위에서 만든 private key를 사용하여 public key 생성



#### encrypt

```shell
$ openssl rsautl -encrypt -inkey public.pem -pubin -in plaintext.txt -out encrypttext.ssl
```

* rsa를 사용하여 위에서 생성한 public.pem 공개키로 plaintext.text를 암호화하여 encrypttext.ssl 파일 생성



#### decrypt

```shell
$ openssl rsautl -decrypt -inkey private.pem -in encrypttext.ssl -out decrypttext.txt
```

* rsa를 사용하여 위에서 생성한 private.pem 개인키로 공개키로 암호화했던 encrypttext.ssl 파일을 복호화 하여 decrypttext.txt 파일 생성





## StartSSL



### 가입

* https://startssl.com/ 로 접속

* 가입 절차를 수행하고 나면 .p12 인증서 파일을 다운 받을 수 있게 된다.

  * 이 인증서 파일은 인증서를 관리하기 위해 startssl에 접속할 때 사용되는 것으로 이 인증서가 없으면 로그인이 불가능하다. 그러므로 잘 관리해야함.

* 다운 받은 인증서를 브라우저에 등록한다.

  * 크롬의 경우 설정에 들어가서 인증서 관리에 추가해주면 된다.

* 도메인에 대한 인증 절차 수행

* 이후 Control pannel로 이동해서 private 생성 절차 수행

* 호스트네임 입력. 

  * 상단에 위치한 도메인이 공통 이름이 됨.

* openssl을 사용하여 private 키 생성

  ```shell
  $ openssl req -newkey rsa:2048 -keyout username.key -out username.csr
  ```

* .crt 파일과 .key 파일이 생성되는데 crt 파일의 내용을 등록한다.

* 인증서 등록 완료

  * username.key : 서버쪽 비공개 키
  * username.crt : SSL 디지털 인증서
    * 인증서와 username.key(private key)로 생성한 public key가 포함되어 있음.
  * username.pem : ROOT CA 인증서
  * intermediate.pem : 중계자 인증서

* ​