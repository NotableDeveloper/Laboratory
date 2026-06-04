package jgit.command;

import jgit.exception.GitOperationException;
import jgit.exception.GitOperationException.ErrorCode;
import jgit.git.CommitInfo;
import jgit.git.GitRepositoryManager;
import jgit.protocol.RequestMessage;
import jgit.protocol.ResponseMessage;

public class CommitProcessor extends CommandProcessor {
    private final GitRepositoryManager gitManager;

    public CommitProcessor(GitRepositoryManager gitManager) {
        super("COMMIT");
        this.gitManager = gitManager;
    }

    @Override
    public ResponseMessage process(RequestMessage request) throws GitOperationException {
        if (request.getParameterCount() < 4) {
            throw new GitOperationException(
                ErrorCode.INVALID_PARAMS,
                "COMMIT requires: repoPath|author|email|message"
            );
        }

        String repoPath = request.getParameter(0);
        String author = request.getParameter(1);
        String email = request.getParameter(2);
        String message = request.getParameter(3);

        if (repoPath == null || repoPath.isEmpty()) {
            throw new GitOperationException(
                ErrorCode.INVALID_PARAMS,
                "Repository path is required"
            );
        }

        if (author == null || author.isEmpty()) {
            throw new GitOperationException(
                ErrorCode.AUTHOR_REQUIRED,
                "Author name is required"
            );
        }

        if (email == null || email.isEmpty()) {
            throw new GitOperationException(
                ErrorCode.AUTHOR_REQUIRED,
                "Author email is required"
            );
        }

        if (message == null || message.isEmpty()) {
            throw new GitOperationException(
                ErrorCode.MESSAGE_REQUIRED,
                "Commit message is required"
            );
        }

        CommitInfo commitInfo = gitManager.commit(repoPath, author, email, message);
        return ResponseMessage.success(commitInfo.getCommitHash(), author);
    }
}
