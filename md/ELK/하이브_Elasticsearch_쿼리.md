## 재화 소비량

```json
GET hive_user/7062524311363492765/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "term": {
            "title": "goods"
          }
        },
        {
          "term": {
            "subTitle": "consume"
          }
        },
        {
          "range" : {
            "detail.gold" : {
              "gt" : 0
            }
          }
        }
      ]
    }
  },
  "aggs" : {
        "consume_goods" : { "sum" : { "field" : "detail.gold" } }
    }
}
```

* gold 부분을 특정 재화명으로 입력하면 해당 재화에 대한 정보 및 통계 출력