# Elasticsearch Tips

## Elastic search.in.sh 설정

* 자바 메모리 옵션은 메모리 용량이 변경되는 불필요한 오버헤드를 방지하기 위해 최소/최대 메모리를 동일하게 지정해서 사용하는 것을 권장

  * ES_HEAP_SIZE 옵션을 조정하면 최소/최대 값이 동일하게 적용된다.

* 힙 덤프 파일의 저장경로 지정

  * JAVA_OPTS 설정

    ```shell
    JAVA_OPTS="$JAVA_OPTS -XX:HeapDumpPath=$ES_HOME/logs/heapdump.hprof"
    ```



## elasticsearch.yml 설정

* path.data의 경로 설정에서 쉼표(,)로 구분하여 여러 경로를 지정할 수 있다.

  * 이 방법으로 색인 된 데이터를 여러 개의 드라이브에 나눠서 저장 가능

* plugin.mandatory에 쉼표(,)로 구분하여 지정된 플러그인들이 반드시 설치되어야만 elasticsearch가 구동되도록 할 수 있다.

* Bootstrap.mlockall 옵션을 true로 설정하면 자바 가상 머신 위에서 실행 중인 엘라스틱서치가 점유하는 메모리를 고정시킨다.

  * ES_HEAP_SIZE를 통해 자바 힙 메모리의 최소/최대 값을 동일하게 설정하고 bootstrap.mlockall 설정을 true로 지정하면 엘라스틱서치에서 메모리가 부족하지 않도록 방지할 수 있으므로 true로 설정할 것을 권장
  * 메모리를 미리 점유하고 사용하기 때문에 다른 자바 프로세스가 메모리를 swap하는 것을 방지할 수 있다.

* 슬로우 로그 설정

  ```yaml
  # 질의
  index.search.slowlog.threshold.query.warn: 10s
  index.search.slowlog.threshold.query.info: 5s
  index.search.slowlog.threshold.query.debug: 2s
  index.search.slowlog.threshold.query.trace: 500ms

  # 불러오기
  index.search.slowlog.threshold.fetch.warn: 1s
  index.search.slowlog.threshold.fetch.info: 800ms
  index.search.slowlog.threshold.fetch.debug: 500ms
  index.search.slowlog.threshold.fetch.trace: 200ms

  # 색인
  index.indexing.slowlog.threshold.index.warn: 1s
  index.indexing.slowlog.threshold.index.info: 800ms
  index.indexing.slowlog.threshold.index.debug: 500ms
  index.indexing.slowlog.threshold.index.trace: 200ms
  ```

  * 엘라스틱서치는 log4j 라이브러리를 사용
  * 각 로그 레벨에 따라 slow 로그 설정
  * query/fetch/indexing 작업이 지정된 시간이상 소요되었을 경우 이에 해당하는 슬로우 로그가 찍힘.
  * 예를들어 query에 2초가 지연되면 debug레벨의 슬로우 로그가 찍히고, 더 지연되어 5초가 되었을 때는 info 레벨로 슬로우 로그가 찍히고 10초까지 지연된 경우 warn 레벨의 로그가 찍힘
  * 슬로우 로그에 대한 설정은 `config/logging.yml`파일에서 설정



## docker

* 클러스터링 지원을 위해서는 environment 옵션이 필요

  ```shell
  $ docker run -d --name elas elasticsearch -Etransport.host=0.0.0.0 -Ediscovery.zen.minimum_master_nodes=1
  ```

* 엘라스틱서치는 indice들을 저장하기 위해서 hybrid mmapfs / niofs 디렉토리를 사용한다.

  * [가상 메모리 설정](https://www.elastic.co/guide/en/elasticsearch/reference/5.0/vm-max-map-count.html)이 필요.

  * mmap count의 한계치를 증가시켜야함 ([최소 262144 이상](https://www.elastic.co/guide/en/elasticsearch/reference/5.0/_maximum_map_count_check.html))

    ```shell
    $ sysctl -w vm.max_map_count=262144
    ```

  * 위 명령을 수행하거나 /etc/sysctl.conf 파일을 수정하여 위 설정을 추가시켜야한다.

    ```
    vm.max_map_count=262144
    ```

* ​