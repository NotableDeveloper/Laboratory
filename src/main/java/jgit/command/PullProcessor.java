package jgit.command;

import jgit.exception.GitOperationException;
import jgit.exception.GitOperationException.ErrorCode;
import jgit.git.GitRepositoryManager;
import jgit.git.GitRepositoryManager.PullResult;
import jgit.protocol.RequestMessage;
import jgit.protocol.ResponseMessage;

public class PullProcessor extends CommandProcessor {
    private final GitRepositoryManager gitManager;
    private static final String DEFAULT_REMOTE = "origin";

    public PullProcessor(GitRepositoryManager gitManager) {
        super("PULL");
        this.gitManager = gitManager;
    }

    @Override
    public ResponseMessage process(RequestMessage request) throws GitOperationException {
        if (request.getParameterCount() < 1) {
            throw new GitOperationException(
                ErrorCode.INVALID_PARAMS,
                "PULL requires: repoPath [remoteName] [branchName]"
            );
        }

        String repoPath = request.getParameter(0);
        String remoteName = request.getParameterCount() > 1 && request.getParameter(1) != null
            ? request.getParameter(1)
            : DEFAULT_REMOTE;
        String branchName = request.getParameterCount() > 2 ? request.getParameter(2) : null;

        if (repoPath == null || repoPath.isEmpty()) {
            throw new GitOperationException(
                ErrorCode.INVALID_PARAMS,
                "Repository path is required"
            );
        }

        PullResult result = gitManager.pull(repoPath, remoteName, branchName);
        return ResponseMessage.success(
            String.format("Merged %d commit(s) from %s", result.getMergedCommitCount(), remoteName),
            result.getBranch()
        );
    }
}
