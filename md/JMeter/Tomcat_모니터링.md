## Tomcat Heap 사이즈 조절

bin/catalina.sh 파일 내용에서 CATALINA_OPTS 수정

```
CATALINA_OPTS="$CATALINA_OPTS -server -Xms2048M -Xmx2048M -XX:+AggressiveOpts"
```



## Tomcat status 확인을 위해 계정 권한 추가

conf/tomcat-users.xml 파일 내용에 계정 추가

```xml
<role rolename="manager-gui"/>
<role rolename="manager-jmx"/>
<user username="tomcat" password="tomcat" roles="manager-gui,manager-jmx"/>
```

* manager-gui : 브라우저로 gui 환경에서 status 확인을 가능하도록 함
* manager-jmx : 상세한 status 정보를 확인 가능
* tomcat이라는 유저에 manager-gui와 manager-jmx 권한 부여



GUI를 통해 상태 정보 확인

```
http://localhost:8080/manager
```



jmx 정보를 사용하여 ThreadPool 정보 확인

```
http://localhost:8080/manager/jmxproxy?qry=Catalina:name=%22ajp-apr-8009%22,type=ThreadPool
```

* qry의 name 정보는 GUI 상태 정보로 접속하여 확인 가능.
* 아파치 서버를 사용하는 경우 위와 같이 ajp-apr-8009를 통해 스레드풀 확인
* 톰캣으로 다이렉트로 붙는 환경이면 http-apr-8080으로 확인
* 네이밍은 환경에 따라 다를 수 있으므로 상태 정보에서 확인 후 적용.