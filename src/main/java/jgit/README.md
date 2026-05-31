# JGit Repository TCP Server

TCP 패킷을 통해 클라이언트 요청을 입력받을 수 있는 JGit 저장소 애플리케이션 예제입니다.

## 프로젝트 구조

```
jgit/
├── docker/              # Docker 배포 관련 파일
│   ├── Dockerfile
│   ├── docker-compose.yml
│   ├── .dockerignore
│   └── DOCKER.md
├── server/              # TCP 서버 구현
│   ├── TcpServerBootstrap.java
│   ├── TcpServerHandler.java
│   └── GitRepositoryTcpServer.java
└── README.md            # 이 문서
```

## 구성 요소

### TcpServerBootstrap
- Netty 기반 TCP 서버를 초기화하고 관리하는 컴포넌트
- 다중 클라이언트 연결을 처리할 수 있는 EventLoop 그룹 운영
- StringCodec을 사용하여 텍스트 기반 통신 지원

### TcpServerHandler
- 각 클라이언트 연결에 대한 요청 처리 핸들러
- 클라이언트 접속/절단 로깅
- 수신한 메시지 처리 및 응답 전송

### GitRepositoryTcpServer
- 전체 서버 애플리케이션의 진입점
- 기본 포트: 9000 (명령행 인자로 변경 가능)

## 로컬 실행 방법

### 기본 포트(9000)로 서버 시작
```bash
./gradlew run
```

### 특정 포트로 서버 시작
```bash
java -cp build/libs/Laboratory.jar jgit.server.GitRepositoryTcpServer 8080
```

## 클라이언트 테스트

### netcat을 사용한 테스트
```bash
# 서버 연결
nc localhost 9000

# PING 요청
PING
# 응답: PONG

# 일반 메시지
hello world
# 응답: RECEIVED: hello world
```

### Telnet을 사용한 테스트
```bash
telnet localhost 9000
```

## Docker로 실행

### 빠른 시작

1. **이미지 빌드** (프로젝트 루트에서)
```bash
docker build -f src/main/java/jgit/docker/Dockerfile -t jgit-server:latest .
```

2. **컨테이너 실행**
```bash
docker run -d -p 9000:9000 --name jgit-server jgit-server:latest
```

3. **테스트**
```bash
# PING 테스트
echo "PING" | nc localhost 9000
# 응답: PONG

# 메시지 테스트
echo "hello docker" | nc localhost 9000
# 응답: RECEIVED: hello docker
```

4. **컨테이너 중지**
```bash
docker stop jgit-server
docker rm jgit-server
```

### Docker Compose로 실행

docker 디렉터리에서 한 명령으로 서비스를 시작할 수 있습니다.

```bash
cd src/main/java/jgit/docker

# 서비스 시작
docker-compose up -d

# 상태 확인
docker-compose ps

# PING 테스트
echo "PING" | nc localhost 9000

# 로그 확인
docker-compose logs -f jgit-server

# 서비스 중지
docker-compose down
```

### 테스트 스크립트

전체 테스트를 자동으로 수행하는 스크립트:

```bash
#!/bin/bash
set -e

echo "=== Docker 이미지 빌드 ==="
docker build -f src/main/java/jgit/docker/Dockerfile -t jgit-server:latest .

echo "=== 컨테이너 시작 ==="
docker run -d -p 9000:9000 --name jgit-test jgit-server:latest
sleep 2

echo "=== PING 테스트 ==="
(echo "PING"; sleep 0.2) | nc -w 1 localhost 9000

echo "=== 메시지 테스트 ==="
(echo "test message"; sleep 0.2) | nc -w 1 localhost 9000

echo "=== 빈 메시지 테스트 ==="
(echo ""; sleep 0.2) | nc -w 1 localhost 9000

echo "=== 컨테이너 로그 ==="
docker logs jgit-test | tail -10

echo "=== 정리 ==="
docker stop jgit-test
docker rm jgit-test

echo "=== 모든 테스트 완료 ==="
```

## 포트 변경

기본 포트 9000을 다른 포트로 변경하려면:

### 로컬 실행
```bash
java -cp build/libs/Laboratory.jar jgit.server.GitRepositoryTcpServer 8080
nc localhost 8080  # 접속 테스트
```

### Docker 컨테이너
```bash
docker run -d -p 8080:9000 --name jgit-server jgit-server:latest
echo "PING" | nc localhost 8080
```

## 문제 해결

### 포트가 이미 사용 중인 경우
```bash
# 포트 사용 확인
lsof -i :9000

# 다른 포트 사용
docker run -d -p 9001:9000 --name jgit-server jgit-server:latest
```

### 컨테이너 로그 확인
```bash
docker logs jgit-server
docker logs -f jgit-server  # 실시간 로그
```

### 컨테이너 내부 접근
```bash
docker exec -it jgit-server /bin/bash
```

## 향후 확장 기능

- JGit을 사용한 저장소 명령어 처리 (clone, log, status 등)
- 요청/응답 프로토콜 정의
- 저장소 관리 기능 구현
- 인증 및 권한 관리
- 멀티 스레드 요청 처리 최적화

## 참고 문서

- [Docker 배포 가이드](docker/DOCKER.md)
- [build.gradle](../../build.gradle)
