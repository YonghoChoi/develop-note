# 내부 배포용 kibana 설정

## 메뉴 수정

* $KIBANA_HOME/src/core_plugins/kibana/index.js 수정

  ```shell
  ... 생략 ...
  links: [{
    id: 'kibana:discover',
    title: 'Discover',
    order: -1003,
    url: `${kbnBaseUrl}#/discover`,
    description: 'interactively explore your data',
    icon: 'plugins/kibana/assets/discover.svg'
  }, {
    id: 'kibana:visualize',
    title: 'Visualize',
    order: -1002,
    url: `${kbnBaseUrl}#/visualize`,
    description: 'design data visualizations',
    icon: 'plugins/kibana/assets/visualize.svg'
  }, {
    id: 'kibana:dashboard',
    title: 'Dashboard',
    order: -1001,
    url: `${kbnBaseUrl}#/dashboards`,
    // The subUrlBase is the common substring of all urls for this app. If not given, it defaults to the url
    // above. This app has to use a different subUrlBase, in addition to the url above, because "#/dashboard"
    // routes to a page that creates a new dashboard. When we introduced a landing page, we needed to change
    // the url above in order to preserve the original url for BWC. The subUrlBase helps the Chrome api nav
    // to determine what url to use for the app link.
    subUrlBase: `${kbnBaseUrl}#/dashboard`,
    description: 'compose visualizations for much win',
    icon: 'plugins/kibana/assets/dashboard.svg'
  }, {
    id: 'kibana:dev_tools',
    title: 'Dev Tools',
    order: 9001,
    url: '/app/kibana#/dev_tools',
    description: 'development tools',
    icon: 'plugins/kibana/assets/wrench.svg'
  }, {
    id: 'kibana:management',
    title: 'Management',
    order: 9003,
    url: `${kbnBaseUrl}#/management`,
    description: 'define index patterns, change config, and more',
    icon: 'plugins/kibana/assets/settings.svg',
    linkToLastSubUrl: false
  }]

  ... 생략 ...
  ```

  * link 부분의 불필요한 카테고리 제거



## nginx를 통한 계정 로그인

* 사용자 인증을 위해 htpasswd 설치

  ```shell
  $ apt-get install apache2-utils
  ```

* 사용자 추가

  ```shell
  $ htpasswd -c .htpasswd yongho
  ```

* nginx 설정

  * kibana-admin.conf

    ```
    server {
        listen       9991;
        server_name  localhost;

        charset utf-8;
        #access_log  /var/log/nginx/log/host.access.log  main;

        location / {
            auth_basic "restricted -admin";
            auth_basic_user_file /etc/nginx/conf.d/.admin-htpasswd;

            # set some headers
            proxy_http_version 1.1;
            proxy_set_header  X-Real-IP  $remote_addr;
            proxy_set_header  X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header  Host $http_host;
            proxy_redirect off;
            proxy_pass http://192.168.10.214:5601;
        }
    ```


        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   /usr/share/nginx/html;
        }
    }
    ​```

  * kibana-readonly.conf

    ```
    server {
        listen       9992;
        server_name  localhost;

        charset utf-8;
        #access_log  /var/log/nginx/log/host.access.log  main;

        location / {
            auth_basic "Restricted";
            auth_basic_user_file /etc/nginx/conf.d/.htpasswd;

            # set some headers
            proxy_http_version 1.1;
            proxy_set_header  X-Real-IP  $remote_addr;
            proxy_set_header  X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header  Host $http_host;
            proxy_redirect off;
            proxy_pass http://192.168.10.214:5602;
        }

        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   /usr/share/nginx/html;
        }
    }
    ```

    ​



## 참고

* [Elasticsearch와 Kibana 보안](https://mapr.com/blog/how-secure-elasticsearch-and-kibana/)

