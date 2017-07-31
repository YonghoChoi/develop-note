* update

  ```
  POST hive_user/5155483832070376030/AV2HFAEtG52pRIDg0d3n/_update
  {
    "script": {
      "inline": "if(ctx._source.type.contains(params.type)){ ctx._source.weaponInvenTable.partsLevel1 = 1 }",
      "lang": "painless",
      "params": {
        "type": "WEAPON_INSERT"
      }  
    }
  }
  ```

* delete

  * 단일 document 삭제

    ```
    DELETE <index>/<type>/<id>
    ```

  * 쿼리로 삭제

    ```
    DELETE <index>/<type>/_query?q=<query>
    ```

  * 색인 닫기

    ```
    POST <index>/_close
    ```

    * 오래된 데이터의 경우 검색에 필요하지는 않지만 그래도 삭제하지는 않고 보존하고 싶은 경우
    * 오래된 색인을 유지한다는 것은 로그가 쌓일 수록 처리해야할 자원이 증가된다는 것을 의미한다. 
    * 색인을 닫은 이후 일래스틱서치의 메모리에 남아 있는 것은 이름과 샤드 위치와 같은 메타데이터 뿐이다.
    * 언제든 다시 열어서 검색할 수 있으므로 다시 검색할 필요성이 있는 경우 색인을 닫는 것이 유리.

* query

  * type필드가 WEAPON_INSERT인 document를 timestamp로 오름차순 정렬 하여 type 필드만 출력

    ```
    GET hive_user/5155483832070376030/_search?sort=timestamp:asc&_source=type&q=type:WEAPON_INSERT
    ```

  * 본문 입력 쿼리

    ```
    GET hive_user/_search
    {
      "query": {
        "match_all": {}
      },
      "_source": ["uid", "type"]
    }
    ```

  * wildcard 사용

    ```
    GET hive_user/_search
    {
      "query": {
        "match_all": {}
      },
      "_source": ["u*", "t*"]
    }
    ```

  * 포함 또는 제외

    ```
    GET hive_user/_search
    {
      "query": {
        "match_all": {}
      },
      "_source": {
        "includes": ["weapon*"],
        "excludes": ["weaponInvenTable.partsLevel1"]
      }
    }
    ```

    * includes에 포함된 필드들만 출력
    * 그 중 excludes에 명시된 항목은 제외

  ​