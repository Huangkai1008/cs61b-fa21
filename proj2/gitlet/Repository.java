package gitlet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
    public static final File COMMIT_DIR = join(OBJECTS_DIR, "commits");
    public static final File BLOB_DIR = join(COMMIT_DIR, "blobs");
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

    public static void ensureInitialized() {
        if (!isInitialized()) {
            throw error("Not in an initialized Gitlet directory.");
        }
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

        File blobFile = join(BLOB_DIR, blob.getBlobID());
        writeObject(blobFile, blob);
        stage.addFile(filename, blob.getBlobID());
        writeStage(stage);
    }


    /**
     * Saves a snapshot of tracked files in the current commit and staging area.
     * Creates a new commit that tracks the saved files.
     *
     * <p>By default, each commit's snapshot will be exactly the same as its parent's.
     * A commit will only update files that have been staged for addition.
     * Files staged for removal will be untracked in the new commit.
     *
     * <p>Key behaviors:
     * <ul>
     *   <li>The staging area is cleared after a commit.</li>
     *   <li>The commit command never modifies files in the working directory
     *       (except .gitlet directory).</li>
     *   <li>Changes made to files after staging are ignored by commit.</li>
     *   <li>The new commit becomes the "current commit" and HEAD points to it.</li>
     *   <li>The previous HEAD commit becomes this commit's parent.</li>
     *   <li>Each commit contains the date and time it was made.</li>
     *   <li>Each commit is identified by its SHA-1 id, which includes file (blob)
     *       references, parent reference, log message, and commit time.</li>
     * </ul>
     *
     * @param message The log message describing changes in this commit.
     */
    public static void commit(String message) {
        Stage stage = readStage();
        if (stage.isClean()) {
            throw error("No changes added to the commit.");
        }

        Commit parentCommit = getCurrentCommit();
        String parentCommitID = parentCommit.getCommitID();
        Map<String, String> newBlobs = new TreeMap<>(parentCommit.getBlobs());
        newBlobs.putAll(stage.getAdded());
        for (String filename: stage.getRemoved()) {
            newBlobs.remove(filename);
        }

        Commit commit = new Commit(message, parentCommitID, newBlobs);
        saveCommit(commit);

        String currentBranch = getCurrentBranch();
        File branchFile = join(HEADS_DIR, currentBranch);
        writeContents(branchFile, commit.getCommitID());

        stage.clear();
        writeStage(stage);
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
     * Checkout command with three different usages:
     *
     * <p>Usage 1: {@code checkout -- [file name]}
     * <br>Takes the version of the file from the HEAD commit and puts it in the
     * working directory, overwriting the existing version if present.
     * The new version is not staged.
     *
     * <p>Usage 2: {@code checkout [commit id] -- [file name]}
     * <br>Takes the version of the file from the specified commit and puts it in
     * the working directory, overwriting the existing version if present.
     * The new version is not staged.
     * Supports abbreviated commit IDs (unique prefix).
     *
     * <p>Usage 3: {@code checkout [branch name]}
     * <br>Takes all files from the HEAD of the given branch and puts them in the
     * working directory, overwriting existing versions.
     * The given branch becomes the current branch (HEAD).
     * Files tracked in current branch but not in checked-out branch are deleted.
     * The staging area is cleared.
     *
     * <p>Failure cases:
     * <ul>
     *   <li>Usage 1/2: If file does not exist in the commit, print
     *       "File does not exist in that commit."</li>
     *   <li>Usage 2: If no commit with given id exists, print
     *       "No commit with that id exists."</li>
     *   <li>Usage 3: If no branch with that name exists, print
     *       "No such branch exists."</li>
     *   <li>Usage 3: If branch is current branch, print
     *       "No need to checkout the current branch."</li>
     *   <li>Usage 3: If untracked file would be overwritten, print
     *       "There is an untracked file in the way; delete it, or add and commit it first."</li>
     * </ul>
     *
     */
    public static void checkout(String filename) {
        checkout(getCurrentCommitID(), filename);
    }

    public static void checkout(String commitID, String filename) {
        Commit commit = getCommitFromID(commitID);
        Map<String, String> blobs = commit.getBlobs();
        String blobID = blobs.get(filename);
        if (blobID == null) {
            throw error("File does not exist in that commit.");
        }

        File blobFile = join(BLOB_DIR, blobID);
        Blob blob = readObject(blobFile, Blob.class);
        byte[] content = blob.getContent();

        File targetFile = join(CWD, filename);
        writeContents(targetFile, (Object) content);
    }

    public static void checkoutBranch(String branchName) {
        File branchFile = Utils.join(REFS_DIR, branchName);
        if (!branchFile.exists()) {
            throw error("No such branch exists.");
        }

        if (getCurrentBranch().equals(branchName)) {
            throw error("No need to checkout the current branch.");
        }

        Commit targetCommit = getCommitFromID(Utils.readContentsAsString(branchFile));
        if (hasUntrackedFiles(targetCommit)) {
            throw error("There is an untracked file in the way; delete it, or add and commit it first.");
        }

        setCurrentBranch(branchName);
        Stage stage = readStage();
        stage.clear();
    }

    /**
     * Starting at the current head commit, display information about each commit
     * backwards along the commit tree until the initial commit, following the first parent commit links,
     * ignoring any second parents found in merge commits.
     * (In regular Git, this is what you get with git log --first-parent).
     * This set of commit nodes is called the commit’s history.
     * For every node in this history, the information it should display is the commit id,
     * the time the commit was made, and the commit message.
     */
    public static void log() {
        for (Commit commit = getCurrentCommit();
             commit != null;
             commit = getParentCommit(commit)) {
            System.out.print(commit.getLogString());
            System.out.println();
        }
    }

    /**
     * @return whether the repository is initialized.
     */
    private static boolean isInitialized() {
        return GITLET_DIR.exists();
    }

    private static void setupDirectories() {
        if (!GITLET_DIR.mkdirs()) {
            throw error("Failed to create .gitlet directory.");
        }
        if (!OBJECTS_DIR.mkdirs()) {
            throw error("Failed to create objects directory.");
        }
        if (!COMMIT_DIR.mkdirs()) {
            throw error("Failed to create commits directory.");
        }
        if (!BLOB_DIR.mkdirs()) {
            throw error("Failed to create blobs directory.");
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

    private static String getCurrentBranch() {
        return Utils.readContentsAsString(HEAD_FILE);
    }

    private static void setCurrentBranch(String branchName) {
        Utils.writeContents(HEAD_FILE, branchName);
    }

    private static String getCurrentCommitID() {
        String currentBranch = getCurrentBranch();
        File branchFile = join(HEADS_DIR, currentBranch);
        return Utils.readContentsAsString(branchFile);
    }

    private static Commit getCurrentCommit() {
        String commitID = getCurrentCommitID();
        return getCommitFromID(commitID);
    }

    /**
     * Get commit from given commit id, commit id can be abbreviated.
     */
    private static Commit getCommitFromID(String commitID) {
        String fullCommitID = resolveCommitID(commitID);
        File commitFile = Utils.join(COMMIT_DIR, fullCommitID);
        if (!commitFile.exists()) {
            throw error("No commit with that id exists.");
        }
        return readObject(commitFile, Commit.class);
    }

    /**
     * Resolve abbreviated commit ID to full ID.
     */
    private static String resolveCommitID(String commitID) {
        if (commitID.length() == UID_LENGTH) {
            return commitID;
        }

        List<String> allCommitIDs = plainFilenamesIn(COMMIT_DIR);
        if (allCommitIDs == null || allCommitIDs.isEmpty()) {
            throw error("No commit with that id exists.");
        }

        List<String> matches = new ArrayList<>();
        for (String fullId : allCommitIDs) {
            if (fullId.startsWith(commitID)) {
                matches.add(fullId);
            }
        }

        if (matches.isEmpty()) {
            throw error("No commit with that id exists.");
        }

        if (matches.size() > 1) {
            throw error("Ambiguous commit id.");
        }

        return matches.get(0);
    }

    private static void createInitialCommit() {
        Commit initCommit = new Commit();
        saveCommit(initCommit);
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

    private static void saveCommit(Commit commit) {
        File commitFile = Utils.join(COMMIT_DIR, commit.getCommitID());
        writeObject(commitFile, commit);
    }

    private static boolean hasUntrackedFiles(Commit targetCommit) {
        Commit currentCommit = getCurrentCommit();
        Map<String, String> currentBlobs = currentCommit.getBlobs();
        Map<String, String> targetBlobs = targetCommit.getBlobs();
        Stage stage = readStage();

        List<String> workingFiles = plainFilenamesIn(CWD);
        if (workingFiles == null) {
            return false;
        }

        for (String filename: workingFiles) {
            if (filename.startsWith(".gitlet")) continue;

            boolean trackedInCurrent = currentBlobs.containsKey(filename);
            boolean stagedForAddition = stage.getAdded().containsKey(filename);
            boolean isUntracked = !trackedInCurrent && !stagedForAddition;
            if (isUntracked && targetBlobs.containsKey(filename)) {
                return true;
            }
        }

        return false;
    }

    private static Commit getParentCommit(Commit commit) {
        String parentId = commit.getParent();
        return parentId != null ? getCommitFromID(parentId) : null;
    }
}
