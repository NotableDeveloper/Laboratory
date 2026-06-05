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

  ADD <file1> [file2...]
    Add files to the staging area (git add).
    Repository path is read from config file (.git-tcp-config).
    - file1, file2...: File paths to add (required, multiple allowed)
    Example:
      ./git-tcp-client.sh ADD file.txt
      ./git-tcp-client.sh ADD file1.txt file2.txt file3.txt

  COMMIT <message> <file1> [file2...]
    Commit staged files (git commit).
    Repository path and author info are automatically read from config file.
    - message: Commit message (required)
    - file1, file2...: File paths to commit (required, multiple allowed)
    Example:
      ./git-tcp-client.sh COMMIT 'Initial commit' file.txt
      ./git-tcp-client.sh COMMIT 'Add features' file1.txt file2.txt

  STATUS
    Show repository status (git status).
    Repository path is read from config file (.git-tcp-config).
    Return: Current branch, commit count, working tree status
    Example:
      ./git-tcp-client.sh STATUS

  PUSH [--remote <name>]
    Push commits to remote repository (git push).
    Repository path and default remote are read from config file.
    - --remote: Remote name (optional, overrides default remote)
    Example:
      ./git-tcp-client.sh PUSH
      ./git-tcp-client.sh PUSH --remote origin
      ./git-tcp-client.sh PUSH --remote upstream

  PULL [--remote <name>] [--branch <name>]
    Pull and merge changes from remote repository (git pull).
    Repository path and default remote are read from config file.
    - --remote: Remote name (optional, overrides default remote)
    - --branch: Branch name (optional, default: current branch)
    Example:
      ./git-tcp-client.sh PULL
      ./git-tcp-client.sh PULL --remote origin
      ./git-tcp-client.sh PULL --remote origin --branch main

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
        if [ $# -lt 1 ]; then
            echo "ERROR: file(s) are required"
            exit 1
        fi

        load_config "$REPO_PATH"

        msg="ADD|$REPO_PATH|$(IFS='|'; echo "$*")"
        send_command "$msg"
        ;;

    COMMIT)
        if [ $# -lt 2 ]; then
            echo "ERROR: message and file(s) are required"
            exit 1
        fi

        message="$1"
        shift

        load_config "$REPO_PATH"

        msg="COMMIT|$REPO_PATH|$AUTHOR|$EMAIL|$message|$(IFS='|'; echo "$*")"
        send_command "$msg"
        ;;

    STATUS)
        load_config "$REPO_PATH"

        msg="STATUS|$REPO_PATH"
        send_command "$msg"
        ;;

    PUSH)
        remote="$DEFAULT_REMOTE"
        while [ $# -gt 0 ]; do
            case "$1" in
                --remote)
                    remote="$2"
                    shift 2
                    ;;
                *)
                    echo "ERROR: Unknown option $1"
                    exit 1
                    ;;
            esac
        done

        load_config "$REPO_PATH"

        msg="PUSH|$REPO_PATH|$remote"
        send_command "$msg"
        ;;

    PULL)
        remote="$DEFAULT_REMOTE"
        branch=""

        while [ $# -gt 0 ]; do
            case "$1" in
                --remote)
                    remote="$2"
                    shift 2
                    ;;
                --branch)
                    branch="$2"
                    shift 2
                    ;;
                *)
                    echo "ERROR: Unknown option $1"
                    exit 1
                    ;;
            esac
        done

        load_config "$REPO_PATH"

        msg="PULL|$REPO_PATH|$remote"
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
