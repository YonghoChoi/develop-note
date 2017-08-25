## Client ID와 Secret 발급

1. google cloud platform 접속
2. 로그인
3. API 및 서비스 -> 사용자 인증 정보 -> OAuth Client ID 선택
4. Client ID와 Secret 발급 후 저장



## Access Token 발급

1. [레퍼런스](https://developers.google.com/identity/protocols/OAuth2) 참고
   * [Server-side web Apps](https://developers.google.com/identity/protocols/OAuth2WebServer) 참고
2. accessType
   * offline : Resource 서버가 access token을 줄 때 refresh token 값도 같이 줌.
     * access token이 만료되면 refresh token을 Resource Server에 전달해서 access token을 다시 받을 수 있다.
   * online : 
3. redirect_uri : Code 정보를 Resource Server가 Client에 전달할 때 Client가 받게될 경로
4. online url encode : https://meyerweb.com/eric/tools/dencoder/





## user정보

* user 정보 획득 scope

  * https://www.googleapis.com/auth/userinfo.profile

* access Token 발급 후 정보 획득

  * ```
    https://www.googleapis.com/oauth2/v1/userinfo?access_token=xxx
    ```



## Java API

### Credential

* Access Token을 사용하여 보호 된 리소스에 액세스하기 위한 스레드 안전한 OAuth 2.0 Helper 클래스

* Access Token이 만료된 경우 Refresh Token을 사용하여 새로 고침 가능

  ```java
  public static HttpResponse executeGet(
        HttpTransport transport, JsonFactory jsonFactory, String accessToken, GenericUrl url)
        throws IOException {
      Credential credential =
          new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(accessToken);
      HttpRequestFactory requestFactory = transport.createRequestFactory(credential);
      return requestFactory.buildGetRequest(url).execute();
    }
  ```



## 참고

* [Google OAuth Example](https://github.com/bashofmann/oauth2_java_webapp_example/blob/master/src/main/java/de/bastianhofmann/oauth2/examples/webapp/AuthorizationCodeFlowProvider.java)

* [Using OAuth 2.0 with the Google API Client Library for Java](https://developers.google.com/api-client-library/java/google-api-java-client/oauth2)

  ​