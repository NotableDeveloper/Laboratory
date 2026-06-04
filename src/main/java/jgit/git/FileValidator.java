package jgit.git;

import jgit.exception.GitOperationException;
import jgit.exception.GitOperationException.ErrorCode;
import java.io.File;
import java.io.IOException;

public class FileValidator {
    public static void validatePath(String repoPath, String filePath) throws GitOperationException {
        try {
            File repoFile = new File(repoPath).getCanonicalFile();
            File targetFile = new File(filePath).getCanonicalFile();

            String repoAbsPath = repoFile.getAbsolutePath();
            String targetAbsPath = targetFile.getAbsolutePath();

            if (!targetAbsPath.startsWith(repoAbsPath)) {
                throw new GitOperationException(
                    ErrorCode.IO_ERROR,
                    "File path is outside repository: " + filePath
                );
            }

            if (!targetFile.exists()) {
                throw new GitOperationException(
                    ErrorCode.IO_ERROR,
                    "File not found: " + filePath
                );
            }

            if (!targetFile.isFile()) {
                throw new GitOperationException(
                    ErrorCode.IO_ERROR,
                    "Path is not a file: " + filePath
                );
            }
        } catch (IOException e) {
            throw new GitOperationException(
                ErrorCode.IO_ERROR,
                "Error validating path: " + e.getMessage(),
                e
            );
        }
    }

    public static void validateRepositoryPath(String repoPath) throws GitOperationException {
        File repoDir = new File(repoPath);
        if (!repoDir.exists()) {
            throw new GitOperationException(
                ErrorCode.REPO_NOT_FOUND,
                "Repository not found: " + repoPath
            );
        }

        if (!repoDir.isDirectory()) {
            throw new GitOperationException(
                ErrorCode.IO_ERROR,
                "Repository path is not a directory: " + repoPath
            );
        }
    }
}
