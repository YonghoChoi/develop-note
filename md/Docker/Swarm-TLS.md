## 인증기관 (CA) 서버 만들기

1. CA 서버로 사용할 호스트에서 root 계정으로 로그인

   ```
   $ sudo su
   ```

2.  `ca-priv-key.pem` 라는 이름으로 CA에서 사용할 private key 생성:

   ```shell
   # openssl genrsa -out ca-priv-key.pem 2048
   Generating RSA private key, 2048 bit long modulus
   ...........................................................+++
   .....+++
   e is 65537 (0x10001)
   ```

3.  `ca.pem` 라는 이름으로 CA에서 사용할 public key 생성.

   이는 이전에 생성한 private key를 기반으로 생성된다.

   맥을 사용하는 경우 openssl.cnf의 위치 : `/System/Library/OpenSSL/openssl.cnf`

   ```shell
   # openssl req -config /usr/lib/ssl/openssl.cnf -new -key ca-priv-key.pem -x509 -days 1825 -out ca.pem
   You are about to be asked to enter information that will be incorporated
   into your certificate request.
   What you are about to enter is what is called a Distinguished Name or a DN.
   There are quite a few fields but you can leave some blank
   For some fields there will be a default value,
   If you enter '.', the field will be left blank.
   -----
   Country Name (2 letter code) [AU]:US
   <output truncated>
   ```

아래와 같이 각각 생성된 키를 검증할 수 있다.

```
# openssl rsa -in ca-priv-key.pem -noout -text
```

```
# openssl x509 -in ca.pem -noout -text`
```

아래의 명령을 통해 CA의 public key에 대한 정보를 확인할 수 있다.

```
# openssl x509 -in ca.pem -noout -text
Certificate:
    Data:
        Version: 3 (0x2)
        Serial Number: 17432010264024107661 (0xf1eaf0f9f41eca8d)
    Signature Algorithm: sha256WithRSAEncryption
        Issuer: C=US, ST=CA, L=Sanfrancisco, O=Docker Inc
        Validity
            Not Before: Jan 16 18:28:12 2016 GMT
            Not After : Jan 13 18:28:12 2026 GMT
        Subject: C=US, ST=CA, L=San Francisco, O=Docker Inc
        Subject Public Key Info:
            Public Key Algorithm: rsaEncryption
                Public-Key: (2048 bit)
                Modulus:
                    00:d1:fe:6e:55:d4:93:fc:c9:8a:04:07:2d:ba:f0:
                    55:97:c5:2c:f5:d7:1d:6a:9b:f0:f0:55:6c:5d:90:
<output truncated>
```



## 키 서명

CA 서버가 만들어졌으면 이제 원격지 서버에 대한 키 쌍을 만들어야 한다. 여러 대의 서버가 존재하더라도 키 쌍을 만드는 과정은 동일하다.

|                   |                                          |
| ----------------- | ---------------------------------------- |
| `ca-priv-key.pem` | CA의 개인 키이므로 보안을 유지해야한다. 다른 환경의 노드에 대한 새 키에 서명하는데 사용된다. `ca.pem` (공개키)파일 과 함께 CA의 키 쌍을 구성한다. |
| `ca.pem`          | CA의 공개 키 (인증서라고도 함). 이는 모든 노드에 설치되므로 모든 노드가 CA가 서명 한 인증서를 신뢰한다. `ca-priv-key.pem`(개인키) 파일과 함께 CA의 키 쌍을 구성한다. |
| `*node*.csr`      | 인증서 서명 요청 (CSR). CSR은 사실상 특정 노드에 대한 새로운 키 쌍을 생성하는 CA에 대한 응용 프로그램이다. CA는 CSR에서 제공된 정보를 가져와 해당 노드에 대한 공개 키와 개인 키 쌍을 생성한다. |
| `*node*-priv.key` | CA가 서명 한 개인 키. 노드는 이 키를 사용하여 원격 Docker 엔진으로 자신을 인증한다. `*node*-cert.pem`파일 과 함께 노드의 키 쌍을 구성한다. |
| `*node*-cert.pem` | CA가 서명 한 인증서. `*node*-priv.key`파일과 함께 노드의 키 쌍을 구성한다. |

이제 각 노드들을 위한 키를 생성하도록 한다.

1. CA 서버 역할을 하는 호스트의 터미널에 root 권한으로 로그인한다.

   ```shell
   $ sudo su
   ```

2. Swarm Manager를 위한 `swarm-priv-key.pem`(개인 키) 만들기

   ```shell
   # openssl genrsa -out swarm-priv-key.pem 2048
   Generating RSA private key, 2048 bit long modulus
   ............................................................+++
   ........+++
   e is 65537 (0x10001)
   ```

3. 이전 단계에서 작성한 개인 키를 사용하여 CSR (Certificate Signing Request)  `swarm.csr`을 생성한다.

   ```shell
   # openssl req -subj "/CN=swarm" -new -key swarm-priv-key.pem -out swarm.csr
   ```

   여기서는 데모용이므로, 실제 CSR을 만드는 과정은 다를 수 있다.

   CN은 Common Name으로 사용자의 이름이나 회사 이름과 같은 것을 설정하면 된다.

4. 이전 단계에서 만든 CSR을 기반으로 `swarm-cert.pem`(인증서)를 만든다 .

   ```shell
   # openssl x509 -req -days 1825 -in swarm.csr -CA ca.pem -CAkey ca-priv-key.pem -CAcreateserial -out swarm-cert.pem -extensions v3_req -extfile /usr/lib/ssl/openssl.cnf
   <snip>
   # openssl rsa -in swarm-priv-key.pem -out swarm-priv-key.pem
   ```

5. 위의 과정을 반복하여 모든 노드에 대한 키와 서명 파일을 생성한다.

6. 이제 만들어진 파일들을 각 노드에 scp명령을 사용하여 복사한다. 

   * docker-machine을 사용하는 경우 `docker-machine scp`를 사용
   * 여기서는 매니저가 될 호스트인 swarm과 매니저에 조인할 node1, node2로 구성한다.

7. 각 노드가 이 키 파일들을 사용하도록 `DOCKER_OPTS`에 옵션을 설정해준다. 

   ```shell
   -H tcp://0.0.0.0:2376 --tlsverify --tlscacert=/home/ubuntu/.certs/ca.pem --tlscert=/home/ubuntu/.certs/cert.pem --tlskey=/home/ubuntu/.certs/key.pem
   ```

   * docker-machine을 사용하는 경우 `var/lib/boot2docker/profile`파일을 수정한다.

     ```
     CACERT=/home/docker/.certs/ca.pem
     DOCKER_HOST='-H tcp://0.0.0.0:2376'
     DOCKER_STORAGE=aufs
     DOCKER_TLS=auto
     SERVERKEY=/home/docker/.certs/key.pem
     SERVERCERT=/home/docker/.certs/cert.pem
     ```

   * 적용을 위해 docker-machine 내의 docker daemon을 재시작 한다.

     ```shell
     $ sudo /etc/init.d/docker restart
     ```

8. 이제 manager가 되는 호스트(여기서는 swarm 노드)에서 클러스터를 구성한다.

   * 먼저 클러스터를 생성하고 토큰 값을 환경변수에 등록한다.

     ```shell
     $ export TOKEN=$(docker run --rm swarm create)
     ```

   * node1과 node2에 조인한다.

     ```shell
     $ docker run -d swarm join --addr=node1:2376 token://$TOKEN
     $ docker run -d swarm join --addr=node2:2376 token://$TOKEN
     ```

9. TLS를 사용하여 Swarm Manager를 구동시킨다.

   ```shell
   $ docker run -d -p 3376:3376 -v /home/docker/.certs:/certs:ro swarm manage --tlsverify --tlscacert=/certs/ca.pem --tlscert=/certs/cert
   .pem --tlskey=/certs/key.pem --host=0.0.0.0:3376 token://$TOKEN
   ```

10. 제대로 구성이 잘 되었는지 확인해보기 위해 아래 명령을 수행해 본다.

    ```shell
    $ docker --tlsverify --tlscacert=/home/docker/.certs/ca.pem --tlscert=/home/docker/.certs/cert.pem --tlskey=/home/docker/.certs/key.pe
    m -H swarm:3376 version
    ```

    ​