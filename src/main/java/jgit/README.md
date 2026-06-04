# JGit Repository TCP Server

Netty와 JGit을 이용하여 TCP 프로토콜을 통해 Git 저장소 명령을 원격에서 실행할 수 있는 서버 애플리케이션입니다.

## 프로젝트 구조

```
jgit/
├── docker/              # Docker 배포 관련 파일
│   ├── Dockerfile
│   ├── docker-compose.yml
│   ├── .dockerignore
│   └── init-script.sh
├── server/              # TCP 서버 및 명령 처리
│   ├── TcpServerBootstrap.java      # Netty TCP 서버 초기화
│   ├── TcpServerHandler.java        # 기존 단순 메시지 핸들러
│   ├── GitCommandHandler.java       # Git 명령어 처리 핸들러
│   └── GitRepositoryTcpServer.java  # 메인 진입점
├── command/             # Git 명령 처리 계층
│   ├── CommandProcessor.java        # 명령 처리기 추상 클래스
│   ├── InitProcessor.java           # git init
│   ├── AddProcessor.java            # git add
│   ├── CommitProcessor.java         # git commit
│   ├── StatusProcessor.java         # git status
│   ├── PushProcessor.java           # git push
│   └── PullProcessor.java           # git pull
├── git/                 # Git 저장소 관리
│   ├── GitRepositoryManager.java    # JGit을 사용한 저장소 관리
│   ├── CommitInfo.java              # 커밋 정보 DTO
│   └── FileValidator.java           # 파일 경로 검증
├── protocol/            # 통신 프로토콜
│   ├── RequestMessage.java          # 요청 메시지 파싱
│   └── ResponseMessage.java         # 응답 메시지 생성
├── exception/           # 예외 처리
│   └── GitOperationException.java   # Git 작업 예외
├── client/              # TCP 클라이언트
│   ├── git-tcp-client.sh            # Bash 클라이언트 스크립트
│   └── file.txt
└── README.md            # 이 문서
```

## 핵심 컴포넌트

### 서버 계층 (server/)
- **TcpServerBootstrap**: Netty 기반 TCP 서버 초기화/관리, EventLoop 그룹 운영
- **GitCommandHandler**: 파이프(|) 구분 프로토콜로 Git 명령어 처리 및 라우팅
- **GitRepositoryTcpServer**: 메인 진입점, 포트 설정(기본값: 9000), 우아한 종료 처리

### 명령 처리 계층 (command/)
CommandProcessor를 상속한 6개의 명령어 처리기:
- **InitProcessor**: 저장소 초기화 (원격 URL 선택)
- **AddProcessor**: 파일을 스테이징 영역에 추가
- **CommitProcessor**: 커밋 생성 (작성자 정보 포함)
- **StatusProcessor**: 저장소 상태 조회 (브랜치, 커밋 수, dirty 상태)
- **PushProcessor**: 원격 저장소로 푸시
- **PullProcessor**: 원격 저장소에서 풀 (기본/지정 원격, 선택 브랜치)

### Git 관리 계층 (git/)
- **GitRepositoryManager**: JGit API를 사용한 저장소 연산 수행
  - init/add/commit/status/push/pull 지원
  - 저장소 경로 유효성 검증
  - PersonIdent를 통한 작성자 정보 관리
  - 병합 결과 추적
- **FileValidator**: 보안을 위한 경로 검증 (경로 탈출 방지)

### 프로토콜 (protocol/)
요청 메시지 포맷: `명령어|파라미터1|파라미터2|...`
- INIT, ADD, COMMIT, STATUS, PUSH, PULL 등의 명령어
- 파라미터는 파이프(|)로 구분
- 응답은 JSON 형식 또는 프로토콜 문자열

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

### 1. netcat을 사용한 테스트

#### 저장소 초기화
```bash
echo "INIT|/tmp/test-repo" | nc localhost 9000
# 응답 예시: {"status":"success","data":"REPO_INITIALIZED:/tmp/test-repo"}

# 원격 URL과 함께 초기화
echo "INIT|/tmp/test-repo|https://github.com/user/repo.git" | nc localhost 9000
```

#### 상태 조회
```bash
echo "STATUS|/tmp/test-repo" | nc localhost 9000
# 응답 예시: {"branch":"master","commitCount":0,"dirty":false}
```

#### 원격 저장소에서 풀
```bash
# 기본 원격(origin)에서 풀
echo "PULL|/tmp/test-repo" | nc localhost 9000
# 응답 예시: {"status":"success","data":"Merged 2 commit(s) from origin"}

# 특정 원격에서 풀
echo "PULL|/tmp/test-repo|upstream" | nc localhost 9000

# 특정 브랜치에서 풀
echo "PULL|/tmp/test-repo|origin|develop" | nc localhost 9000
```

### 2. git-tcp-client.sh를 사용한 테스트

클라이언트 스크립트를 실행 가능하게 만들고 사용:
```bash
chmod +x src/main/java/jgit/client/git-tcp-client.sh

# 저장소 초기화
./src/main/java/jgit/client/git-tcp-client.sh INIT /tmp/test-repo

# 저장소 상태 조회
./src/main/java/jgit/client/git-tcp-client.sh STATUS /tmp/test-repo
```

### 3. Telnet을 사용한 대화형 테스트
```bash
telnet localhost 9000
INIT|/tmp/test-repo
STATUS|/tmp/test-repo
# Ctrl+C로 종료
```

## Docker로 실행

### 이미지 빌드

프로젝트 루트에서:
```bash
docker build -f src/main/java/jgit/docker/Dockerfile -t jgit-server:latest .
```

### 컨테이너 실행

```bash
# 기본 포트(9000)로 실행
docker run -d -p 9000:9000 --name jgit-server jgit-server:latest

# 다른 포트로 실행
docker run -d -p 8080:9000 --name jgit-server jgit-server:latest

# 리포지토리 디렉터리 마운트 (데이터 영속화)
docker run -d -p 9000:9000 -v /host/repos:/app/repos --name jgit-server jgit-server:latest
```

### Docker Compose로 실행

docker 디렉터리에서:
```bash
cd src/main/java/jgit/docker

# 서비스 시작
docker-compose up -d

# 상태 확인
docker-compose ps

# 로그 확인
docker-compose logs -f jgit-server

# 서비스 중지
docker-compose down
```

### 테스트

```bash
# 저장소 초기화
echo "INIT|/app/repos/test-repo" | nc localhost 9000

# 저장소 상태 조회
echo "STATUS|/app/repos/test-repo" | nc localhost 9000
```

### 컨테이너 로그 확인
```bash
docker logs -f jgit-server
```

### 컨테이너 내부 접근
```bash
docker exec -it jgit-server /bin/bash
```

## 문제 해결

### 포트가 이미 사용 중인 경우
```bash
# 포트 사용 확인
lsof -i :9000

# 다른 포트로 실행
java -cp build/libs/Laboratory.jar jgit.server.GitRepositoryTcpServer 8080
docker run -d -p 9001:9000 --name jgit-server jgit-server:latest
```

### 연결 거부 오류
```bash
# 서버가 실행 중인지 확인
netstat -an | grep 9000

# 방화벽 확인 (macOS)
sudo lsof -i :9000
```

### 명령어 파싱 오류
요청 메시지 형식을 확인하세요:
- 명령어는 대문자 (INIT, STATUS, ADD, COMMIT, PUSH, PULL)
- 파라미터는 파이프(|)로 구분
- 예: `INIT|/tmp/repo` 또는 `STATUS|/tmp/repo`

## 명령어 레퍼런스

### 기본 명령어

| 명령어 | 파라미터                                                                | 설명 |
|--------|---------------------------------------------------------------------|------|
| INIT | `<repo-path>` `<remoteUrl>`                                          | 저장소 초기화 |
| STATUS | `<repo-path>`                                                       | 저장소 상태 조회 |
| ADD | `<repo-path>` `<file1>` `<file2...>`                                | 파일 스테이징 |
| COMMIT | `<repo-path>` `<author>` `<email>` `<message>` `<file1>` [file2...] | 커밋 생성 |
| PUSH | `<repo-path>` [remote]                                              | 원격 저장소로 푸시 |
| PULL | `<repo-path>` [remote] [branch]                                     | 원격 저장소에서 풀 |

### 응답 형식

성공:
```json
{"status":"success","data":"..."}
```

실패:
```json
{"status":"error","errorCode":"INVALID_PARAMS","message":"..."}
```
