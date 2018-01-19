## Elasticsearch Node

### Master Node

* node.master 설정을 true로 지정하여 설정
* 노드 관리, 검색/색인 작업의 분배 역할



### Data Node

* node.data 설정을 true로 지정하여 설정
* 실질 적인 데이터 검색과 색인 작업을 수행



### Client Node

* node.client 설정을 true로 지정하여 설정
* REST API Call 작업 담당
* search 검색에 대한 로드 분산 담당



### 사용 팁

* 마스터 노드는 하둡의 Name 노드와 비슷
  * 고사양의 스펙이 필요하진 않음
* 데이터 노드보다는 마스터 노드의 사양을 더 낮게해서 사용



## Shard

* 물리적인 루씬의 Index

* primary 설정은 초기 지정 후 변경 불가

* replica 설정은 변경 가능

* 문서 색인 요청이 들어오면 요청을 받은 노드에서 이 문서를 어떤 노드의 어떤 샤드에 저장할지를 결정

  * hash partitioning을 통해 어떤 샤드에 문서를 저장할지 결정

  * 문서 ID에 대한 샤드 위치를 결정

  * 특정 필드 값을 지정 후 300GB 정도의 데이터를 색인해보니 특정 노드에 데이터가 몰리는 현상이 있었음

  * elasticsearch에서 제공하는 hash 함수가 있음

    * 문서 ID를 넘기면 shardSize에 따라 ID가 발급됨

    ```java
    int shardId = MathUtils.mod(hash(String.valueOf(i)), shardSize)
    ```

    ​

* 샤드에 색인이 되고 나면 replica 샤드로 복제가 이루어짐

* 삭제 요청도 먼저 primary 샤드에서 제거 후 복제 샤드에서 제거가 이루어짐



### Retrieve

* _id 값을 가지고 문서를 가져오는 방법



### Search

* 어떤 샤드에 문서가 있는지 알 수 없기 때문에 모든 샤드에 검색 요청
* 요청 받은 노드(마스터 노드)에서 모든 샤드에 검색 요청을 하고 결과를 해당 노드가 merge 및 sorting하여 반환



### Fetch

* 모든 노드(마스터/데이터)는 검색에 대한 요청을 받을 수가 있음.
* 검색 요청을 받은 노드가 마스터 노드로 요청을 전달하여 검색에 대한 작업 분배가 이루어진 후 결과를 머지해서 반환





