package gitlet;

import java.io.File;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *
 *  @author huang.kai
 */
public class Repository {

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));

    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    public static final File REFS_DIR = join(GITLET_DIR, "refs");
    public static final File HEADS_DIR = join(REFS_DIR, "heads");
    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");
    public static final File STAGE_FILE = join(GITLET_DIR, "stage");

    /** The default branch of gitlet */
    public static final String DEFAULT_BRANCH = "master";

    /**
     * Creates a new Gitlet version-control system in the current directory.
     * <p>
     * This system will automatically start with one commit:
     * a commit that contains no files and has the commit message initial commit
     * (just like that, with no punctuation).
     * <p>
     * It will have a single branch: master, which initially points to this initial commit,
     * and master will be the current branch.
     * The timestamp for this initial commit will be 00:00:00 UTC, Thursday, 1 January 1970
     * in whatever format you choose for dates
     * (this is called “The (Unix) Epoch”, represented internally by the time 0.)
     * <p>
     * Since the initial commit in all repositories created by Gitlet will have exactly the same content,
     * it follows that all repositories will automatically share this commit (they will all have the same UID)
     * and all commits in all repositories will trace back to it.
     */
    public static void init() {
        if (isInitialized()) {
            throw error("A Gitlet version-control system already exists in the current directory.");
        }

        setupDirectories();
        setupStagingArea();
        createInitialCommit();
    }

    /**
     * Adds a copy of the file as it currently exists to the staging area (see the description of the commit command).
     * <p>
     * For this reason, adding a file is also called staging the file for addition.
     * Staging an already-staged file overwrites the previous entry in the staging area with the new contents.
     * The staging area should be somewhere in .gitlet.
     * <p>
     * If the current working version of the file is identical to the version in the current commit,
     * do not stage it to be added, and remove it from the staging area if it is already there
     * (as can happen when a file is changed, added, and then changed back to it’s original version).
     * The file will no longer be staged for removal (see gitlet rm), if it was at the time of the command.
     */
    public static void add(String filename) {
        File file = Utils.join(CWD, filename);
        if (!file.exists()) {
            throw error("File does not exist.");
        }

        Stage stage = readStage();
        Blob blob = new Blob(file);
        stage.addFile(filename, blob.getBlobID());
    }

    /**
     * Creates a new branch with the given name, and points it at the current head commit.
     * <p>
     * A branch is nothing more than a name for a reference (an SHA-1 identifier) to a commit node.
     * This command does NOT immediately switch to the newly created branch (just as in real Git).
     * Before you ever call branch, your code should be running with a default branch called “master”.
     */
    public void branch(String branchName) {
        File branch = join(HEADS_DIR, branchName);
        if (branch.exists()) {
            throw error("A branch with that name already exists.");
        }

        Utils.writeContents(branch, getCurrentCommitID());
    }

    /**
     * @return whether the repository is initialized.
     */
    private static boolean isInitialized() {
        return GITLET_DIR.exists();
    }

    private static void ensureInitialized() {
        if (!isInitialized()) {
            throw error("Not in an initialized Gitlet directory.");
        }
    }

    private static void setupDirectories() {
        if (!GITLET_DIR.mkdirs()) {
            throw error("Failed to create .gitlet directory.");
        }
        if (!OBJECTS_DIR.mkdirs()) {
            throw error("Failed to create objects directory.");
        }
        if (!REFS_DIR.mkdirs()) {
            throw error("Failed to create refs directory.");
        }

        if (!HEADS_DIR.mkdirs()) {
            throw error("Failed to create heads directory.");
        }
    }

    private static void setupStagingArea() {
        Stage stage = new Stage();
        writeStage(stage);
    }


    private String getCurrentBranch() {
        return Utils.readContentsAsString(HEAD_FILE);
    }

    private static void setCurrentBranch(String branchName) {
        Utils.writeContents(HEAD_FILE, branchName);
    }

    private String getCurrentCommitID() {
        String currentBranch = getCurrentBranch();
        File branchFile = join(HEADS_DIR, currentBranch);
        return Utils.readContentsAsString(branchFile);
    }

    private static void createInitialCommit() {
        Commit initCommit = new Commit();
        File branch = join(HEADS_DIR, DEFAULT_BRANCH);
        Utils.writeContents(branch, initCommit.getCommitID());
        setCurrentBranch(DEFAULT_BRANCH);
    }

    private static Stage readStage() {
        return readObject(STAGE_FILE, Stage.class);
    }

    private static void writeStage(Stage stage) {
        writeObject(STAGE_FILE, stage);
    }
}
