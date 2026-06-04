package jgit.command;

import jgit.exception.GitOperationException;
import jgit.exception.GitOperationException.ErrorCode;
import jgit.git.GitRepositoryManager;
import jgit.git.GitRepositoryManager.PushResult;
import jgit.protocol.RequestMessage;
import jgit.protocol.ResponseMessage;

public class PushProcessor extends CommandProcessor {
    private final GitRepositoryManager gitManager;
    private static final String DEFAULT_REMOTE = "origin";

    public PushProcessor(GitRepositoryManager gitManager) {
        super("PUSH");
        this.gitManager = gitManager;
    }

    @Override
    public ResponseMessage process(RequestMessage request) throws GitOperationException {
        if (request.getParameterCount() < 1) {
            throw new GitOperationException(
                ErrorCode.INVALID_PARAMS,
                "PUSH requires: repoPath [remoteName]"
            );
        }

        String repoPath = request.getParameter(0);
        String remoteName = request.getParameterCount() > 1 && request.getParameter(1) != null
            ? request.getParameter(1)
            : DEFAULT_REMOTE;

        if (repoPath == null || repoPath.isEmpty()) {
            throw new GitOperationException(
                ErrorCode.INVALID_PARAMS,
                "Repository path is required"
            );
        }

        PushResult result = gitManager.push(repoPath, remoteName);
        return ResponseMessage.success(
            String.valueOf(result.getRefCount()),
            result.getBranch()
        );
    }
}
