* elasticsearch는 2.x 버전 이전에는 Djb 해시 함수를 사용했었는데 이 후 부터는 murmur3 해시 함수를 사용함.

* 아래 로직으로 샤드에 어떻게 분배되는지 확인해보니 거의 균일하게 배포됨

  ```java
  final int SHARD_SIZE = 5;
  int[] shards = new int[SHARD_SIZE];
  for(int i = 0; i < 100000; i++) {
      int hash = Math.abs(Murmur3HashFunction.hash(String.valueOf(i)));
      int shardId = hash % SHARD_SIZE;
      shards[shardId]++;

      Thread.sleep(1);
  }

  for(int i = 0; i < SHARD_SIZE; i++) {
      System.out.println(String.format("shardId : %d, count : %d", i, shards[i]));
  }
  ```

  ```
  // 결과
  shardId : 0, count : 19740
  shardId : 1, count : 20016
  shardId : 2, count : 19905
  shardId : 3, count : 20032
  shardId : 4, count : 20307
  ```

* 해시 말고 걍 랜덤으로 돌려도 거의 균일하게 배포됨

  ```java
  final int SHARD_SIZE = 5;
  int[] shards = new int[SHARD_SIZE];
  Random rand = new Random();
  for(int i = 0; i < 100000; i++) {
      int hash = rand.nextInt(2000000000);
      if(hash < 100000000) {
          hash += 100000000;
      }
      int shardId = hash % SHARD_SIZE;
      shards[shardId]++;
  }

  for(int i = 0; i < SHARD_SIZE; i++) {
      System.out.println(String.format("shardId : %d, count : %d", i, shards[i]));
  }
  ```

  ```
  // 결과
  shardId : 0, count : 19959
  shardId : 1, count : 20032
  shardId : 2, count : 19926
  shardId : 3, count : 20012
  shardId : 4, count : 20071
  ```

* UUID를 사용해도 균일하게 배포됨

  ```java
  final int SHARD_SIZE = 5;
  int[] shards = new int[SHARD_SIZE];
  for(int i = 0; i < 100000; i++) {
      UUID uuid = UUID.randomUUID();
      int hash = Math.abs(Murmur3HashFunction.hash(uuid.toString()));
      int shardId = hash % SHARD_SIZE;
      shards[shardId]++;
  }

  for(int i = 0; i < SHARD_SIZE; i++) {
      System.out.println(String.format("shardId : %d, count : %d", i, shards[i]));
  }
  ```

  ```
  // 결과
  shardId : 0, count : 20006
  shardId : 1, count : 19830
  shardId : 2, count : 20141
  shardId : 3, count : 19863
  shardId : 4, count : 20160
  ```

  ​

## 참고

* [elasticsearch Djb Hash함수](https://github.com/elastic/elasticsearch/blob/1.4/src/main/java/org/elasticsearch/cluster/routing/operation/hash/djb/DjbHashFunction.java)
* [elasticsearch murmur3 Hash 함수](https://github.com/elastic/elasticsearch/blob/master/server/src/main/java/org/elasticsearch/cluster/routing/Murmur3HashFunction.java)
* [Java HashMap은 어떻게 동작하는가?](http://d2.naver.com/helloworld/831311)