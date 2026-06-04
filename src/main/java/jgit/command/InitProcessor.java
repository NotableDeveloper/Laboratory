package jgit.command;

import jgit.exception.GitOperationException;
import jgit.exception.GitOperationException.ErrorCode;
import jgit.git.GitRepositoryManager;
import jgit.protocol.RequestMessage;
import jgit.protocol.ResponseMessage;

public class InitProcessor extends CommandProcessor {
    private final GitRepositoryManager gitManager;

    public InitProcessor(GitRepositoryManager gitManager) {
        super("INIT");
        this.gitManager = gitManager;
    }

    @Override
    public ResponseMessage process(RequestMessage request) throws GitOperationException {
        if (request.getParameterCount() < 1) {
            throw new GitOperationException(
                ErrorCode.INVALID_PARAMS,
                "INIT requires: repoPath [remoteUrl]"
            );
        }

        String repoPath = request.getParameter(0);
        String remoteUrl = request.getParameterCount() > 1 ? request.getParameter(1) : null;

        if (repoPath == null || repoPath.isEmpty()) {
            throw new GitOperationException(
                ErrorCode.INVALID_PARAMS,
                "Repository path is required"
            );
        }

        gitManager.initRepository(repoPath, remoteUrl);
        return ResponseMessage.success("REPO_INITIALIZED", repoPath);
    }
}
