package jgit.git;

import jgit.exception.GitOperationException;
import jgit.exception.GitOperationException.ErrorCode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.TimeZone;

public class GitRepositoryManager {
    private static final Logger logger = LoggerFactory.getLogger(GitRepositoryManager.class);

    public void initRepository(String repoPath) throws GitOperationException {
        initRepository(repoPath, null);
    }

    public void initRepository(String repoPath, String remoteUrl) throws GitOperationException {
        try {
            File repoDir = new File(repoPath);

            if (repoDir.exists() && new File(repoPath, ".git").exists()) {
                throw new GitOperationException(
                    ErrorCode.GIT_ERROR,
                    "Repository already exists: " + repoPath
                );
            }

            if (!repoDir.exists()) {
                if (!repoDir.mkdirs()) {
                    throw new GitOperationException(
                        ErrorCode.IO_ERROR,
                        "Failed to create repository directory: " + repoPath
                    );
                }
            }

            Git git = Git.init().setDirectory(repoDir).call();

            if (remoteUrl != null && !remoteUrl.isEmpty()) {
                git.remoteAdd()
                    .setName("origin")
                    .setUri(new org.eclipse.jgit.transport.URIish(remoteUrl))
                    .call();
                logger.info("Set remote origin to {} in {}", remoteUrl, repoPath);
            }

            git.close();
            logger.info("Initialized repository: {}", repoPath);

        } catch (GitAPIException | java.net.URISyntaxException e) {
            throw new GitOperationException(
                ErrorCode.GIT_ERROR,
                "Failed to initialize repository: " + e.getMessage(),
                e
            );
        }
    }

    public void add(String repoPath, String[] filePaths) throws GitOperationException {
        try {
            FileValidator.validateRepositoryPath(repoPath);

            Repository repository = openRepository(repoPath);
            Git git = new Git(repository);

            try {
                for (String filePath : filePaths) {
                    FileValidator.validatePath(repoPath, filePath);
                    String relativePath = getRelativePath(repoPath, filePath);
                    git.add().addFilepattern(relativePath).call();
                    logger.debug("Added file to staging: {}", relativePath);
                }
            } finally {
                git.close();
                repository.close();
            }
        } catch (GitAPIException e) {
            throw new GitOperationException(
                ErrorCode.GIT_ERROR,
                "Add failed: " + e.getMessage(),
                e
            );
        }
    }

    public CommitInfo commit(String repoPath, String authorName, String authorEmail,
                             String message) throws GitOperationException {
        try {
            FileValidator.validateRepositoryPath(repoPath);

            Repository repository = openRepository(repoPath);
            Git git = new Git(repository);

            try {
                PersonIdent author = new PersonIdent(authorName, authorEmail,
                    new Date(), TimeZone.getDefault());

                RevCommit commit = git.commit()
                    .setMessage(message)
                    .setAuthor(author)
                    .setCommitter(author)
                    .call();

                logger.info("Created commit {} by {} in {}", commit.getName(), authorName, repoPath);

                return new CommitInfo(
                    commit.getName(),
                    authorName,
                    authorEmail,
                    message,
                    commit.getCommitTime() * 1000L
                );

            } finally {
                git.close();
                repository.close();
            }

        } catch (GitAPIException e) {
            throw new GitOperationException(
                ErrorCode.GIT_ERROR,
                "Commit failed: " + e.getMessage(),
                e
            );
        }
    }

    public RepositoryStatus getStatus(String repoPath) throws GitOperationException {
        try {
            FileValidator.validateRepositoryPath(repoPath);

            Repository repository = openRepository(repoPath);
            Git git = new Git(repository);

            try {
                String branch = repository.getBranch();
                int commitCount = 0;

                for (@SuppressWarnings("unused") RevCommit ignored : git.log().call()) {
                    commitCount++;
                }

                boolean dirty = !git.status().call().isClean();

                return new RepositoryStatus(branch, commitCount, dirty);

            } finally {
                git.close();
                repository.close();
            }

        } catch (GitAPIException | IOException e) {
            throw new GitOperationException(
                ErrorCode.GIT_ERROR,
                "Status check failed: " + e.getMessage(),
                e
            );
        }
    }

    private Repository openRepository(String repoPath) throws GitOperationException {
        try {
            return new FileRepositoryBuilder()
                .setGitDir(new File(repoPath, ".git"))
                .readEnvironment()
                .findGitDir()
                .build();
        } catch (IOException e) {
            throw new GitOperationException(
                ErrorCode.REPO_NOT_FOUND,
                "Failed to open repository: " + e.getMessage(),
                e
            );
        }
    }

    public PushResult push(String repoPath, String remoteName) throws GitOperationException {
        try {
            FileValidator.validateRepositoryPath(repoPath);

            Repository repository = openRepository(repoPath);
            Git git = new Git(repository);

            try {
                String currentBranch = repository.getBranch();

                git.push()
                    .setRemote(remoteName)
                    .call();

                logger.info("Pushed from {} to {} (branch: {})",
                    repoPath, remoteName, currentBranch);

                return new PushResult(currentBranch, 1);

            } finally {
                git.close();
                repository.close();
            }

        } catch (GitAPIException | IOException e) {
            throw new GitOperationException(
                ErrorCode.GIT_ERROR,
                "Push failed: " + e.getMessage(),
                e
            );
        }
    }

    public PullResult pull(String repoPath, String remoteName, String branchName) throws GitOperationException {
        try {
            FileValidator.validateRepositoryPath(repoPath);

            Repository repository = openRepository(repoPath);
            Git git = new Git(repository);

            try {
                String targetBranch = branchName != null && !branchName.isEmpty()
                    ? branchName
                    : repository.getBranch();

                var pullResult = git.pull()
                    .setRemote(remoteName)
                    .setRebase(false)
                    .call();

                boolean successful = pullResult.isSuccessful();
                int mergedCommitCount = 0;

                if (successful && pullResult.getMergeResult() != null) {
                    mergedCommitCount = pullResult.getMergeResult().getMergedCommits().length;
                }

                logger.info("Pulled from {} to {} (branch: {}, merged: {}, successful: {})",
                    remoteName, repoPath, targetBranch, mergedCommitCount, successful);

                return new PullResult(targetBranch, mergedCommitCount, successful);

            } finally {
                git.close();
                repository.close();
            }

        } catch (GitAPIException | IOException e) {
            throw new GitOperationException(
                ErrorCode.GIT_ERROR,
                "Pull failed: " + e.getMessage(),
                e
            );
        }
    }


    private String getRelativePath(String repoPath, String filePath) {
        File repoFile = new File(repoPath);
        File file = new File(filePath);
        return file.getAbsolutePath().substring(repoFile.getAbsolutePath().length() + 1)
            .replace("\\", "/");
    }

    public static class PushResult {
        private final String branch;
        private final int refCount;

        public PushResult(String branch, int refCount) {
            this.branch = branch;
            this.refCount = refCount;
        }

        public String getBranch() {
            return branch;
        }

        public int getRefCount() {
            return refCount;
        }
    }

    public static class PullResult {
        private final String branch;
        private final int mergedCommitCount;
        private final boolean successful;

        public PullResult(String branch, int mergedCommitCount, boolean successful) {
            this.branch = branch;
            this.mergedCommitCount = mergedCommitCount;
            this.successful = successful;
        }

        public String getBranch() {
            return branch;
        }

        public int getMergedCommitCount() {
            return mergedCommitCount;
        }

        public boolean isSuccessful() {
            return successful;
        }
    }

    public static class RepositoryStatus {
        private final String branch;
        private final int commitCount;
        private final boolean dirty;

        public RepositoryStatus(String branch, int commitCount, boolean dirty) {
            this.branch = branch;
            this.commitCount = commitCount;
            this.dirty = dirty;
        }

        public String getBranch() {
            return branch;
        }

        public int getCommitCount() {
            return commitCount;
        }

        public boolean isDirty() {
            return dirty;
        }
    }
}
