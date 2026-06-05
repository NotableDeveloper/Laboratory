#!/bin/bash

HOST="${GIT_TCP_HOST:-localhost}"
PORT="${GIT_TCP_PORT:-9000}"

show_help() {
    cat <<'HELP'
╔═══════════════════════════════════════════════════════════════╗
║           Git TCP Client - Remote Git Repository Manager      ║
╚═══════════════════════════════════════════════════════════════╝

Usage: ./git-tcp-client.sh <command> [parameters...]

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Commands:

  INIT <repo-path>
    Initialize a new Git repository and configure settings interactively.
    - repo-path: Repository path (required)
    Example:
      ./git-tcp-client.sh INIT /app/data/my-repo

  ADD <repo-path> <file1> [file2...]
    Add files to the staging area (git add).
    - repo-path: Repository path (required)
    - file1, file2...: File paths to add (required, multiple allowed)
    Example:
      ./git-tcp-client.sh ADD /app/data/my-repo file.txt
      ./git-tcp-client.sh ADD /app/data/my-repo file1.txt file2.txt file3.txt

  COMMIT <repo-path> <author> <email> <message> <file1> [file2...]
    Commit staged files (git commit).
    - repo-path: Repository path (required)
    - author: Author name (required)
    - email: Author email (required)
    - message: Commit message (required)
    - file1, file2...: File paths to commit (required, multiple allowed)
    Example:
      ./git-tcp-client.sh COMMIT /app/data/my-repo John 'john@example.com' 'Initial commit' file.txt
      ./git-tcp-client.sh COMMIT /app/data/my-repo Alice 'alice@example.com' 'Add features' file1.txt file2.txt

  STATUS <repo-path>
    Show repository status (git status).
    - repo-path: Repository path (required)
    Return: Current branch, commit count, working tree status
    Example:
      ./git-tcp-client.sh STATUS /app/data/my-repo

  PUSH <repo-path> [remote]
    Push commits to remote repository (git push).
    - repo-path: Repository path (required)
    - remote: Remote name (optional, default: origin)
    Example:
      ./git-tcp-client.sh PUSH /app/data/my-repo
      ./git-tcp-client.sh PUSH /app/data/my-repo origin
      ./git-tcp-client.sh PUSH /app/data/my-repo upstream

  PULL <repo-path> [remote] [branch]
    Pull and merge changes from remote repository (git pull).
    - repo-path: Repository path (required)
    - remote: Remote name (optional, default: origin)
    - branch: Branch name (optional, default: current branch)
    Example:
      ./git-tcp-client.sh PULL /app/data/my-repo
      ./git-tcp-client.sh PULL /app/data/my-repo origin
      ./git-tcp-client.sh PULL /app/data/my-repo origin main
      ./git-tcp-client.sh PULL /app/data/my-repo upstream develop

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Environment Variables:

  GIT_TCP_HOST   Server host (default: localhost)
  GIT_TCP_PORT   Server port (default: 9000)

Example:
  GIT_TCP_HOST=192.168.1.100 GIT_TCP_PORT=8080 ./git-tcp-client.sh INIT /app/data/repo

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Response Format:

  Success: SUCCESS|<command>|<data>
  Error: ERROR|<error-code>|<error-message>

Example:
  SUCCESS|REPO_INITIALIZED|/app/data/my-repo
  ERROR|INVALID_PARAMS|Repository path is required

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
HELP
}

if [ $# -eq 0 ]; then
    show_help
    exit 0
fi

COMMAND="$1"
shift

send_command() {
    local msg="$1"
    exec 3<>/dev/tcp/$HOST/$PORT 2>/dev/null
    if [ $? -ne 0 ]; then
        echo "ERROR: Cannot connect to $HOST:$PORT"
        exit 1
    fi

    echo "$msg" >&3
    timeout 2 cat <&3
    exit_code=$?
    exec 3>&-

    exit $exit_code
}

case "$COMMAND" in
    INIT)
        if [ $# -lt 1 ]; then
            echo "ERROR: repo-path is required"
            exit 1
        fi

        repo_path="$1"

        msg="INIT|$repo_path"
        if [ $# -gt 1 ]; then
            msg="$msg|$2"
        fi

        send_command "$msg"
        ;;

    ADD)
        if [ $# -lt 2 ]; then
            echo "ERROR: repo-path and file(s) are required"
            exit 1
        fi

        repo_path="$1"
        shift

        msg="ADD|$repo_path|$(IFS='|'; echo "$*")"
        send_command "$msg"
        ;;

    COMMIT)
        if [ $# -lt 4 ]; then
            echo "ERROR: repo-path, author, email, message, and file(s) are required"
            exit 1
        fi

        repo_path="$1"
        author="$2"
        email="$3"
        message="$4"
        shift 4

        msg="COMMIT|$repo_path|$author|$email|$message|$(IFS='|'; echo "$*")"
        send_command "$msg"
        ;;

    STATUS)
        if [ $# -lt 1 ]; then
            echo "ERROR: repo-path is required"
            exit 1
        fi

        msg="STATUS|$1"
        send_command "$msg"
        ;;

    PUSH)
        if [ $# -lt 1 ]; then
            echo "ERROR: repo-path is required"
            exit 1
        fi

        repo_path="$1"
        remote="${2:-origin}"

        msg="PUSH|$repo_path|$remote"
        send_command "$msg"
        ;;

    PULL)
        if [ $# -lt 1 ]; then
            echo "ERROR: repo-path is required"
            exit 1
        fi

        repo_path="$1"
        remote="${2:-origin}"
        branch="${3:-}"

        msg="PULL|$repo_path|$remote"
        if [ -n "$branch" ]; then
            msg="$msg|$branch"
        fi

        send_command "$msg"
        ;;

    *)
        echo "ERROR: Unknown command $COMMAND"
        show_help
        exit 1
        ;;
esac
