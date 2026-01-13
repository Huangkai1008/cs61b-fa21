package gitlet;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static gitlet.Utils.*;

/**
 * Represents a gitlet repository.
 *
 * @author huang.kai
 */
public class Repository {

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));

    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    public static final File COMMIT_DIR = join(OBJECTS_DIR, "commits");
    public static final File BLOB_DIR = join(COMMIT_DIR, "blobs");
    public static final File REFS_DIR = join(GITLET_DIR, "refs");
    public static final File HEADS_DIR = join(REFS_DIR, "heads");
    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");
    public static final File STAGE_FILE = join(GITLET_DIR, "stage");
    public static final File REMOTES_FILE = join(GITLET_DIR, "remotes");

    /**
     * The default branch of gitlet
     */
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
            abort("A Gitlet version-control system already exists in the current directory.");
        }

        setupDirectories();
        setupStagingArea();
        setupRemotes();
        createInitialCommit();
    }

    public static void ensureInitialized() {
        if (!isInitialized()) {
            abort("Not in an initialized Gitlet directory.");
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
            abort("File does not exist.");
        }

        Stage stage = readStage();
        Blob blob = new Blob(file);
        Commit currentCommit = getCurrentCommit();
        String existBlobID = currentCommit.getBlobs().get(filename);
        // Current working version of the file is identical to the version in the current commit.
        if (existBlobID != null && existBlobID.equals(blob.getBlobID())) {
            stage.getAdded().remove(filename);
            stage.getRemoved().remove(filename);
            writeStage(stage);
            return;
        }

        File blobFile = join(BLOB_DIR, blob.getBlobID());
        writeObject(blobFile, blob);
        stage.addFile(filename, blob.getBlobID());
        writeStage(stage);
    }

    /**
     * Unstage the file if it is currently staged for addition.
     * <p>
     * If the file is tracked in the current commit,
     * stage it for removal and remove the file from the working directory
     * if the user has not already done so (do not remove it unless it is tracked in the current commit).
     */
    public static void rm(String filename) {
        Stage stage = readStage();
        Commit currentCommit = getCurrentCommit();
        boolean staged = stage.getAdded().containsKey(filename);
        boolean tracked = currentCommit.getBlobs().containsKey(filename);
        if (!staged && !tracked) {
            abort("No reason to remove the file.");
        }

        if (staged) {
            stage.unstageFile(filename);
        }

        if (tracked) {
            stage.stageForRemoval(filename);
            Utils.restrictedDelete(Utils.join(CWD, filename));
        }

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
            abort("No changes added to the commit.");
        }

        Commit parentCommit = getCurrentCommit();
        String parentCommitID = parentCommit.getCommitID();
        Map<String, String> newBlobs = new TreeMap<>(parentCommit.getBlobs());
        newBlobs.putAll(stage.getAdded());
        for (String filename : stage.getRemoved()) {
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
    public static void branch(String branchName) {
        File branch = join(HEADS_DIR, branchName);
        if (branch.exists()) {
            abort("A branch with that name already exists.");
        }

        Utils.writeContents(branch, getCurrentCommitID());
    }

    public static void rmBranch(String branchName) {
        File branch = join(HEADS_DIR, branchName);
        if (!branch.exists()) {
            abort("A branch with that name does not exist.");
        }

        if (branchName.equals(getCurrentBranch())) {
            abort("Cannot remove the current branch.");
        }

        if (!branch.delete()) {
            throw error("Failed to delete branch");
        }
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
            abort("File does not exist in that commit.");
        }

        restoreFile(filename, blobID);
    }

    public static void checkoutBranch(String branchName) {
        File branchFile = Utils.join(HEADS_DIR, branchName);
        if (!branchFile.exists()) {
            abort("No such branch exists.");
        }

        if (getCurrentBranch().equals(branchName)) {
            abort("No need to checkout the current branch.");
        }

        Commit targetCommit = getCommitFromID(Utils.readContentsAsString(branchFile));
        if (hasUntrackedFiles(targetCommit)) {
            abort("There is an untracked file in the way; delete it, or add and commit it first.");
        }

        Commit currentCommit = getCurrentCommit();
        Map<String, String> targetBlobs = targetCommit.getBlobs();
        for (var entry : targetBlobs.entrySet()) {
            restoreFile(entry.getKey(), entry.getValue());
        }

        for (String filename : currentCommit.getBlobs().keySet()) {
            if (!targetBlobs.containsKey(filename)) {
                restrictedDelete(join(CWD, filename));
            }
        }

        setCurrentBranch(branchName);
        Stage stage = readStage();
        stage.clear();
        writeStage(stage);
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
     * Like log, except displays information about all commits ever made. The order of the commits does not matter.
     */
    public static void globalLog() {
        List<String> allCommitIDs = safeListFiles(COMMIT_DIR);
        for (String commitID : allCommitIDs) {
            Commit commit = getCommitFromID(commitID);
            System.out.print(commit.getLogString());
            System.out.println();
        }
    }

    /**
     * Displays what branches currently exist, and marks the current branch with a *.
     * <p>
     * Also displays what files have been staged for addition or removal.
     */
    public static void status() {
        // Display branches
        System.out.println("=== Branches ===");
        logBranches();
        System.out.println();

        // Display staged files
        System.out.println("=== Staged Files ===");
        logStagedFiles();
        System.out.println();

        // Display removed files
        System.out.println("=== Removed Files ===");
        logRemovedFiles();
        System.out.println();

        // Display modifications not staged for commit
        System.out.println("=== Modifications Not Staged For Commit ===");
        logNotStageForCommit();
        System.out.println();

        // Display untracked files
        System.out.println("=== Untracked Files ===");
        logUntrackedFiles();
        System.out.println();
    }

    /**
     * Prints out the ids of all commits that have the given commit message, one per line.
     * If there are multiple such commits, it prints the ids out on separate lines.
     */
    public static void find(String message) {
        List<String> allCommits = safeListFiles(COMMIT_DIR);
        List<String> matchedCommits = allCommits.stream()
                .map(Repository::getCommitFromID)
                .filter(commit -> commit.getMessage().equals(message))
                .map(Commit::getCommitID)
                .collect(Collectors.toList());

        if (matchedCommits.isEmpty()) {
            abort("Found no commit with that message.");
        }

        matchedCommits.forEach(System.out::println);
    }

    /**
     * Checks out all the files tracked by the given commit.
     * Removes tracked files that are not present in that commit.
     * Also moves the current branch’s head to that commit node.
     * <p>
     * See the intro for an example of what happens to the head pointer after using reset.
     * The [commit id] may be abbreviated as for checkout.
     * The staging area is cleared.
     * The command is essentially checkout of an arbitrary commit that also changes the current branch head.
     */
    public static void reset(String commitID) {
        Commit currentCommit = getCurrentCommit();
        Commit targetCommit = getCommitFromID(commitID);

        // Check for untracked files that would be overwritten
        if (hasUntrackedFiles(targetCommit)) {
            abort("There is an untracked file in the way; delete it, or add and commit it first.");
        }

        // Restore all files from the target commit.
        Map<String, String> targetBlobs = targetCommit.getBlobs();
        for (var entry : targetBlobs.entrySet()) {
            String filename = entry.getKey();
            String blobId = entry.getValue();
            restoreFile(filename, blobId);
        }

        // Delete tracked files that are not present in that commit.
        for (String filename : currentCommit.getBlobs().keySet()) {
            if (!targetBlobs.containsKey(filename)) {
                restrictedDelete(join(CWD, filename));
            }
        }

        Stage stage = readStage();
        stage.clear();
        writeStage(stage);

        // Moves the current branch’s head to that commit node.
        File branch = Utils.join(HEADS_DIR, getCurrentBranch());
        Utils.writeContents(branch, targetCommit.getCommitID());
    }

    /**
     * Merges files from the given branch into the current branch.
     */
    public static void merge(String branchName) {
        Stage stage = readStage();
        if (!stage.isClean()) {
            abort("You have uncommitted changes.");
        }

        File givenBranch = Utils.join(HEADS_DIR, branchName);
        if (!givenBranch.exists()) {
            abort("A branch with that name does not exist.");
        }

        if (branchName.equals(getCurrentBranch())) {
            abort("Cannot merge a branch with itself.");
        }

        String currentCommitID = getCurrentCommitID();
        String givenCommitID = readContentsAsString(givenBranch);

        // Check for untracked files that would be overwritten
        Commit givenCommit = getCommitFromID(givenCommitID);
        if (hasUntrackedFiles(givenCommit)) {
            abort("There is an untracked file in the way; delete it, or add and commit it first.");
        }

        String splitPoint = findSplitPoint(currentCommitID, givenCommitID);

        if (splitPoint.equals(givenCommitID)) {
            message("Given branch is an ancestor of the current branch.");
            return;
        }

        if (splitPoint.equals(currentCommitID)) {
            checkoutBranch(branchName);
            message("Current branch fast-forwarded.");
            return;
        }

        Commit splitCommit = getCommitFromID(splitPoint);
        Commit currentCommit = getCommitFromID(currentCommitID);

        Map<String, String> splitBlobs = splitCommit.getBlobs();
        Map<String, String> currentBlobs = currentCommit.getBlobs();
        Map<String, String> givenBlobs = givenCommit.getBlobs();

        Set<String> allFiles = new HashSet<>();
        allFiles.addAll(splitBlobs.keySet());
        allFiles.addAll(currentBlobs.keySet());
        allFiles.addAll(givenBlobs.keySet());
        boolean hasConflict = false;

        for (String filename : allFiles) {
            String splitBlobId = splitBlobs.get(filename);
            String currentBlobId = currentBlobs.get(filename);
            String givenBlobId = givenBlobs.get(filename);

            boolean modifiedInCurrent = !Objects.equals(splitBlobId, currentBlobId);
            boolean modifiedInGiven = !Objects.equals(splitBlobId, givenBlobId);
            boolean presentAtSplitPoint = splitBlobId != null;
            boolean presentAtCurrent = currentBlobId != null;
            boolean presentAtGiven = givenBlobId != null;
            boolean sameInBoth = Objects.equals(currentBlobId, givenBlobId);

            // Merge rule 0 (implicit):
            // Files that are the same in all three commits (split, current, given) remain unchanged
            if (!modifiedInCurrent && !modifiedInGiven) {
                keepCurrent();
                continue;
            }

            // Merge rule 1:
            //
            // Any files that have been modified in the given branch since the split point,
            // but not modified in the current branch since the split point should be changed to their versions
            // in the given branch (checked out from the commit at the front of the given branch).
            // These files should then all be automatically staged.
            //
            // To clarify, if a file is “modified in the given branch since the split point” this
            // means the version of the file as it exists in the commit at the front of the given branch
            // has different content from the version of the file at the split point.
            if (modifiedInGiven && !modifiedInCurrent) {
                // The file has been deleted in given branch.
                if (givenBlobId == null) {
                    File file = Utils.join(CWD, filename);
                    Utils.restrictedDelete(file);
                    stage.stageForRemoval(filename);
                } else {
                    restoreFile(filename, givenBlobId);
                    stage.addFile(filename, givenBlobId);
                }
            }

            // Merge rule 2:
            //
            // Any files that have been modified in the current branch but not in the given branch
            // since the split point should stay as they are.
            else if (modifiedInCurrent && !modifiedInGiven) {
                keepCurrent();
            }

            // Merge rule 3:
            //
            // Any files that have been modified in both the current and given branch in the same way
            // (i.e., both files now have the same content or were both removed) are left unchanged by the merge.
            //
            // If a file was removed from both the current and given branch,
            // but a file of the same name is present in the working directory,
            // it is left alone and continues to be absent (not tracked nor staged) in the merge.
            else if (modifiedInGiven && modifiedInCurrent && sameInBoth) {
                keepCurrent();
            }

            // Merge rule 4:
            //
            // Any files that were not present at the split point and
            // are present only in the current branch should remain as they are.
            else if (!presentAtSplitPoint && presentAtCurrent && !presentAtGiven) {
                keepCurrent();
            }

            // Merge rule 5:
            //
            // Any files that were not present at the split point
            // and are present only in the given branch should be checked out and staged.
            else if (!presentAtSplitPoint && presentAtGiven && !presentAtCurrent) {
                restoreFile(filename, givenBlobId);
                stage.addFile(filename, givenBlobId);
            }

            // Merge rule 7 :
            //
            // Any files present at the split point, unmodified in the given branch,
            // and absent in the current branch should remain absent.
            else if (presentAtSplitPoint && !modifiedInGiven && !presentAtCurrent) {
                keepCurrent();
            }

            // Merge rule 8:
            // Any files modified in different ways in the current and given branches are in conflict.
            // "Modified in different ways" can mean that the contents of both are changed and different from other,
            // or the contents of one are changed and the other file is deleted,
            // or the file was absent at the split point and has different contents in the given and current branches.
            else {
                hasConflict = true;
                handleMergeConflict(filename, currentBlobId, givenBlobId, stage);
            }
        }

        writeStage(stage);

        Map<String, String> newBlobs = new TreeMap<>(currentBlobs);
        newBlobs.putAll(stage.getAdded());
        for (String removed : stage.getRemoved()) {
            newBlobs.remove(removed);
        }

        String mergeMessage = String.format("Merged %s into %s.", branchName, getCurrentBranch());
        Commit mergeCommit = new Commit(mergeMessage, currentCommitID, givenCommitID, newBlobs);
        saveCommit(mergeCommit);

        File currentBranchFile = join(HEADS_DIR, getCurrentBranch());
        writeContents(currentBranchFile, mergeCommit.getCommitID());

        stage.clear();
        writeStage(stage);

        if (hasConflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    /**
     * Saves the given login information under the given remote name.
     * Attempts to push or pull from the given remote name will then attempt to use this .gitlet directory.
     * <p>
     * By writing, e.g., java gitlet.Main add-remote other ../testing/otherdir/.gitlet.
     * <p>
     * Always use forward slashes in these commands.
     * Have your program convert all the forward slashes into the path separator character
     * (forward slash on Unix and backslash on Windows).
     */
    public static void addRemote(String remoteName, String remotePath) {
        RemoteRepository remoteRepository = readRemotes();
        remoteRepository.addRemote(remoteName, remotePath);
        writeRemotes(remoteRepository);
    }

    /**
     * Remove information associated with the given remote name.
     * <p>
     * The idea here is that if you ever wanted to change a remote that you added,
     * you would have to first remove it and then re-add it.
     */
    public static void rmRemote(String remoteName) {
        RemoteRepository remoteRepository = readRemotes();
        remoteRepository.removeRemote(remoteName);
        writeRemotes(remoteRepository);
    }

    /**
     * Attempts to append the current branch’s commits to the end of the given branch at the given remote.
     * <p>
     * Details:
     * This command only works if the remote branch’s head is in the history of the current local head,
     * which means that the local branch contains some commits in the future of the remote branch.
     * In this case, append the future commits to the remote branch.
     * Then, the remote should reset to the front of the appended commits
     * (so its head will be the same as the local head). This is called fast-forwarding.
     * <p>
     * If the Gitlet system on the remote machine exists but does not have the input branch,
     * then simply add the branch to the remote Gitlet.
     */
    public static void push(String remoteName, String remoteBranchName) {
        RemoteRepository remoteRepo = readRemotes();
        File remoteGitletDir = remoteRepo.getRemoteGitletDir(remoteName);

        if (!remoteGitletDir.exists()) {
            abort("Remote directory not found.");
        }

        String localHead = getCurrentCommitID();
        String remoteHead = readBranchHead(remoteGitletDir, remoteBranchName);

        if (remoteHead != null && !isAncestor(remoteHead, localHead)) {
            abort("Please pull down remote changes before pushing.");
        }

        pushCommits(localHead, remoteHead, remoteGitletDir);

        writeBranchHead(remoteGitletDir, remoteBranchName, localHead);
    }

    /**
     * Brings down commits from the remote Gitlet repository into the local Gitlet repository.
     * <p>
     * Basically, this copies all commits and blobs
     * from the given branch in the remote repository (that are not already in the current repository)
     * into a branch named [remote name]/[remote branch name] in the local .gitlet (just as in real Git),
     * changing [remote name]/[remote branch name] to point to the head commit
     * (thus copying the contents of the branch from the remote repository to the current one).
     * This branch is created in the local repository if it did not previously exist.
     */
    public static void fetch(String remoteName, String remoteBranchName) {
        RemoteRepository remoteRepo = readRemotes();
        File remoteGitletDir = remoteRepo.getRemoteGitletDir(remoteName);

        if (!remoteGitletDir.exists()) {
            abort("Remote directory not found.");
        }

        if (!hasBranchIn(remoteGitletDir, remoteBranchName)) {
            abort("That remote does not have that branch.");
        }

        String remoteHead = readBranchHead(remoteGitletDir, remoteBranchName);

        fetchCommits(remoteHead, remoteGitletDir);

        String localRemoteBranchName = remoteName + "/" + remoteBranchName;
        File localBranchFile = join(HEADS_DIR, localRemoteBranchName);
        File parentDir = localBranchFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw error("Failed to create directory: %s", parentDir);
            }
        }
        writeContents(localBranchFile, remoteHead);
    }

    /**
     * Fetches branch [remote name]/[remote branch name] as for the fetch command,
     * and then merges that fetch into the current branch.
     */
    public static void pull(String remoteName, String remoteBranchName) {
        fetch(remoteName, remoteBranchName);
        String localRemoteBranchName = remoteName + "/" + remoteBranchName;
        merge(localRemoteBranchName);
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

    private static void setupRemotes() {
        RemoteRepository remoteRepository = new RemoteRepository();
        writeRemotes(remoteRepository);
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
            abort("No commit with that id exists.");
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

        List<String> allCommitIDs = safeListFiles(COMMIT_DIR);
        if (allCommitIDs.isEmpty()) {
            abort("No commit with that id exists.");
        }

        List<String> matches = new ArrayList<>();
        for (String fullId : allCommitIDs) {
            if (fullId.startsWith(commitID)) {
                matches.add(fullId);
            }
        }

        if (matches.isEmpty()) {
            abort("No commit with that id exists.");
        }

        if (matches.size() > 1) {
            abort("Ambiguous commit id.");
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

    private static RemoteRepository readRemotes() {
        if (!REMOTES_FILE.exists()) {
            return new RemoteRepository();
        }
        return readObject(REMOTES_FILE, RemoteRepository.class);
    }

    private static void writeRemotes(RemoteRepository remoteRepository) {
        writeObject(REMOTES_FILE, remoteRepository);
    }

    private static void restoreFile(String filename, String blobId) {
        File blobFile = join(BLOB_DIR, blobId);
        Blob blob = readObject(blobFile, Blob.class);

        File targetFile = join(CWD, filename);
        writeContents(targetFile, (Object) blob.getContent());
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

        List<String> workingFiles = safeListFiles(CWD);

        for (String filename : workingFiles) {
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

    private static void logBranches() {
        String currentBranch = getCurrentBranch();
        List<String> branches = safeListFiles(HEADS_DIR);

        Collections.sort(branches);
        for (String branch : branches) {
            if (branch.equals(currentBranch)) {
                System.out.println("*" + branch);
            } else {
                System.out.println(branch);
            }
        }
    }

    private static void logStagedFiles() {
        Stage stage = readStage();
        stage.getAdded().keySet().stream()
                .sorted()
                .forEach(System.out::println);
    }

    private static void logRemovedFiles() {
        Stage stage = readStage();
        stage.getRemoved().stream()
                .sorted()
                .forEach(System.out::println);
    }

    /**
     * A file in the working directory is "modified but not staged" if it is
     * <p>
     * <ul>
     *     <li>Tracked in the current commit, changed in the working directory, but not staged</li>
     *     <li>Staged for addition, but with different contents than in the working directory</li>
     *     <li>Staged for addition, but deleted in the working directory</li>
     *     <li>Not staged for removal, but tracked in the current commit and deleted from the working directory.</li>
     * </ul>
     */
    private static void logNotStageForCommit() {
        Commit currentCommit = getCurrentCommit();
        Map<String, String> currentBlobs = currentCommit.getBlobs();
        Stage stage = readStage();
        Map<String, String> stagedAdded = stage.getAdded();
        Set<String> stagedRemoved = stage.getRemoved();
        List<String> workingFiles = safeListFiles(CWD);
        Set<String> workingFileSet = new HashSet<>(workingFiles);

        Set<String> modifiedFiles = new TreeSet<>();

        // Case 1: Tracked in current commit, changed in working directory, but not staged
        for (Map.Entry<String, String> entry : currentBlobs.entrySet()) {
            String filename = entry.getKey();
            String blobID = entry.getValue();

            if (workingFileSet.contains(filename)) {
                File file = join(CWD, filename);
                byte[] content = readContents(file);
                String currentBlobID = sha1((Object) content);

                if (!currentBlobID.equals(blobID) && !stagedAdded.containsKey(filename)) {
                    modifiedFiles.add(filename + " (modified)");
                }
            } else {
                // Case 4: Not staged for removal, but tracked in current commit and deleted from working directory
                if (!stagedRemoved.contains(filename)) {
                    modifiedFiles.add(filename + " (deleted)");
                }
            }
        }

        // Case 2: Staged for addition, but with different contents than in working directory
        // Case 3: Staged for addition, but deleted in the working directory
        for (Map.Entry<String, String> entry : stagedAdded.entrySet()) {
            String filename = entry.getKey();
            String stagedBlobID = entry.getValue();

            if (workingFileSet.contains(filename)) {
                File file = join(CWD, filename);
                byte[] content = readContents(file);
                String currentBlobID = sha1((Object) content);

                if (!currentBlobID.equals(stagedBlobID)) {
                    modifiedFiles.add(filename + " (modified)");
                }
            } else {
                modifiedFiles.add(filename + " (deleted)");
            }
        }

        modifiedFiles.forEach(System.out::println);
    }

    /**
     * Untracked file is for files present in the working directory but neither staged for addition nor tracked.
     * This includes files that have been staged for removal, but then re-created without Gitlet's knowledge.
     * Ignore any subdirectories that may have been introduced, since Gitlet does not deal with them.
     */
    private static void logUntrackedFiles() {
        Commit currentCommit = getCurrentCommit();
        Map<String, String> currentBlobs = currentCommit.getBlobs();
        Stage stage = readStage();
        Map<String, String> stagedAdded = stage.getAdded();
        Set<String> stagedRemoved = stage.getRemoved();
        List<String> workingFiles = safeListFiles(CWD);

        Set<String> untrackedFiles = new TreeSet<>();

        for (String filename : workingFiles) {
            // Ignore .gitlet directory
            if (filename.startsWith(".gitlet")) {
                continue;
            }

            boolean trackedInCommit = currentBlobs.containsKey(filename);
            boolean stagedForAddition = stagedAdded.containsKey(filename);
            boolean stagedForRemoval = stagedRemoved.contains(filename);

            // Untracked: not tracked in commit AND not staged for addition
            // OR staged for removal but re-created
            if ((!trackedInCommit && !stagedForAddition) || stagedForRemoval) {
                untrackedFiles.add(filename);
            }
        }

        untrackedFiles.forEach(System.out::println);
    }

    private static List<String> safeListFiles(File directory) {
        List<String> files = plainFilenamesIn(directory);
        return files != null ? files : Collections.emptyList();
    }

    /**
     * The split point is the latest common ancestor of the current and given branch heads:
     * <p>
     * - A common ancestor is a commit to which there is a path (of 0 or more parent pointers) from both branch heads.
     * <p>
     * - The latest common ancestor is a common ancestor that is not an ancestor of any other common ancestor.
     */
    private static String findSplitPoint(String currentCommitID, String givenCommitID) {
        Map<String, Integer> currentDepths = getCommitDepths(currentCommitID);
        Map<String, Integer> givenDepths = getCommitDepths(givenCommitID);

        // Find the common ancestor with minimum depth (most recent)
        return currentDepths.keySet().stream()
                .filter(givenDepths::containsKey)
                .min(Comparator.comparingInt(currentDepths::get))
                .orElse(null);
    }

    private static Map<String, Integer> getCommitDepths(String commitId) {
        Map<String, Integer> depths = new HashMap<>();
        collectDepths(commitId, 0, depths);
        return depths;
    }

    private static void collectDepths(String commitId, int depth, Map<String, Integer> depths) {
        if (commitId == null || depths.containsKey(commitId)) {
            return;
        }

        depths.put(commitId, depth);
        Commit commit = getCommitFromID(commitId);

        collectDepths(commit.getParent(), depth + 1, depths);
        collectDepths(commit.getSecondParent(), depth + 1, depths);
    }

    private static void keepCurrent() {
    }

    private static void handleMergeConflict(String filename, String currentBlobId,
                                            String givenBlobId, Stage stage) {
        String currentContent = "";
        String givenContent = "";

        if (currentBlobId != null) {
            File blobFile = join(BLOB_DIR, currentBlobId);
            Blob blob = readObject(blobFile, Blob.class);
            currentContent = new String(blob.getContent());
        }

        if (givenBlobId != null) {
            File blobFile = join(BLOB_DIR, givenBlobId);
            Blob blob = readObject(blobFile, Blob.class);
            givenContent = new String(blob.getContent());
        }

        String conflictContent = """
                <<<<<<< HEAD
                %s=======
                %s>>>>>>>
                """.formatted(currentContent, givenContent);

        File file = join(CWD, filename);
        writeContents(file, conflictContent);

        // Stage the conflict file
        Blob conflictBlob = new Blob(conflictContent.getBytes());
        File conflictBlobFile = join(BLOB_DIR, conflictBlob.getBlobID());
        writeObject(conflictBlobFile, conflictBlob);
        stage.addFile(filename, conflictBlob.getBlobID());
    }

    // ==================== Remote Repository Helper Methods ====================

    /**
     * Read branch head from specified gitlet directory
     */
    private static String readBranchHead(File gitletDir, String branchName) {
        File branchFile = join(gitletDir, "refs", "heads", branchName);
        if (!branchFile.exists()) {
            return null;
        }
        return readContentsAsString(branchFile);
    }

    /**
     * Write branch head to specified gitlet directory
     */
    private static void writeBranchHead(File gitletDir, String branchName, String commitID) {
        File branchFile = join(gitletDir, "refs", "heads", branchName);
        writeContents(branchFile, commitID);
    }

    /**
     * Read commit from specified gitlet directory
     */
    private static Commit readCommitFrom(File gitletDir, String commitID) {
        File commitFile = join(gitletDir, "objects", "commits", commitID);
        return readObject(commitFile, Commit.class);
    }

    /**
     * Write commit to specified gitlet directory
     */
    private static void writeCommitTo(File gitletDir, Commit commit) {
        File commitFile = join(gitletDir, "objects", "commits", commit.getCommitID());
        writeObject(commitFile, commit);
    }

    /**
     * Read blob from specified gitlet directory
     */
    private static Blob readBlobFrom(File gitletDir, String blobID) {
        File blobFile = join(gitletDir, "objects", "commits", "blobs", blobID);
        return readObject(blobFile, Blob.class);
    }

    /**
     * Write blob to specified gitlet directory
     */
    private static void writeBlobTo(File gitletDir, Blob blob) {
        File blobFile = join(gitletDir, "objects", "commits", "blobs", blob.getBlobID());
        writeObject(blobFile, blob);
    }

    /**
     * Check if specified gitlet directory has a commit
     */
    private static boolean hasCommitIn(File gitletDir, String commitID) {
        File commitFile = join(gitletDir, "objects", "commits", commitID);
        return commitFile.exists();
    }

    /**
     * Check if specified gitlet directory has a blob
     */
    private static boolean hasBlobIn(File gitletDir, String blobID) {
        File blobFile = join(gitletDir, "objects", "commits", "blobs", blobID);
        return blobFile.exists();
    }

    /**
     * Check if specified gitlet directory has a branch
     */
    private static boolean hasBranchIn(File gitletDir, String branchName) {
        File branchFile = join(gitletDir, "refs", "heads", branchName);
        return branchFile.exists();
    }

    private static void pushCommits(String localHead, String remoteHead, File remoteGitletDir) {
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(localHead);

        while (!queue.isEmpty()) {
            String commitID = queue.poll();

            if (visited.contains(commitID)
                    || (remoteHead != null && commitID.equals(remoteHead))
                    || hasCommitIn(remoteGitletDir, commitID)) {
                continue;
            }
            visited.add(commitID);

            // Copy commit
            Commit commit = getCommitFromID(commitID);
            writeCommitTo(remoteGitletDir, commit);

            // Copy blobs
            for (String blobID : commit.getBlobs().values()) {
                if (!hasBlobIn(remoteGitletDir, blobID)) {
                    File localBlobFile = join(BLOB_DIR, blobID);
                    Blob blob = readObject(localBlobFile, Blob.class);
                    writeBlobTo(remoteGitletDir, blob);
                }
            }

            // Add parent nodes
            if (commit.getParent() != null) {
                queue.add(commit.getParent());
            }
            if (commit.getSecondParent() != null) {
                queue.add(commit.getSecondParent());
            }
        }
    }

    private static void fetchCommits(String remoteHead, File remoteGitletDir) {
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(remoteHead);

        while (!queue.isEmpty()) {
            String commitID = queue.poll();

            File localCommitFile = join(COMMIT_DIR, commitID);
            if (visited.contains(commitID) || localCommitFile.exists()) {
                continue;
            }
            visited.add(commitID);

            // Copy commit
            Commit commit = readCommitFrom(remoteGitletDir, commitID);
            writeObject(localCommitFile, commit);

            // Copy blobs
            for (String blobID : commit.getBlobs().values()) {
                File localBlobFile = join(BLOB_DIR, blobID);
                if (!localBlobFile.exists()) {
                    Blob blob = readBlobFrom(remoteGitletDir, blobID);
                    writeObject(localBlobFile, blob);
                }
            }

            // Add parent nodes
            if (commit.getParent() != null) {
                queue.add(commit.getParent());
            }
            if (commit.getSecondParent() != null) {
                queue.add(commit.getSecondParent());
            }
        }
    }

    private static boolean isAncestor(String ancestorID, String descendantID) {
        Commit current = getCommitFromID(descendantID);
        while (current != null) {
            if (current.getCommitID().equals(ancestorID)) {
                return true;
            }
            String parentID = current.getParent();
            if (parentID == null) {
                break;
            }
            current = getCommitFromID(parentID);
        }
        return false;
    }
}
