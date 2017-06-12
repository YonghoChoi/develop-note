# Mac OS 초기화

1. 부팅 시 option + Command + R 버튼을 누르고 있는다.
2. GUI 환경에서 디스크를 초기화 하고 설치 시 오류가 발생할 경우 아래 과정 수행
3. 유틸리티 -> 터미널 실행
4. `diskutil cs list` 명령을 통해 하위에 disk가 마운트 되어 있으면 ummount
   * 거진 disk1이 마운트 되어 있음
   * `diskutil unmount force /dev/disk1`
5. core storage 제거
   * `diskutil cs delete <UUID>`
     * UUID는 diskutil cs list를 했을때 출력되는 가장 상위의 Logical Volume Group ID
6. `diskutil erasedisk jhfs+ "디스크이름" disk0`으로 초기화
7. 다시 OSX 설치 진행



## 참고

* http://blog.naver.com/hankboy/220221196779
* https://apple.stackexchange.com/questions/136590/how-can-i-delete-a-partition-corestorage-logical-volume-from-the-terminal