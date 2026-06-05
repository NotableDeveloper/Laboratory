#!/bin/bash

HOST="${GIT_TCP_HOST:-localhost}"
PORT="${GIT_TCP_PORT:-9000}"

load_config() {
    local repo_path="$1"
    local config_file="$repo_path/.git-tcp-config"

    if [ ! -f "$config_file" ]; then
        echo "ERROR: Config file not found at $config_file. Run INIT command first."
        exit 1
    fi

    source "$config_file"
}

read_config() {
    local repo_path="$1"
    local config_file="$repo_path/.git-tcp-config"
    local existing_author=""
    local existing_email=""
    local existing_remote_url=""
    local existing_default_remote=""

    if [ -f "$config_file" ]; then
        source "$config_file"
        existing_author="$AUTHOR"
        existing_email="$EMAIL"
        existing_remote_url="$REMOTE_URL"
        existing_default_remote="$DEFAULT_REMOTE"
    fi

    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    if [ -f "$config_file" ]; then
        echo "Edit existing configuration:"
    else
        echo "Enter repository configuration:"
    fi
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

    read -p "Author name [$existing_author]: " input_author
    AUTHOR="${input_author:-$existing_author}"
    if [ -z "$AUTHOR" ]; then
        echo "ERROR: Author name is required."
        exit 1
    fi

    read -p "Author email [$existing_email]: " input_email
    EMAIL="${input_email:-$existing_email}"
    if [ -z "$EMAIL" ]; then
        echo "ERROR: Author email is required."
        exit 1
    fi

    read -p "Remote repository URL (optional) [$existing_remote_url]: " input_remote_url
    REMOTE_URL="${input_remote_url:-$existing_remote_url}"

    read -p "Default remote name [$existing_default_remote]: " input_default_remote
    DEFAULT_REMOTE="${input_default_remote:-$existing_default_remote}"
    if [ -z "$DEFAULT_REMOTE" ]; then
        DEFAULT_REMOTE="origin"
    fi

    echo ""
}

save_config() {
    local repo_path="$1"
    local config_file="$repo_path/.git-tcp-config"

    cat > "$config_file" << EOF
REPO_PATH=${repo_path}
AUTHOR=${AUTHOR}
EMAIL=${EMAIL}
REMOTE_URL=${REMOTE_URL}
DEFAULT_REMOTE=${DEFAULT_REMOTE}
EOF

    echo "Config saved to $config_file"
}

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
    You can modify existing settings by running INIT again.
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

Config File (.git-tcp-config):

Created interactively when running INIT command. Stored in repository root.
You can modify existing settings by running INIT again.
File content:
  REPO_PATH=/path/to/repository
  AUTHOR=author name
  EMAIL=email address
  REMOTE_URL=remote repository URL (optional)
  DEFAULT_REMOTE=origin (default remote name for PUSH/PULL)

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

        mkdir -p "$repo_path"
        read_config "$repo_path"
        save_config "$repo_path"

        msg="INIT|$repo_path"
        if [ -n "$REMOTE_URL" ]; then
            msg="$msg|$REMOTE_URL"
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
