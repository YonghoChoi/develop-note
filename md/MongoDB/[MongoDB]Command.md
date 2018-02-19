# MongoDB Command

## 검색

* 컬렉션 전체 검색

  ```
  db.[CollectionName].find()
  ```

* 컬렉션 row 카운트

  ```
  db.[CollectionName].find().count()
  ```

* 날짜 범위 검색

  ```
  db.[CollectionName].find({ timestamp: { $gt:  ISODate("2017-11-07T00:00:00.000Z") }, $lt:  ISODate("2017-11-08T00:00:00.000Z") } })
  ```

* like 검색

  ```
  db.[CollectionName].find({"name": /.*m.*/})
  db.[CollectionName].find({"name": /m/})
  ```

* limit 검색

  ```
  db.[CollectionName].find().limit(1)
  ```

* join 검색

  ```

  ```

  ​



## 삽입

* key-value 삽입

  ```
  db.[CollectionName].insert({"[key]": "[value]"})
  ```

* 날짜 데이터 삽입

  ```
  db.[CollectionName].insert({"[key]": ISODate("2017-11-07T00:00:00.000Z")})
  ```

  ```
  db.[CollectionName].insert({"[key]": new Date()})
  ```



## 삭제

* 컬렉션 삭제

  ```
  db.[CollectionName].drop()
  ```

  ​