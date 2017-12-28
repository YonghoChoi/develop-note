# Snapshot

## 리파지토리 만들기

1. config/elasticsearch.yml 파일에 path.repo 추가

   ```
   path.repo: ["/backup"]
   ```

2. backup 디렉토리에 사용자 계정이 접근할 수 있도록 권한 수정

   ```shell
   $ chown 계정:계정그룹 /backup
   ```

3. curl 명령으로 리파지토리 생성

   ```shell
   $ curl -H 'Content-Type: application/json' -XPUT localhost:9200/_snapshot/my_backup -d '
   {
     "type": "fs",
     "settings": {
       "location": "/backup/my_backup"
     }
   }'
   ```

   * Content-Type을 지정하지 않으면 아래와 같은 오류 발생. application/json으로 지정

     ```json
     {
       "error": "Content-Type header [application/x-www-form-urlencoded] is not supported",
       "status": 406
     }
     ```



## Snapshot 생성

* 명령 수행

  ```shell
  $ curl -H 'Content-Type: application/json' -XPUT localhost:9200/_snapshot/my_backup/snapshot_1?wait_for_completion=true -d '
  {
    "indices": "hive_live*",
    "ignore_unavailable": true,
    "include_global_state": false
  }'
  ```

* 결과

  ```json
  {
    "snapshot": {
      "snapshot": "snapshot_1",
      "uuid": "4L9dAxRyTWKYpXfFe2DAmg",
      "version_id": 6010199,
      "version": "6.1.1",
      "indices": [
        "hive_live_2017_12_18"
      ],
      "state": "SUCCESS",
      "start_time": "2017-12-28T08:31:06.525Z",
      "start_time_in_millis": 1514449866525,
      "end_time": "2017-12-28T08:31:07.608Z",
      "end_time_in_millis": 1514449867608,
      "duration_in_millis": 1083,
      "failures": [],
      "shards": {
        "total": 5,
        "failed": 0,
        "successful": 5
      }
    }
  }
  ```

* Snapshot 파일 확인

  ```
  drwxrwxr-x 3 hive hive 4096 12월 28 17:31 ./
  drwxrwxr-x 3 hive hive 4096 12월 28 17:23 ../
  -rw-rw-r-- 1 hive hive  185 12월 28 17:31 index-0
  -rw-rw-r-- 1 hive hive    8 12월 28 17:31 index.latest
  drwxrwxr-x 3 hive hive 4096 12월 28 17:31 indices/
  -rw-rw-r-- 1 hive hive   90 12월 28 17:31 meta-4L9dAxRyTWKYpXfFe2DAmg.dat
  -rw-rw-r-- 1 hive hive  235 12월 28 17:31 snap-4L9dAxRyTWKYpXfFe2DAmg.dat
  ```



## Restore

* 명령 실행

  ```shell
  $ curl -H 'Content-Type: application/json' -XPOST localhost:9200/_snapshot/my_backup/snapshot_1/_restore -d '
  {
    "indices": "hive_live*",
    "ignore_unavailable": true,
    "include_global_state": true
  }'
  ```

* 결과

  ```json
  {"accepted":true}
  ```

  ​





