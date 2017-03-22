여러 노드를 테스트 해보기 위해 docker-machine을 사용하여 호스트를 추가

```shell
$ docker-machine create manager1
```

생성된 호스트를 ssh를 사용하여 접속할 수 있다.

```shell
$ docker-machine ssh manager1
```

해당 호스트에서 아래 명령을 수행하여 swarm을 초기화 한다.

```shell
$ docker swarm init --advertise-addr <MANAGER-IP>
```

내가 테스트 한 환경에서는 추가한 호스트의 ip가 192.168.99.100 였으므로 아래와 같이 명령을 수행한다.

```shell
$ docker swarm init --advertise-addr 192.168.99.100
Swarm initialized: current node (uxyypvr7sdqonc3wo0yfb2xl2) is now a manager.

To add a worker to this swarm, run the following command:

    docker swarm join \
    --token SWMTKN-1-5zvxsvfoox1qn0cioqboet4atdytlepbivautgbqq54fm71yzg-du6jgchik8m6j6eujgfsmp14i \
    192.168.99.100:2377

To add a manager to this swarm, run 'docker swarm join-token manager' and follow the instructions.
```

`docker info` 명령을 통해 스웜 정보를 살펴보면 다음과 같다.

```
Swarm: active
 NodeID: uxyypvr7sdqonc3wo0yfb2xl2
 Is Manager: true
 ClusterID: v5ov0tgklyfg6pco3cwnj4aur
 Managers: 1
 Nodes: 1
 Orchestration:
  Task History Retention Limit: 5
 Raft:
  Snapshot Interval: 10000
  Number of Old Snapshots to Retain: 0
  Heartbeat Tick: 1
  Election Tick: 3
 Dispatcher:
  Heartbeat Period: 5 seconds
 CA Configuration:
  Expiry Duration: 3 months
 Node Address: 192.168.99.100
 Manager Addresses:
  192.168.99.100:2377
```

`docker node ls` 명령을 수행하면 현재 스웜 노드 리스트를 확인해 볼 수 있다.

```shell
$ docker node ls
ID                           HOSTNAME  STATUS  AVAILABILITY  MANAGER STATUS
uxyypvr7sdqonc3wo0yfb2xl2 *  manager1  Ready   Active        Leader
```

manager가 정상적으로 생성되었다면 아래의 명령을 통해 worker 노드를 추가할 수 있다. 

```shell
$ docker swarm join \
    --token SWMTKN-1-5zvxsvfoox1qn0cioqboet4atdytlepbivautgbqq54fm71yzg-du6jgchik8m6j6eujgfsmp14i \
    192.168.99.100:2377
```

토큰을 잊어버렸거나 어떤 명령을 수행해야 할 지 모른다면 manager 호스트에서 `docker swarm join-token worker` 명령을 통해 확인할 수 있다. manager를 추가할 경우에는 worker 대신 manager를 명시하면 된다.

이제 docker-machine create 명령을 사용하여 worker가 될 호스트를 생성한 후 마찬가지로 ssh 로 접속하여 위의 join 명령을 수행한다.

```shell
$ docker-machine create worker1
$ docker-machine ssh worker1
$ docker swarm join \
    --token SWMTKN-1-5zvxsvfoox1qn0cioqboet4atdytlepbivautgbqq54fm71yzg-du6jgchik8m6j6eujgfsmp14i \
    192.168.99.100:2377
```

worker2도 생성하여 총 두개의 worker를 생성하여 manager에 join 시키면 아래와 같이 manager 호스트에서 node 리스트가 출력된다.

```shell
$ docker node ls
ID                           HOSTNAME  STATUS  AVAILABILITY  MANAGER STATUS
iyh8a6rhtce1l3ph26hslz1yq    worker2   Ready   Active
mbxrbunz9g0zt2iqvugog3v7q    worker1   Ready   Active
uxyypvr7sdqonc3wo0yfb2xl2 *  manager1  Ready   Active        Leader
```

이제 manager에서 서비스를 생성하여 worker 노드에 컨테이너를 생성해도록 해보겠다. 먼저 서비스 생성을 위해 아래 명령을 수행한다.

```shell
$ docker service create --replicas 1 --name helloworld alpine ping docker.com
```

replicas 옵션을 통해 하나의 worker에서 작동하도록 하며 이 값에 따라 각 호스트들에 균형있게 서비스가 배포된다. 서비스의 이름은 helloworld인 alpine 리눅스 컨테이너를 구동 시켜 ping docker.com 명령을 수행하도록 한다.

manager 호스트에서 `docker service ls` 명령을 통해 현재 실행중인 서비스들의 목록을 확인할 수 있다.

```shell
$ docker service ls
ID            NAME        MODE        REPLICAS  IMAGE
i9jj8r6ad04p  helloworld  replicated  0/1       alpine:latest
```

그리고 inspect 명령을 통해 service의 상세 정보를 확인할 수 있다.

```shell
$ docker service inspect --pretty helloworld

ID:		i9jj8r6ad04plbyja4c4ft7pr
Name:		helloworld
Service Mode:	Replicated
 Replicas:	1
Placement:
UpdateConfig:
 Parallelism:	1
 On failure:	pause
 Max failure ratio: 0
ContainerSpec:
 Image:		alpine:latest@sha256:58e1a1bb75db1b5a24a462dd5e2915277ea06438c3f105138f97eb53149673c4
 Args:		ping docker.com
Resources:
Endpoint Mode:	vip
```

`docker service ps`명령을 통해 해당 서비스가 어떠한 worker에서 구동중인 지를 확인할 수 있다.

```shell
$ docker service ps helloworld
ID            NAME          IMAGE          NODE     DESIRED STATE  CURRENT STATE               ERROR  PORTS
jy1dwoz1vsvw  helloworld.1  alpine:latest  worker2  Running        Running about a minute ago
```

위 내용을 보면 worker2 노드에서 서비스가 구동중인 것을 알 수 있다.

여기서 컨테이너를 추가적으로 구동시켜야 하는 경우 아래와 같은 `docker service scale <SERVICE-ID>=<NUMBER-OF-TASK>` 명령을 통해 등록된 각 노드들에 자동으로 확장이 가능하다.

```shell
$ docker service scale helloworld=5
```

다시 `docker service ps` 명령을 수행해보면 아래와 같이 서비스가 5개 구동된 것을 확인할 수 있다.

```shell
$ docker service ps helloworld
ID            NAME          IMAGE          NODE      DESIRED STATE  CURRENT STATE           ERROR  PORTS
jy1dwoz1vsvw  helloworld.1  alpine:latest  worker2   Running        Running 13 minutes ago
xxgi4jipx0dv  helloworld.2  alpine:latest  worker2   Running        Running 18 seconds ago
x6z619l81eh6  helloworld.3  alpine:latest  worker1   Running        Running 7 seconds ago
lyc81ect7b48  helloworld.4  alpine:latest  manager1  Running        Running 7 seconds ago
ulwpbymnveg8  helloworld.5  alpine:latest  manager1  Running        Running 7 seconds ago
```

서비스를 제거하는 방법도 간단하다. 아래와 같이 rm  명령을 수행하면 서비스가 제거되고 각 노드의 컨테이너들도 제거된다. 컨테이너 제거에는 약간의 시간이 걸릴 수 있다.

```shell
$ docker service rm helloworld
```

각 호스트에 균형있게 분배된 이 서비스들에 대해 변경사항이 생겼을 때, 서비스에 지장 없이 롤링 업데이트를 적용할 수가 있다.

3.0.6 버전의 redis 서비스를 사용 중인 각 노드들을 3.0.7로 업그레이드하는 업데이트를 수행해보도록 한다.

먼저 아래와 같이 redis 서비스를 실행한다.

```shell
$ docker service create \
  --replicas 3 \
  --name redis \
  --update-delay 10s \
  redis:3.0.6
```

이전과 다른 점은 update-delay 옵션이 추가된 것인데 여기서는 10초를 지정했다. 이 의미는 하나의 노드에 대해 업데이트가 끝나고 10초 후에 다음 노드에 대한 업데이트를 수행하겠다는 의미이다. 

적용된 내용을 조금 더 자세히 살펴보기 위해 inspect 명령을 수행해보겠다.

```shell
$ docker service inspect --pretty redis

ID:		ezyaps0tpfpxx9vdeq3z2yu0y
Name:		redis
Service Mode:	Replicated
 Replicas:	3
Placement:
UpdateConfig:
 Parallelism:	1
 Delay:		10s
 On failure:	pause
 Max failure ratio: 0
ContainerSpec:
 Image:		redis:3.0.6@sha256:6a692a76c2081888b589e26e6ec835743119fe453d67ecf03df7de5b73d69842
Resources:
Endpoint Mode:	vip
```

UpdateConfig 부분을 보면 업데이트 시 적용되는 설정 값들을 확인해 볼 수 있는데 여기서 Parallelism은 한번에 업데이트를 수행할 노드의 개수이다. 이 값이 1이므로 한번에 하나의 노드에 대해서만 업데이트가 수행된다. 이 값을 변경하려면 서비스 생성 시에 `—update-parallelism` 옵션을 설정하면된다.

이제 redis 버전을 3.0.7로 업그레이드 시켜 보겠다.

```shell
$ docker service update --image redis:3.0.7 redis
```

`docker service ps redis` 명령을 통해 업데이트 진행 상황을 확인해볼 수 있다. 

```
D            NAME         IMAGE        NODE      DESIRED STATE  CURRENT STATE                ERROR  PORTS
y4a6fhhw3aq5  redis.1      redis:3.0.7  worker1   Running        Running 8 seconds ago
w68xkzhovbnb   \_ redis.1  redis:3.0.6  worker1   Shutdown       Shutdown 40 seconds ago
ibdyl9x5qrfk  redis.2      redis:3.0.7  worker2   Running        Running 51 seconds ago
9rykaxivy773   \_ redis.2  redis:3.0.6  worker2   Shutdown       Shutdown about a minute ago
wu1m9q0ktszl  redis.3      redis:3.0.6  manager1  Running        Running 2 minutes ago
```

위 내용을 보면 현재 manager1은 아직 3.0.6 버전으로 동작 중이고, worker2는 업데이트가 끝나서 3.0.7로 업그레이드가 되어 동작중이다. worker1은 조금전에 3.0.7로 업그레이드가 되어 구동된지 8초가 지난 상황이므로 앞으로 2초 후에 manager1도 업데이트가 이루어질 것이다.

```
ID            NAME         IMAGE        NODE      DESIRED STATE  CURRENT STATE                   ERROR  PORTS
y4a6fhhw3aq5  redis.1      redis:3.0.7  worker1   Running        Running 10 seconds ago
w68xkzhovbnb   \_ redis.1  redis:3.0.6  worker1   Shutdown       Shutdown 41 seconds ago
ibdyl9x5qrfk  redis.2      redis:3.0.7  worker2   Running        Running 53 seconds ago
9rykaxivy773   \_ redis.2  redis:3.0.6  worker2   Shutdown       Shutdown about a minute ago
4hiqmfc0955i  redis.3      redis:3.0.7            Ready          Pending less than a second ago
wu1m9q0ktszl   \_ redis.3  redis:3.0.6  manager1  Shutdown       Running 2 minutes ago
```

2초가 지나 worker1이 구동된지 딱 10초가 된 상황을 보니 manager1도 이제 막 업그레이드를 시작한 것을 볼 수 있다. 

만약 업데이트 중에 오류가 발생할 경우 inspect 명령을 통해 Update status에서 에러 메세지를 확인할 수 있고 `docker service update <SERVICE-ID>`명령을 통해 업데이트를 이어서 실행할 수 있다.