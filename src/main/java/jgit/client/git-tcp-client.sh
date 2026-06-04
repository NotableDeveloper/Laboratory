#!/bin/bash

HOST="${GIT_TCP_HOST:-localhost}"
PORT="${GIT_TCP_PORT:-9000}"

if [ $# -eq 0 ]; then
    echo "╔════════════════════════════════════════╗"
    echo "║      Git TCP Client                    ║"
    echo "╚════════════════════════════════════════╝"
    echo ""
    echo "사용법: ./git-tcp-client.sh <command> [parameters...]"
    echo ""
    echo "명령어:"
    echo "  PING"
    echo "  INIT <repo-path> [remote-url]"
    echo "  ADD <repo-path> <file1> [file2...]"
    echo "  COMMIT <repo-path> <author> <email> <message> <file1> [file2...]"
    echo "  STATUS <repo-path>"
    echo "  PUSH <repo-path> [remote]"
    echo "  PULL <repo-path> [remote] [branch]"
    echo ""
    echo "예제:"
    echo "  ./git-tcp-client.sh PING"
    echo "  ./git-tcp-client.sh INIT /app/data/my-repo"
    echo "  ./git-tcp-client.sh ADD /app/data/my-repo file.txt"
    echo "  ./git-tcp-client.sh COMMIT /app/data/my-repo John 'john@test.com' 'Init' file.txt"
    echo "  ./git-tcp-client.sh STATUS /app/data/my-repo"
    echo "  ./git-tcp-client.sh PUSH /app/data/my-repo origin"
    echo "  ./git-tcp-client.sh PULL /app/data/my-repo origin"
    echo ""
    echo "환경 변수:"
    echo "  GIT_TCP_HOST (기본값: localhost)"
    echo "  GIT_TCP_PORT (기본값: 9000)"
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
