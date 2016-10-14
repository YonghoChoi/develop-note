# 리눅스 명령어

## 네트워크

* 현재 열려있는 Port 확인

  ```shell
  $ netstat -tnlp
  ```

  ```shell
  $ netstat -tnlp | grep -v 127.0.0.1 | sed 's/:::/0 /g' | sed 's/[:\/]/ /g' | awk '{print $5"\t"$10}' | sort -ug
  ```

  ```shell
  $ lsof -i -nP | grep LISTEN | awk '{print $(NF-1)" "$1}' | sort -u
  ```

  ```shell
  $ nmap localhost
  ```

  ​

