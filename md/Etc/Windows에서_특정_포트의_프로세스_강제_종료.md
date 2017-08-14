# Windows에서 특정 포트의 프로세스 강제 종료

1. 8080 포트를 가진 pid 검색

   ```
   > netstat -ano | findstr 8080
   ```

2. 해당 pid Kill

   ```
   > taskkill /pid <PID>
   ```

3. 종료가 안되는 경우 강제종료

   ```
   > taskkill /F /pid <PID>
   ```

   ​