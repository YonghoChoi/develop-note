## Auto Scaling

### AMI 사용

* AMI를 생성하여 Auto Scaling Group에서 해당 AMI를 복제하여 Scale in/out
* EC2 Instance 목록에서 오토 스케일링이 필요한 인스턴스를 선택한후 오른쪽 버튼을 누르고 Create Image를 선택하여 AMI를 생성한다.



### Auto Scaling Group 생성 절차

1. Launch Configuration 생성
   * Auto Scaling 될 때 EC2 인스턴스를 생성 설정
2. Auto Scaling Group 생성
   * Group size : 오토 스케일링 인스턴스 시작 개수
   * Network : VPC 지정
3. EC2 인스턴스의 개수 조정
   * Keep this group at its initial size : 앞서 설정한 인스턴스 개수 유지
   * Use scaling policies to adjust the capacity of this group : 스케일링 정책에 따라 유동적으로 인스턴스 개수 변경
     * 알람 정책이 없는 경우 생성 버튼을 클릭하여 정책을 지정



### 오토 스케일링 테스트

