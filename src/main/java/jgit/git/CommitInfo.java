package jgit.git;

public class CommitInfo {
    private final String commitHash;
    private final String authorName;
    private final String authorEmail;
    private final String message;
    private final long timestamp;

    public CommitInfo(String commitHash, String authorName, String authorEmail, String message, long timestamp) {
        this.commitHash = commitHash;
        this.authorName = authorName;
        this.authorEmail = authorEmail;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getCommitHash() {
        return commitHash;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
