package jgit.command;

import jgit.exception.GitOperationException;
import jgit.exception.GitOperationException.ErrorCode;
import jgit.git.GitRepositoryManager;
import jgit.git.GitRepositoryManager.RepositoryStatus;
import jgit.protocol.RequestMessage;
import jgit.protocol.ResponseMessage;

public class StatusProcessor extends CommandProcessor {
    private final GitRepositoryManager gitManager;

    public StatusProcessor(GitRepositoryManager gitManager) {
        super("STATUS");
        this.gitManager = gitManager;
    }

    @Override
    public ResponseMessage process(RequestMessage request) throws GitOperationException {
        if (request.getParameterCount() < 1) {
            throw new GitOperationException(
                ErrorCode.INVALID_PARAMS,
                "STATUS requires: repoPath"
            );
        }

        String repoPath = request.getParameter(0);
        if (repoPath == null || repoPath.isEmpty()) {
            throw new GitOperationException(
                ErrorCode.INVALID_PARAMS,
                "Repository path is required"
            );
        }

        RepositoryStatus status = gitManager.getStatus(repoPath);
        String statusInfo = String.format(
            "BRANCH=%s|COMMITS=%d|DIRTY=%s",
            status.getBranch(),
            status.getCommitCount(),
            status.isDirty()
        );

        return ResponseMessage.success(statusInfo);
    }
}
