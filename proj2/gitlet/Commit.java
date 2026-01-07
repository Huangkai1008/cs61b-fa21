package gitlet;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Represents a gitlet commit object.
 *
 * @author huang.kai
 *
 */
public class Commit implements Serializable {
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z", Locale.US);

    /**
     * The message of the commit.
     */
    private final String message;

    /**
     * The first parent commit reference.
     */
    private final String parent;

    /**
     * The second parent commit reference.
     */
    private final String secondParent;

    /**
     * The date of the commit.
     */
    private final Date timestamp;

    /**
     * The blobs of the commit.
     * <p>
     * Maps filename to blob ID.
     */
    private final Map<String, String> blobs;

    /**
     * The ID of the commit.
     */
    private final String commitID;

    public Commit() {
        this.message = "initial commit";
        this.timestamp = new Date(0);
        this.parent = null;
        this.secondParent = null;
        this.blobs = new TreeMap<>();
        this.commitID = generateID();
    }

    public Commit(String message, String parent, Map<String, String> blobs) {
        this.message = message;
        this.parent = parent;
        this.secondParent = null;
        this.timestamp = new Date();
        this.blobs = blobs;
        this.commitID = generateID();
    }

    public Commit(String message, String parent, String secondParent, Map<String, String> blobs) {
        this.message = message;
        this.timestamp = new Date();
        this.parent = parent;
        this.secondParent = secondParent;
        this.blobs = blobs;
        this.commitID = generateID();
    }

    public Map<String, String> getBlobs() {
        return blobs;
    }

    public String getCommitID() {
        return commitID;
    }

    public String getParent() {
        return parent;
    }

    public String getSecondParent() {
        return secondParent;
    }

    public boolean hasParent() {
        return parent != null;
    }

    /**
     * Returns formatted timestamp string.
     */
    private String getFormattedDate() {
        return DATE_FORMAT.format(timestamp);
    }

    private boolean isMergeCommit() {
        return parent != null && secondParent != null;
    }


    /**
     * Returns formatted commit information for log display.
     */
    public String getLogString() {
        if (isMergeCommit()) {
            return String.format("""
                    ===
                    commit %s
                    Merge: %s %s
                    Date: %s
                    %s
                    """, commitID, parent.substring(0, 6), secondParent.substring(0, 6), getFormattedDate(), message);
        }

        return String.format("""
                ===
                commit %s
                Date: %s
                %s
                """, commitID, getFormattedDate(), message);
    }


    private String generateID() {
        return Utils.sha1((Object) Utils.serialize(this));
    }

    @Override
    public String toString() {
        return String.format("Commit(message=%s, commitID=%s)", message, commitID);
    }
}
