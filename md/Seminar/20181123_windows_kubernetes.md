# Journey to Windows Kubernetes

* 쿠키워즈에서 게임서버는 리눅스, 운영 도구는 윈도우


## Windows Kubernetes

* 기존의 쿠버네티스와 크게 다르지 않음
* 고성능의 IOCP를 사용할 수 있다는 장점
* 기존 레거시 시스템을 쿠버네티스 환경으로 전환하여 클러스터 관리를 편하게 할 수 있다는 장점
* 안정화까지는 다소 시간이 걸릴 것으로 예상 됨
  * Windows Server 2019에서 안정화 될 것으로 예상
* Windows Server VM 필요
* 설치과정이 네트워크 설정부터 각 종 raw level의 설정을 수동으로 했어야 했는데 Rancher를 사용하여 어느정도 자동화 가능
* Windows 10에서는 컨테이너 기술을 사용하려면 무조건 가상화 기술이 필요



## HCS(Host Compute Service)

* 쿠버네티스는 Docker REST API로 컨테이너 관리
* Docker는 HCS와 HNS로 커뮤니케이션
* 윈도우에서 컨테이너를 구동한다는 것은 커널에 있는 HCS를 사용해서 구동한다는 것



## HNS(Host Network Service)

* Windows에서 쿠버네티스를 사용할 때 팟마다 네트워크 인터페이스가 생성되어서 엄청나게 많은 네트워크가 생성되는 문제가 발생함
* 이러한 이슈를 해결하기 위해 한단계 상위의 네트워크를 구성하게 되었는데 이것이 HNS (잘 이해 못함)



## 그 외

* 윈도우 10과 윈도우 서버에서 구동되는 Docker는 다름
* Rancher를 사용하는 경우 CNI로 flannel을 권장.
  * linux와 windows가 다르게 구현되어 있음
* 컨테이너용 windows server os가 따로 있음
* 중첩 가상화를 사용할 수 있는 환경이 필요
  * Azure : 3세대 VM 이상
  * AWS: i3.baremetal 혹은 유사 인스턴스
  * 

## 질문

* 데브시스터즈에서 IOCP를 사용하는 이유
* 윈도우 컨테이너 성능