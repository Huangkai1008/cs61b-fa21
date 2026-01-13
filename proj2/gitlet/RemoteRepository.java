package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import static gitlet.Utils.*;

/**
 * Represents remote repository configurations.
 *
 * @author huang.kai
 */
public class RemoteRepository implements Serializable {
    /**
     * Maps remote name to remote path.
     */
    private final Map<String, String> remotes;


    public RemoteRepository() {
        remotes = new TreeMap<>();
    }

    public void addRemote(String name, String path) {
        if (remotes.containsKey(name)) {
            abort("A remote with that name already exists.");
        }

        String normalizedPath = path.replace("/", File.separator);
        remotes.put(name, normalizedPath);
    }

    public void removeRemote(String name) {
        if (!hasRemote(name)) {
            abort("A remote with that name does not exist.");
        }
        remotes.remove(name);
    }

    public String getRemotePath(String name) {
        String path = remotes.get(name);
        if (path == null) {
            abort("A remote with that name does not exist.");
        }
        return path;
    }

    public File getRemoteGitletDir(String name) {
        return new File(getRemotePath(name));
    }

    private boolean hasRemote(String name) {
        return remotes.containsKey(name);
    }
}
