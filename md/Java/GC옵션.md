GC 로그를 확인하기 위해 java 실행 옵션에 아래 옵션을 추가

```
"-Xloggc:<로그파일 경로>/gc.log"
```

해당 경로에 생성된 로그 파일을 확인해보면 아래와 같이 출력

```
Java HotSpot(TM) 64-Bit Server VM (25.111-b14) for linux-amd64 JRE (1.8.0_111-b14), built on Sep 22 2016 16:14:03 by "java_re" with gcc 4.3.0 20080428 (Red Hat 4.3.0-8)
Memory: 4k page, physical 8037496k(6189156k free), swap 8251388k(7206132k free)
CommandLine flags: -XX:+CMSClassUnloadingEnabled -XX:+HeapDumpOnOutOfMemoryError -XX:InitialHeapSize=4294967296 -XX:InitialTenuringThreshold=2 -XX:MaxHeapSize=4294967296 -XX:MaxTenuringThreshold=2 -XX:+PrintGC -XX:+PrintGCTimeStamps -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseParallelGC 
0.660: [GC (Metadata GC Threshold)  167772K->12296K(4019712K), 0.0098840 secs]
0.670: [Full GC (Metadata GC Threshold)  12296K->11645K(4019712K), 0.0215507 secs]
3.713: [GC (Allocation Failure)  1060221K->43622K(4019712K), 0.0238695 secs]
5.118: [GC (Allocation Failure)  1092198K->35844K(4019712K), 0.0179233 secs]
6.589: [GC (Allocation Failure)  1084420K->40121K(4019712K), 0.0196068 secs]
...
```

시간 정보가 필요한 경우 아래와 같이 옵션 추가

```
"-Xloggc:<로그파일 경로>/gc.log -XX:+PrintGCDateStamps"
```

아래와 같이  TimeStamp가 추가됨.

```
Java HotSpot(TM) 64-Bit Server VM (25.111-b14) for linux-amd64 JRE (1.8.0_111-b14), built on Sep 22 2016 16:14:03 by "java_re" with gcc 4.3.0 20080428 (Red Hat 4.3.0-8)
Memory: 4k page, physical 8037496k(6187136k free), swap 8251388k(7206252k free)
CommandLine flags: -XX:+CMSClassUnloadingEnabled -XX:+HeapDumpOnOutOfMemoryError -XX:InitialHeapSize=4294967296 -XX:InitialTenuringThreshold=2 -XX:MaxHeapSize=4294967296 -XX:MaxTenuringThreshold=2 -XX:+PrintGC -XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseParallelGC 
2017-03-20T12:24:23.034+0900: 0.650: [GC (Metadata GC Threshold)  167772K->12344K(4019712K), 0.0094486 secs]
2017-03-20T12:24:23.044+0900: 0.659: [Full GC (Metadata GC Threshold)  12344K->11645K(4019712K), 0.0224034 secs]
2017-03-20T12:24:26.115+0900: 3.730: [GC (Allocation Failure)  1060221K->43714K(4019712K), 0.0228086 secs]
2017-03-20T12:24:27.546+0900: 5.161: [GC (Allocation Failure)  1092290K->40506K(4019712K), 0.0214479 secs]
2017-03-20T12:24:29.029+0900: 6.644: [GC (Allocation Failure)  1089082K->40885K(4019712K), 0.0256162 secs]
...
```

