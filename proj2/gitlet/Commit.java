package gitlet;

import java.io.Serializable;
import java.util.*;

/** Represents a gitlet commit object.
 *
 *  @author huang.kai
 *
 */
public class Commit implements Serializable {
    /**
     * The message of the commit.
     */
    private final String message;

    /**
     * The parents commit reference.
     */
    private final List<String> parents;

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
        this.parents = new LinkedList<>();
        this.timestamp = new Date(0);
        this.blobs = new TreeMap<>();
        this.commitID = generateID();
    }

    public Commit(String message, List<String> parents, Map<String, String> blobs) {
        this.message = message;
        this.parents = parents;
        this.timestamp = new Date();
        this.blobs = blobs;
        this.commitID = generateID();
    }

    public String getCommitID() {
        return commitID;
    }

    private String generateID() {
        return Utils.sha1((Object) Utils.serialize(this));
    }

    @Override
    public String toString() {
        return String.format("Commit(message=%s, commitID=%s)", message, commitID);
    }
}
