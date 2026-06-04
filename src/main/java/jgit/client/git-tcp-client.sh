#!/bin/bash

HOST="${GIT_TCP_HOST:-localhost}"
PORT="${GIT_TCP_PORT:-9000}"

show_help() {
    cat <<'HELP'
╔═══════════════════════════════════════════════════════════════╗
║           Git TCP Client - Git 저장소 원격 관리               ║
╚═══════════════════════════════════════════════════════════════╝

사용법: ./git-tcp-client.sh <command> [parameters...]

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

명령어 목록:

  PING
    서버 연결 상태를 확인합니다.
    사용법: ./git-tcp-client.sh PING

  INIT <repo-path> [remote-url]
    새로운 Git 저장소를 초기화합니다.
    - repo-path: 저장소 경로 (필수)
    - remote-url: 원격 저장소 URL (선택, 기본값 없음)
    예제:
      ./git-tcp-client.sh INIT /app/data/my-repo
      ./git-tcp-client.sh INIT /app/data/my-repo https://github.com/user/repo.git

  ADD <repo-path> <file1> [file2...]
    파일들을 스테이징 영역에 추가합니다 (git add).
    - repo-path: 저장소 경로 (필수)
    - file1, file2...: 추가할 파일 경로들 (필수, 여러 개 가능)
    예제:
      ./git-tcp-client.sh ADD /app/data/my-repo file.txt
      ./git-tcp-client.sh ADD /app/data/my-repo file1.txt file2.txt file3.txt

  COMMIT <repo-path> <author> <email> <message> <file1> [file2...]
    스테이징된 파일들을 커밋합니다 (git commit).
    - repo-path: 저장소 경로 (필수)
    - author: 작성자 이름 (필수)
    - email: 작성자 이메일 (필수)
    - message: 커밋 메시지 (필수)
    - file1, file2...: 커밋할 파일 경로들 (필수, 여러 개 가능)
    예제:
      ./git-tcp-client.sh COMMIT /app/data/my-repo John 'john@example.com' 'Initial commit' file.txt
      ./git-tcp-client.sh COMMIT /app/data/my-repo Alice 'alice@example.com' 'Add features' file1.txt file2.txt

  STATUS <repo-path>
    저장소의 현재 상태를 조회합니다 (git status).
    - repo-path: 저장소 경로 (필수)
    반환값: 현재 브랜치, 커밋 수, 작업 트리 상태
    예제:
      ./git-tcp-client.sh STATUS /app/data/my-repo

  PUSH <repo-path> [remote]
    로컬 저장소의 커밋을 원격 저장소로 푸시합니다 (git push).
    - repo-path: 저장소 경로 (필수)
    - remote: 원격 저장소 이름 (선택, 기본값: origin)
    예제:
      ./git-tcp-client.sh PUSH /app/data/my-repo
      ./git-tcp-client.sh PUSH /app/data/my-repo origin
      ./git-tcp-client.sh PUSH /app/data/my-repo upstream

  PULL <repo-path> [remote] [branch]
    원격 저장소의 변경사항을 가져와 병합합니다 (git pull).
    - repo-path: 저장소 경로 (필수)
    - remote: 원격 저장소 이름 (선택, 기본값: origin)
    - branch: 브랜치 이름 (선택, 기본값: 현재 브랜치)
    예제:
      ./git-tcp-client.sh PULL /app/data/my-repo
      ./git-tcp-client.sh PULL /app/data/my-repo origin
      ./git-tcp-client.sh PULL /app/data/my-repo origin main
      ./git-tcp-client.sh PULL /app/data/my-repo upstream develop

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

환경 변수:

  GIT_TCP_HOST   서버 호스트 (기본값: localhost)
  GIT_TCP_PORT   서버 포트 (기본값: 9000)

사용 예:
  GIT_TCP_HOST=192.168.1.100 GIT_TCP_PORT=8080 ./git-tcp-client.sh PING

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

응답 형식:

  성공: SUCCESS|<명령어>|<데이터>
  실패: ERROR|<에러코드>|<에러메시지>

예:
  SUCCESS|PONG
  SUCCESS|REPO_INITIALIZED|/app/data/my-repo
  ERROR|INVALID_PARAMS|Repository path is required

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
HELP
}

if [ $# -eq 0 ]; then
    show_help
    exit 0
fi

# 파라미터를 파이프로 구분된 문자열로 변환
cmd=$(IFS='|'; echo "$*")

# 서버에 명령어 전송
exec 3<>/dev/tcp/$HOST/$PORT 2>/dev/null
if [ $? -ne 0 ]; then
    echo "ERROR: Cannot connect to $HOST:$PORT"
    exit 1
fi

echo "$cmd" >&3
timeout 2 cat <&3
exit_code=$?
exec 3>&-

exit $exit_code
