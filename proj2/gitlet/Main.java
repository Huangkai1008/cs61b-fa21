package gitlet;

import java.util.Arrays;

import static gitlet.Utils.error;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author huang.kai
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            throw error("Please enter a command.");
        }

        String cmd = args[0];
        if (cmd.equals("init")) {
            validArgs(args, 1);
            Repository.init();
            return;
        }

        Repository.ensureInitialized();

        switch(cmd) {
            case "add": {
                validArgs(args, 2);
                String filename = args[1];
                Repository.add(filename);
                break;
            }

            case "commit": {
                validArgs(args, 2);
                String message = args[1];
                if (message == null || message.isEmpty()) {
                    throw error("Please enter a commit message.");
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
                    throw error("Incorrect operands.");
                }
                break;
            }

            case "log": {
                validArgs(args, 1);
                Repository.log();
                break;
            }
            default:
                throw error("No command with that name exists.");
        }
    }

    private static void validArgs(String[] args, int... validLengths) {
        for (int validLength: validLengths) {
            if (args.length == validLength) {
                return;
            }
        }
        throw error("Incorrect operands.");
    }
}
