ELK 관련 docker 이미지들은 기존에 Docker hub를 통해 내려받을 수 있었지만 해당 이미지들이 현재는 Deprecated 되었다. 그래서 최신 이미지를 받기 위해서는 `docker.elastic.co/`하위의 이미지들을 사용해야 한다. 

기존 docker hub에서의 이미지들은 debian 계열이었지만 최신 이미지들은 centos 기반으로 구성이 되어 있다. 

## elasticsearch

### Dockerfile

```dockerfile
FROM docker.elastic.co/elasticsearch/elasticsearch:5.5.0

ENV ES_CLUSTER_NAME hive_es

# sudo 권한 부여
USER root
RUN yum install -y sudo
RUN echo 'elasticsearch:qusduddnjs' | chpasswd
#RUN adduser elasticsearch sudo
RUN echo 'elasticsearch ALL=(ALL) NOPASSWD:ALL' >> /etc/sudoers

COPY conf/elasticsearch.yml ./config/
#COPY conf/jvm.options ./config/
COPY conf/sysctl.conf /etc/
COPY start.sh /start.sh
RUN tr -d '\r' < /start.sh > /temp.sh && mv /temp.sh /start.sh
RUN chmod +x /start.sh && mv /start.sh /usr/share/elasticsearch/
RUN chown -R elasticsearch:elasticsearch /usr/share/elasticsearch

USER elasticsearch

# install search guard
RUN /usr/share/elasticsearch/bin/elasticsearch-plugin install -b com.floragunn:search-guard-5:5.5.0-14
RUN cd /usr/share/elasticsearch//plugins/search-guard-5/tools && \
    chmod +x install_demo_configuration.sh && \
    sed -i -e 's/\/etc\/elasticsearch/\/usr\/share\/elasticsearch\/config/' install_demo_configuration.sh && \
    sed -i -e 's/cluster.name: searchguard_demo/cluster.name: hive_es/' install_demo_configuration.sh && \
    ./install_demo_configuration.sh -y && \
    sed -i -e 's/-cn searchguard_demo/-cn hive_es -h 0.0.0.0 -p 9300/' sgadmin_demo.sh

# 컨테이너 구동 후 최초 한번 /usr/share/elasticsearch/start.sh를 구동 시켜줘야 search guard 플러그인이 정상 동작함
```



### start.sh

```shell
#/bin/bash
cd /usr/share/elasticsearch/plugins/search-guard-5/tools && ./sgadmin_demo.sh
```



## kibana

### Dockerfile

```dockerfile
FROM docker.elastic.co/kibana/kibana:5.5.0

# sudo 권한 부여
USER root
RUN yum install -y sudo wget
RUN echo 'kibana:qusduddnjs' | chpasswd
#RUN adduser elasticsearch sudo
RUN echo 'kibana ALL=(ALL) NOPASSWD:ALL' >> /etc/sudoers

# install logtrail
RUN kibana-plugin install https://github.com/sivasamyk/logtrail/releases/download/v0.1.17/logtrail-5.5.0-0.1.17.zip
COPY conf/logtrail.json /usr/share/kibana/plugins/logtrail/logtrail.json

# install search guard
RUN wget -O /opt/searchguard-kibana.zip https://github.com/floragunncom/search-guard-kibana-plugin/releases/download/v5.5.0-3/searchguard-kibana-5.5.0-3.zip
RUN kibana-plugin install file:///opt/searchguard-kibana.zip

COPY conf/kibana.yml /usr/share/kibana/config/kibana.yml
```



## logstash

### Dockerfile

```

```







## 참고

- [Elasticsearch with Docker](https://www.elastic.co/guide/en/elasticsearch/reference/current/docker.html)
- [Elasticsearch Dockerfile](https://github.com/elastic/elasticsearch-docker/blob/master/templates/Dockerfile.j2)
- [Logstash Settings File](https://www.elastic.co/guide/en/logstash/5.5/logstash-settings-file.html)