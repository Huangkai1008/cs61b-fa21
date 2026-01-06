package gitlet;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/** Represents the staging area. */
public class Stage implements Serializable {
    /** Files staged for addition: filename -> blob ID */
    private Map<String, String> added;

    /** Files staged for removal */
    private Set<String> removed;

    public Stage() {
        added = new TreeMap<>();
        removed = new TreeSet<>();
    }

    public Map<String, String> getAdded() {
        return added;
    }

    public Set<String> getRemoved() {
        return removed;
    }

    /**
     * Add file to stage.
     */
    public void addFile(String filename, String blobID) {
        added.put(filename, blobID);
        removed.remove(filename);
    }

    /**
     * Remove file from stage.
     */
    public void removeFile(String filename) {
        added.remove(filename);
        removed.add(filename);
    }

    public boolean isClean() {
        return added.isEmpty() && removed.isEmpty();
    }

    public void clear() {
        added.clear();
        removed.clear();
    }
}
