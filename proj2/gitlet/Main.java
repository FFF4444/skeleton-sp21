package gitlet;

import edu.princeton.cs.algs4.ST;
import static gitlet.Repository.*;
import java.io.File;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author lhs
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch (firstArg) {
            case "init":
                validateNumArgs(args, 1);
                initCommand();
                break;
            case "add":
                check(args, 2);
                addCommand(getCurBranch(), args[1]);
                break;
            case "commit":
                check(args, 2);
                commitCommand(args[1], getCurBranch(), null);
                break;
            case "rm":
                check(args, 2);
                rmCommand(args[1], getCurBranch());
                break;
            case "log":
                check(args, 1);
                logCommand(getCurBranch());
                break;
            case "global-log":
                check(args, 1);
                globallogCommand();
                break;
            case "find":
                check(args, 2);
                findCommand(args[1]);
                break;
            case "status":
                check(args, 1);
                statusCommand(getCurBranch());
                break;
            case "checkout":
                initCheck(args);
                switch (args.length) {
                    case 3:
                        if (!args[1].equals("--")) {
                            System.out.println("Incorrect operands.");
                            System.exit(0);
                        }
                        checkoutCommandFile(args[2], getCurBranch());
                        break;
                    case 4:
                        if (!args[2].equals("--")) {
                            System.out.println("Incorrect operands.");
                            System.exit(0);
                        }
                        checkoutCommand(args[1], args[3]);
                        break;
                    case 2:
                        checkoutCommandBranch(args[1], getCurBranch());
                        break;
                    default:
                        System.out.println("Incorrect operands.");
                        System.exit(0);
                }
                break;
            case "branch":
                check(args, 2);
                branchCommand(args[1], getCurBranch());
                break;
            case "rm-branch":
                check(args, 2);
                rmBranchCommand(args[1], getCurBranch());
                break;
            case "reset":
                check(args, 2);
                resetCommand(args[1], getCurBranch());
                break;
            case "merge":
                check(args, 2);
                mergeCommand(args[1], getCurBranch());
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }

    public static String getCurBranch() {
        return Utils.readContentsAsString(Utils.join(Repository.HEADS, "curBranch"));
    }
}
