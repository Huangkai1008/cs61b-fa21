package gitlet;

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

        String firstArg = args[0];
        switch(firstArg) {
            case "init": {
                validArgs(args, 1);
                Repository.init();
                break;
            }
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
            default:
                throw error("No command with that name exists.");
        }
    }

    private static void validArgs(String[] args, int length) {
        if (args.length != length) {
            throw error("Incorrect operands.");
        }
    }
}
