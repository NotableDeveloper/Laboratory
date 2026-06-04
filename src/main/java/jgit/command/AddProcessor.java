package jgit.command;

import jgit.exception.GitOperationException;
import jgit.exception.GitOperationException.ErrorCode;
import jgit.git.GitRepositoryManager;
import jgit.protocol.RequestMessage;
import jgit.protocol.ResponseMessage;

public class AddProcessor extends CommandProcessor {
    private final GitRepositoryManager gitManager;

    public AddProcessor(GitRepositoryManager gitManager) {
        super("ADD");
        this.gitManager = gitManager;
    }

    @Override
    public ResponseMessage process(RequestMessage request) throws GitOperationException {
        if (request.getParameterCount() < 2) {
            throw new GitOperationException(
                ErrorCode.INVALID_PARAMS,
                "ADD requires: repoPath|files"
            );
        }

        String repoPath = request.getParameter(0);
        String filesParam = request.getParameter(1);

        if (repoPath == null || repoPath.isEmpty()) {
            throw new GitOperationException(
                ErrorCode.INVALID_PARAMS,
                "Repository path is required"
            );
        }

        if (filesParam == null || filesParam.isEmpty()) {
            throw new GitOperationException(
                ErrorCode.NO_FILES,
                "At least one file is required"
            );
        }

        String[] filePaths = parseFilePaths(filesParam, repoPath);
        if (filePaths.length == 0) {
            throw new GitOperationException(
                ErrorCode.NO_FILES,
                "At least one file is required"
            );
        }

        gitManager.add(repoPath, filePaths);
        return ResponseMessage.success("FILES_ADDED", String.valueOf(filePaths.length));
    }

    private String[] parseFilePaths(String filesParam, String repoPath) {
        String[] fileNames = filesParam.split(",");
        String[] filePaths = new String[fileNames.length];

        for (int i = 0; i < fileNames.length; i++) {
            String fileName = fileNames[i].trim();
            if (fileName.startsWith("/")) {
                filePaths[i] = fileName;
            } else {
                filePaths[i] = repoPath + "/" + fileName;
            }
        }

        return filePaths;
    }
}
