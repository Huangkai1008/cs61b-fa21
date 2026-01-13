package gitlet;

import static gitlet.Utils.abort;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 *
 * @author huang.kai
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            abort("Please enter a command.");
        }

        String cmd = args[0];
        if (cmd.equals("init")) {
            validArgs(args, 1);
            Repository.init();
            return;
        }

        Repository.ensureInitialized();

        switch (cmd) {
            case "add": {
                validArgs(args, 2);
                String filename = args[1];
                Repository.add(filename);
                break;
            }

            case "rm": {
                validArgs(args, 2);
                String filename = args[1];
                Repository.rm(filename);
                break;
            }

            case "commit": {
                validArgs(args, 2);
                String message = args[1];
                if (message == null || message.isEmpty()) {
                    abort("Please enter a commit message.");
                }

                Repository.commit(message);
                break;
            }

            case "checkout": {
                validArgs(args, 2, 3, 4);
                if (args.length == 2) {
                    String branchName = args[1];
                    Repository.checkoutBranch(branchName);
                } else if (args.length == 3 && args[1].equals("--")) {
                    String filename = args[2];
                    Repository.checkout(filename);
                } else if (args.length == 4 && args[2].equals("--")) {
                    String commitID = args[1];
                    String filename = args[3];
                    Repository.checkout(commitID, filename);
                } else {
                    abort("Incorrect operands.");
                }
                break;
            }

            case "log": {
                validArgs(args, 1);
                Repository.log();
                break;
            }

            case "global-log": {
                validArgs(args, 1);
                Repository.globalLog();
                break;
            }

            case "find": {
                validArgs(args, 2);
                String message = args[1];
                if (message == null || message.isEmpty()) {
                    abort("Please enter a commit message.");
                }
                Repository.find(message);
                break;
            }

            case "status": {
                validArgs(args, 1);
                Repository.status();
                break;
            }

            case "branch": {
                validArgs(args, 2);
                String branchName = args[1];
                if (branchName == null || branchName.isEmpty()) {
                    abort("Please enter a branch name.");
                }
                Repository.branch(branchName);
                break;
            }

            case "rm-branch": {
                validArgs(args, 2);
                String branchName = args[1];
                if (branchName == null || branchName.isEmpty()) {
                    abort("Please enter a branch name.");
                }
                Repository.rmBranch(branchName);
                break;
            }

            case "reset": {
                validArgs(args, 2);
                String commitID = args[1];
                Repository.reset(commitID);
                break;
            }

            case "merge": {
                validArgs(args, 2);
                String branchName = args[1];
                if (branchName == null || branchName.isEmpty()) {
                    abort("Please enter a branch name.");
                }
                Repository.merge(branchName);
                break;
            }

            default:
                abort("No command with that name exists.");
        }
    }

    private static void validArgs(String[] args, int... validLengths) {
        for (int validLength : validLengths) {
            if (args.length == validLength) {
                return;
            }
        }
        abort("Incorrect operands.");
    }
}
