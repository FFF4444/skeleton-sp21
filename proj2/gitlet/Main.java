package gitlet;

import edu.princeton.cs.algs4.ST;

import java.io.File;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author lhs
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch (firstArg) {
            case "init":
                // TODO: handle the `init` command
                validateNumArgs(args, 1);
                Repository.initCommand();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                initCheck(args);
                validateNumArgs(args, 2);
                Repository.addCommand(getCurBranch(), args[1]);
                break;
            // TODO: FILL THE REST IN
            case "commit":
                initCheck(args);
                validateNumArgs(args, 2);
                Repository.commitCommand(args[1], getCurBranch());
                break;
            case "rm":
                initCheck(args);
                validateNumArgs(args, 2);
                Repository.rmCommand(args[1], getCurBranch());
                break;
            case "log":
                initCheck(args);
                validateNumArgs(args, 1);
                Repository.logCommand(getCurBranch());
                break;
            case "global-log":
                initCheck(args);
                validateNumArgs(args, 1);
                Repository.glo_logCommand();
                break;
            case "find":
                initCheck(args);
                validateNumArgs(args, 2);
                Repository.findCommand(args[1]);
                break;
            case "status":
                initCheck(args);
                validateNumArgs(args, 1);
                Repository.statusCommand(getCurBranch());
                break;
            case "checkout":
                initCheck(args);
                switch (args.length) {
                    case 3:
                        Repository.checkoutCommand_file(args[2], getCurBranch());
                        break;
                    case 4:
                        Repository.checkoutCommand(args[1], args[3]);
                        break;
                    case 2:
                        Repository.checkoutCommand_branch(args[1], getCurBranch());
                        break;
                    default:
                        System.out.println("Incorrect operands.");
                        System.exit(0);
                }
                break;
            case "branch":
                initCheck(args);
                validateNumArgs(args, 2);
                Repository.branchCommand(args[1], getCurBranch());
                break;
            case "rm-branch":
                initCheck(args);
                validateNumArgs(args, 2);
                Repository.rmBranchCommand(args[1], getCurBranch());
                break;
            case "reset":
                initCheck(args);
                validateNumArgs(args, 2);
                Repository.resetCommand(args[1], getCurBranch());
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }
    public static void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }
    public static void initCheck(String[] args) {
        if (!args[0].equals("init") && !(new File(".gitlet").exists())) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }
    public static String getCurBranch() {
        return Utils.readContentsAsString(Utils.join(Repository.HEADS, "curBranch"));
    }
}
