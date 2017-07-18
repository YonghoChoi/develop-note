# X-Pack 설치

X-Pack을 사용하려면 다음이 필요합니다.

- Elasticsearch 5.4.3 - [Elasticsearch 설치](http://www.elastic.co/guide/en/elasticsearch/reference/5.4/_installation.html)
- Kibana 5.4.3 - [Kibana Up 및 Running하기](http://www.elastic.co/guide/en/kibana/5.4/setup.html)

실행중인 Elasticsearch의 버전과 일치하는 X-Pack 버전을 실행해야합니다.

X-Pack은 또한 [Logstash에](http://www.elastic.co/guide/en/logstash/5.4/introduction.html) 대한 모니터링 UI를 제공합니다 .

![중요](https://www.elastic.co/guide/en/x-pack/5.4/images/icons/important.png) 기존 클러스터에 X-Pack을 처음 설치하는 경우 전체 클러스터를 다시 시작해야합니다. X-Pack을 설치하면 클러스터의 모든 노드에서 보안 및 보안을 활성화해야만 클러스터가 올바르게 작동합니다. [업그레이드](https://www.elastic.co/guide/en/x-pack/5.4/installing-xpack.html#xpack-upgrading) 할 때 대개 [롤링 업그레이드를](http://www.elastic.co/guide/en/elasticsearch/reference/5.4/rolling-upgrades.html) 수행 할 수 있습니다 .

X-Pack을 설치하려면 다음을 수행하십시오.

1. 클러스터에 구동 중인 각각의 노드들의 ES_HOME 위치에서 `bin/elasticsearch-plugin install` 실행:

   ```
   bin/elasticsearch-plugin install x-pack
   ```

   ![노트](https://www.elastic.co/guide/en/x-pack/5.4/images/icons/note.png) Elasticsearch 의 [DEB / RPM 배포판](https://www.elastic.co/guide/en/x-pack/5.4/installing-xpack.html#xpack-package-installation) 을 사용하는 경우 수퍼 유저 권한으로 설치를 실행하십시오. 오프라인 설치를 수행하려면 [X-Pack 바이너리를 다운로드하십시오](https://www.elastic.co/guide/en/x-pack/5.4/installing-xpack.html#xpack-installing-offline) .

2. X-Pack에 추가 사용 권한을 부여 할 것인지 확인하십시오.

   ![팁](https://www.elastic.co/guide/en/x-pack/5.4/images/icons/tip.png) 자동으로 이러한 권한을 부여하고 이러한 설치 프롬프트를 건너 뛰려면 install 명령을 실행할 때 `--batch` 옵션을 지정하십시오 .

   1. Watcher가 전자 메일 알림을 보낼 수 있도록 X-Pack에 설치하는 동안 threat context loader를 설정하려면 이러한 권한이 필요합니다.

      ```
      @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
      @     WARNING: plugin requires additional permissions     @
      @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
      * java.lang.RuntimePermission accessClassInPackage.com.sun.activation.registries
      * java.lang.RuntimePermission getClassLoader
      * java.lang.RuntimePermission setContextClassLoader
      * java.lang.RuntimePermission setFactory
      * java.security.SecurityPermission createPolicy.JavaPolicy
      * java.security.SecurityPermission getPolicy
      * java.security.SecurityPermission putProviderProperty.BC
      * java.security.SecurityPermission setPolicy
      * java.util.PropertyPermission * read,write
      * java.util.PropertyPermission sun.nio.ch.bugLevel write
      * javax.net.ssl.SSLPermission setHostnameVerifier
      See http://docs.oracle.com/javase/8/docs/technotes/guides/security/permissions.html
      for descriptions of what these permissions allow and the associated risks.

      Continue with installation? [y/N]y
      ```

   2. X-Pack에는 Elasticsearch에서 기계 학습 분석 엔진을 시작하는 데 필요한 권한이 필요합니다. 네이티브 컨트롤러는 시작된 프로세스가 유효한 기계 학습 구성 요소임을 보장합니다. 일단 시작되면 기계 학습 프로세스와 Elasticsearch 간의 통신은 Elasticsearch가 실행되는 운영 체제 사용자로 제한됩니다.

      ```
      @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
      @        WARNING: plugin forks a native controller        @
      @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
      This plugin launches a native controller that is not subject to
      the Java security manager nor to system call filters.

      Continue with installation? [y/N]y
      ```

3. Elasticsearch에 자동 인덱스 생성이 비활성화 되어 있는 경우, X-Pack이 다음과 같은 인덱스를 만들 수 있도록 `elasticsearch.yml`에 [`action.auto_create_index`](http://www.elastic.co/guide/en/elasticsearch/reference/5.4/docs-index_.html#index-creation)을 설정합니다:

   ```
   action.auto_create_index: .security,.monitoring*,.watches,.triggered_watches,.watcher-history*,.ml*
   ```

4. Elasticsearch를 실행합니다.

   ```
   bin/elasticsearch
   ```

5. Kibana 설치 디렉토리에서 `bin/kibana-plugin`을 실행하여  X-Pack을 [Kibana](http://www.elastic.co/guide/en/kibana/5.4/setup.html) 에 설치하십시오.

   ```
   bin/kibana-plugin install x-pack
   ```

6. Kibana를 실행합니다.

   ```
   bin/kibana
   ```

7. Logstash 설치 디렉토리에서 `bin/logstash-plugin`을 실행하여 X-Pack을 [Logstash](http://www.elastic.co/guide/en/logstash/5.4/installing-logstash.html) 에 설치하십시오.

   ```
   bin/logstash-plugin install x-pack
   ```

8. [Logstash를 설정하고 시작하십시오](http://www.elastic.co/guide/en/logstash/5.4/configuration.html) .

X-Pack 설치를 확인하려면 웹 브라우저에서 `http://localhost:5601/` 를 눌러 Kibana를 엽니 다. Kibana에 로그인하라는 메시지가 표시됩니다. 로그인하려면 내장 된 `elastic`사용자와 암호를 사용할 수 있습니다  패스워드는 `changeme`.

![중대한](https://www.elastic.co/guide/en/x-pack/5.4/images/icons/important.png)SSL / TLS 암호화는 기본적으로 사용되지 않습니다. 즉, 사용자 자격 증명이 일반 텍스트로 전달됩니다. **암호화를 사용하지 않고 프로덕션에 배포하지 마십시오! **자세한 내용은 [통신 암호화를](https://www.elastic.co/guide/en/x-pack/5.4/encrypting-communications.html) 참조하십시오 .

**생산에 배치하기 전에 Kibana가 elasticsearch와 통신 할 수 있도록 **하는 내장 elastic사용자 및 사용자 의 비밀번호kibana도 **변경** 해야합니다 . 자세한 내용은 [내장 된 사용자](https://www.elastic.co/guide/en/x-pack/5.4/setting-up-authentication.html#built-in-users) 를 참조하십시오.

### DEB / RPM 패키지 설치에 X-Pack 설치하기

DEB / RPM 패키지를 사용하여 Elasticsearch를 설치하는 경우 기본적으로 Elasticsearch가 설치되고 `/usr/share/elasticsearch`구성 파일이 저장됩니다 `/etc/elasticsearch`. (기본 경로의 전체 목록은Elasticsearch Reference의 [Debian Directory Layout](http://www.elastic.co/guide/en/elasticsearch/reference/5.4/deb.html#deb-layout) 및 [RPM Directory Layout](http://www.elastic.co/guide/en/elasticsearch/reference/5.4/rpm.html#rpm-layout) 을 참조하십시오.)

DEB / RPM 패키지 설치에 X-Pack을 설치하려면 수퍼 유저 권한으로 디렉토리 `bin/plugin install`에서 실행해야합니다 `/usr/share/elasticsearch`.

```
cd / usr / share / elasticsearch
sudo bin / elasticsearch - 플러그인 설치 x - pack
```

![노트](https://www.elastic.co/guide/en/x-pack/5.4/images/icons/note.png)구성 파일이없는 `/etc/elasticsearch`경우 시스템 속성 `es.path.conf`을 통해 구성 경로를`ES_JAVA_OPTS="-Des.path.conf=<path>"`설정하거나를 `CONF_DIR`통해 환경 변수 를 설정하여 구성 파일의 위치를 지정해야합니다 `CONF_DIR=<path>`.

### 오프라인 컴퓨터에 X-Pack 설치

플러그인 설치 스크립트는 X-Pack을 다운로드하고 설치하기 위해 인터넷에 직접 액세스해야합니다. 서버가 인터넷에 액세스 할 수없는 경우 수동으로 X-Pack을 다운로드하여 설치할 수 있습니다.

인터넷에 액세스 할 수없는 컴퓨터에 X-Pack을 설치하려면 다음을 수행하십시오.

1. 수동으로 X-Pack zip 파일을 다운로드하십시오. ( [sha1](https://artifacts.elastic.co/downloads/packs/x-pack/x-pack-5.4.3.zip.sha1) )[`https://artifacts.elastic.co/downloads/packs/x-pack/x-pack-5.4.3.zip`](https://artifacts.elastic.co/downloads/packs/x-pack/x-pack-5.4.3.zip)

2. zip 파일을 오프라인 시스템의 임시 디렉토리로 전송하십시오. (파일을 Elasticsearch 플러그인 디렉토리에 두지 마십시오.)

3. `bin/elasticsearch-plugin install`Elasticsearch 설치 디렉토리에서 실행 하고 X-Pack zip 파일의 위치를 지정하십시오. 예 :

   ```
   bin / elasticsearch - 플러그인 설치 파일 : /// path / to / file / x - pack - 5.4 . 3.zip
   ```

   ![노트](https://www.elastic.co/guide/en/x-pack/5.4/images/icons/note.png)

   `file://`프로토콜 다음에 zip 파일의 절대 경로를 지정해야 합니다.

4. `bin/kibana-plugin install`Kibana 설치 디렉토리에서 실행 하고 X-Pack zip 파일의 위치를 지정하십시오. (Elasticsearch, Kibana 및 Logstash 용 플러그인은 동일한 zip 파일에 포함되어 있습니다.) 예를 들면 다음과 같습니다.

   ```
   bin / kibana - 플러그인 설치 파일 : /// 경로 / to / file / x - pack - 5.4 . 3.zip
   ```

5. `bin/logstash-plugin install`Logstash 설치 디렉토리에서 실행 하고 X-Pack zip 파일의 위치를 지정하십시오. (Elasticsearch, Kibana 및 Logstash 용 플러그인은 동일한 zip 파일에 포함되어 있습니다.) 예를 들면 다음과 같습니다.

   ```
   bin / logstash - 플러그인 설치 파일 : /// path / to / file / x - pack - 5.4 . 3.zip
   ```

### X-Pack 기능 활성화 및 비활성화

기본적으로 모든 X-Pack 기능이 사용됩니다. 다음 `elasticsearch.yml`과 에서 X-Pack 기능을 명시 적으로 활성화 또는 비활성화 할 수 있습니다 `kibana.yml`.

| 환경                         | 기술                                       |
| -------------------------- | ---------------------------------------- |
| `xpack.graph.enabled`      | `false`X-Pack 그래프 기능을 사용하지 않으 려면 로 설정하십시오 . `elasticsearch.yml`및 에서 모두 구성하십시오 `kibana.yml`. |
| `xpack.ml.enabled`         | `false`X-Pack 기계 학습 기능을 사용하지 않으 려면 로 설정하십시오 . `elasticsearch.yml`및 에서 모두 구성하십시오 `kibana.yml`. |
| `xpack.monitoring.enabled` | `false`X-Pack 모니터링 기능을 사용하지 않으 려면 로 설정하십시오 . 의 구성 `elasticsearch.yml`, `kibana.yml`그리고 `logstash.yml`. |
| `xpack.reporting.enabled`  | `false`X-Pack보고 기능을 사용하지 않으 려면 로 설정하십시오 . 에서 구성하십시오 `kibana.yml`. |
| `xpack.security.enabled`   | `false`X-Pack 보안 기능을 사용하지 않으 려면 로 설정하십시오 . `elasticsearch.yml`및 에서 모두 구성하십시오 `kibana.yml`. |
| `xpack.watcher.enabled`    | `false`감시자를 사용하지 않으 려면 로 설정하십시오 . 에서 구성하십시오 `elasticsearch.yml`. |

자세한 내용은 [X-Pack 설정을](https://www.elastic.co/guide/en/x-pack/5.4/xpack-settings.html) 참조하십시오 .

### X-Pack 업그레이드

X-Pack을 업그레이드하려면 다음을 수행하십시오.

1. 탄력적 인 검색을 중지하십시오.

2. Elasticsearch에서 X-Pack 제거 :

   ```
   bin / elasticsearch - 플러그인 제거 x - 팩
   ```

3. Elasticsearch에 X-Pack의 새 버전을 설치하십시오.

   ```
   bin / elasticsearch - 플러그인 설치 x - pack
   ```

4. 탄력적 인 검색을 다시 시작하십시오.

   ![중대한](https://www.elastic.co/guide/en/x-pack/5.4/images/icons/important.png)

   프로덕션 클러스터를 업그레이드하는 경우 [롤링 업그레이드](http://www.elastic.co/guide/en/elasticsearch/reference/5.4/rolling-upgrades.html) 를 수행하여 복구가 가능한 빨리 이루어 지도록하십시오. 롤링 업그레이드는 새 부 버전으로 업그레이드 할 때 지원됩니다. 새 주요 버전으로 업그레이드 할 때 전체 클러스터를 다시 시작해야합니다.

5. Kibana에서 X-Pack을 제거하십시오.

   ```
   bin / kibana - x - pack을 제거한 플러그인
   ```

6. Kibana에 X-Pack의 새 버전을 설치하십시오.

   ```
   bin / kibana - 플러그인 설치 x - 팩
   ```

7. Kibana를 다시 시작하십시오.

### X-Pack 제거

X-Pack을 제거하려면 다음을 수행하십시오.

1. 탄력적 인 검색을 중지하십시오.

2. Elasticsearch에서 X-Pack 제거 :

   ```
   bin / elasticsearch - 플러그인 제거 x - 팩
   ```

3. 탄력적 인 검색을 다시 시작하십시오.

4. Kibana에서 X-Pack 제거 :

   ```
   bin / kibana - x - pack을 제거한 플러그인
   ```

5. Kibana를 다시 시작하십시오.

6. Logstash에서 X-Pack 제거 :

   ```
   bin / logstash - 플러그인 제거 x - 팩
   ```

7. 로그 셧을 다시 시작하십시오.