package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  does at a high level.
 *
 *  @author
 */
public class Repository {
    /**
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File COMMITS = join(GITLET_DIR, "commits");
    public static final File HEADS = join(GITLET_DIR, "heads");
    public static final File BLOBS = join(GITLET_DIR, "blobs");
    public static final File STAGE = join(GITLET_DIR, "stage");
    public static final File ADD = join(STAGE, "add");
    public static final File RM = join(STAGE, "remove");
    public static final File CURBRANCH = join(Repository.HEADS, "curBranch");

    private static void setUp() {
        GITLET_DIR.mkdir();
        COMMITS.mkdir();
        HEADS.mkdir();
        BLOBS.mkdir();
        STAGE.mkdir();
        TreeMap<String, String> add = new TreeMap<>();
        TreeSet<String> rm = new TreeSet<>();
        writeObject(ADD, add);
        writeObject(RM, rm);
    }

    public static void initCommand() {
        Commit c = new Commit();
        File i = join(Repository.COMMITS, c.getHash());
        if (new File(".gitlet").exists()) {
            System.out.print("A Gitlet version-control system already "
                    + "exists in the current directory.");
        } else {
            Repository.setUp();
            writeObject(i, c);
            String head = c.getHash();
            String curBranch = "master";
            File h = join(Repository.HEADS, curBranch);
            writeContents(CURBRANCH, curBranch);
            writeContents(h, head);
        }
    }

    public static void addCommand(String curBranch, String name) {
        if (!join(CWD, name).exists()) {
            System.out.println("File does not exist.");
            return;
        }
        Commit curCommit = getCurCommit(curBranch);
        byte[] file = readContents(join(CWD, name));
        @SuppressWarnings("unchecked")
        TreeMap<String, String> add = readObject(ADD, TreeMap.class);
        @SuppressWarnings("unchecked")
        TreeSet<String> rm = readObject(RM, TreeSet.class);
        rm.remove(name);
        if (curCommit.fileExist(name, file)) {
            add.remove(name);
        } else {
            add.put(name, sha1(serialize(file)));
        }
        writeObject(ADD, add);
        writeObject(RM, rm);
        addBlobs(file);
    }

    public static Commit getCurCommit(String curBranch) {
        File ch = join(HEADS, curBranch);
        return Commit.readCommit(readContentsAsString(ch));
    }

    public static void addBlobs(Serializable file) {
        String hash = sha1(serialize(file));
        writeContents(join(BLOBS, hash), file);
    }

    public static void commitCommand(String message, String curBranch, String secBranch) {
        if (message.isBlank()) {
            System.out.println("Please enter a commit message.");
            return;
        }
        @SuppressWarnings("unchecked")
        TreeMap<String, String> add = readObject(ADD, TreeMap.class);
        @SuppressWarnings("unchecked")
        TreeSet<String> rm = readObject(RM, TreeSet.class);
        if (add.isEmpty() && rm.isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }
        Commit cur;
        if (secBranch == null) {
            cur = new Commit(message, getCurCommit(curBranch).getHash());
        } else {
            cur = new Commit(message,
                    getCurCommit(curBranch).getHash(), getCurCommit(secBranch).getHash());
        }
        cur.add(add);
        cur.rm(rm);
        add.clear();
        rm.clear();
        writeObject(ADD, add);
        writeObject(RM, rm);
        File target = join(COMMITS, cur.getHash());
        writeObject(target, cur);
        writeContents(join(HEADS, curBranch), cur.getHash());
    }

    public static void rmCommand(String name, String curBranch) {
        @SuppressWarnings("unchecked")
        TreeMap<String, String> add = readObject(ADD, TreeMap.class);
        Commit cur = getCurCommit(curBranch);
        if (!cur.getBlob().containsKey(name) && !add.containsKey(name)) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        @SuppressWarnings("unchecked")
        TreeSet<String> rm = readObject(RM, TreeSet.class);
        if (cur.getBlob().containsKey(name)) {
            rm.add(name);
            writeObject(RM, rm);
            restrictedDelete(join(CWD, name));
        }
        if (add.containsKey(name)) {
            add.remove(name);
            writeObject(ADD, add);

        }
    }

    public static void logCommand(String curBranch) {
        Commit cur = getCurCommit(curBranch);
        while (cur != null) {
            cur.printCommit();
            cur = Commit.readCommit(cur.prev());
        }
    }

    public static void globallogCommand() {
        List<String> commits = plainFilenamesIn(COMMITS);
        for (String c : commits) {
            Commit cur = Commit.readCommit(c);
            cur.printCommit();
        }
    }

    public static void findCommand(String message) {
        List<String> commits = plainFilenamesIn(COMMITS);
        boolean flag = false;
        for (String c : commits) {
            Commit cur = Commit.readCommit(c);
            if (cur.getMessage().equals(message)) {
                System.out.println(cur.getHash());
                flag = true;
            }
        }
        if (!flag) {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
    }

    public static void statusCommand(String curBranch) {
        List<String> branches = plainFilenamesIn(HEADS);
        List<String> cwd = plainFilenamesIn(CWD);
        Commit c = getCurCommit(curBranch);
        TreeMap<String, String> blobs = c.getBlob();
        @SuppressWarnings("unchecked")
        TreeMap<String, String> add = readObject(ADD, TreeMap.class);
        @SuppressWarnings("unchecked")
        TreeSet<String> rm = readObject(RM, TreeSet.class);
        System.out.println("=== Branches ===");
        for (String i : branches) {
            if (!i.equals("curBranch")) {
                if (curBranch.equals(i)) {
                    System.out.print("*");
                }
                System.out.println(i);
            }
        }
        System.out.println("\n=== Staged Files ===");
        for (String i : add.keySet()) {
            System.out.println(i);
        }
        System.out.println("\n=== Removed Files ===");
        for (String i : rm) {
            System.out.println(i);
        }
        TreeMap<String, String> modifications = new TreeMap<>();
        TreeSet<String> untracked = new TreeSet<>();
        for (String i : cwd) {
            byte[] file = readContents(join(CWD, i));
            if (add.containsKey(i)) {
                if (!add.get(i).equals(sha1(serialize(file)))) {
                    modifications.put(i, " (modified)");
                }
            } else if (blobs.containsKey(i)) {
                if (!blobs.get(i).equals(sha1(serialize(file)))) {
                    modifications.put(i, " (modified)");
                }
            } else {
                untracked.add(i);
            }
        }
        for (String i : add.keySet()) {
            if (!cwd.contains(i)) {
                modifications.put(i, " (deleted)");
            }
        }
        for (String i : blobs.keySet()) {
            if (!rm.contains(i) && !cwd.contains(i)) {
                modifications.put(i, " (deleted)");
            }
        }
        System.out.println("\n=== Modifications Not Staged For Commit ===");
        for (String i : modifications.keySet()) {
            System.out.print(i);
            System.out.println(modifications.get(i));
        }
        System.out.println("\n=== Untracked Files ===");
        for (String i : untracked) {
            System.out.println(i);
        }
    }

    private static void checkoutHelper(String name, Commit c) {
        TreeMap<String, String> blobs = c.getBlob();
        if (blobs.containsKey(name)) {
            File target = join(BLOBS, blobs.get(name));
            byte[] file = readContents(target);
            writeContents(join(CWD, name), file);
        } else {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
    }

    public static void checkoutCommandFile(String name, String curBranch) {
        Commit c = getCurCommit(curBranch);
        checkoutHelper(name, c);
    }

    public static void checkoutCommand(String commitID, String name) {
        String totalID = Commit.commitExist(commitID);
        if (totalID != null) {
            checkoutHelper(name, Commit.readCommit(totalID));
        } else {
            System.out.println("No commit with that id exists.");
        }
    }

    public static void checkoutCommandBranch(String branch, String curBranch) {
        File b = join(HEADS, branch);
        if (!b.exists()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        } else if (branch.equals(curBranch)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        resetHelper(getCurCommit(curBranch), getCurCommit(branch));
        writeContents(CURBRANCH, branch);
    }

    private static void resetHelper(Commit cur, Commit target) {
        TreeMap<String, String> curBlobs = cur.getBlob();
        TreeMap<String, String> tarBlobs = target.getBlob();
        for (String i : tarBlobs.keySet()) {
            File f = join(CWD, i);
            if (f.exists() && (!curBlobs.containsKey(i) || !getHash(f).equals(curBlobs.get(i)))) {
                System.out.println("There is an untracked file in the way"
                        + "; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        for (String i : curBlobs.keySet()) {
            File f = join(CWD, i);
            if (!tarBlobs.containsKey(i) && !curBlobs.get(i).equals(getHash(f))) {
                System.out.println("There is an untracked file"
                        + " in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        for (String i : tarBlobs.keySet()) {
            File f = join(CWD, i);
            if (!f.exists() || !tarBlobs.get(i).equals(getHash(f))) {
                writeContents(f, readContents(join(BLOBS, tarBlobs.get(i))));
            }
        }
        for (String i : curBlobs.keySet()) {
            File f = join(CWD, i);
            if (!tarBlobs.containsKey(i)) {
                restrictedDelete(f);
            }
        }
        TreeMap<String, String> add = new TreeMap<>();
        TreeSet<String> rm = new TreeSet<>();
        writeObject(ADD, add);
        writeObject(RM, rm);
    }
    private static String getHash(File f) {
        if (f.exists()) {
            return sha1(serialize(readContents(f)));
        }
        return null;
    }
    public static void branchCommand(String name, String curBranch) {
        File f = join(HEADS, name);
        if (f.exists()) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        writeContents(f, readContentsAsString(join(HEADS, curBranch)));
    }
    public static void rmBranchCommand(String name, String curBranch) {
        File f = join(HEADS, name);
        if (!f.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (name.equals(curBranch)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        f.delete();
    }
    public static void resetCommand(String id, String curBranch) {
        if (Commit.commitExist(id) == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        resetHelper(Commit.readCommit(readContentsAsString(join(HEADS, curBranch))),
                Commit.readCommit(Commit.commitExist(id)));
        writeContents(join(HEADS, curBranch), Commit.commitExist(id));
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

    public static void check(String[] args, int n) {
        initCheck(args);
        validateNumArgs(args, n);
    }
    public static void mergeCommand(String target, String curBranch) {
        mergeCheck(target, curBranch);
        TreeMap<String, String> toWrite = new TreeMap<>();
        TreeSet<String> toDelete = new TreeSet<>();
        TreeMap<String, String> conflict = new TreeMap<>();
        Commit spiltPoint = searchSplitPoint(target, curBranch);
        Commit tar = getCurCommit(target);
        Commit cur = getCurCommit(curBranch);
        if (spiltPoint.getHash().equals(tar.getHash())) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }
        if (spiltPoint.getHash().equals(cur.getHash())) {
            checkoutCommandBranch(target, curBranch);
            System.out.println("Current branch fast-forwarded.");
            return;
        }
        for (String i : spiltPoint.getBlob().keySet()) {
            if (spiltPoint.getBlob().get(i).equals(cur.getBlob().get(i))) {
                if (tar.getBlob().containsKey(i)) {
                    toWrite.put(i, tar.getBlob().get(i));
                } else {
                    toDelete.add(i);
                }
            } else if (!spiltPoint.getBlob().get(i).equals(tar.getBlob().get(i))) {
                String curStr = readContentsAsString(join(BLOBS, cur.getBlob().get(i)));
                String tarStr = readContentsAsString(join(BLOBS, tar.getBlob().get(i)));
                if (cur.getBlob().containsKey(i) && tar.getBlob().containsKey(i)) {
                    String result = "<<<<<<< HEAD\n" + curStr + "=======\n" + tarStr + ">>>>>>>\n";
                    conflict.put(i, result);
                } else if (!cur.getBlob().containsKey(i) && tar.getBlob().containsKey(i)) {
                    String result = "<<<<<<< HEAD\n" + "=======\n" + tarStr + ">>>>>>>\n";
                    conflict.put(i, result);
                } else if (cur.getBlob().containsKey(i) && !tar.getBlob().containsKey(i)) {
                    String result = "<<<<<<< HEAD\n" + curStr + "=======\n" + ">>>>>>>\n";
                    conflict.put(i, result);
                }
            }
        }
        for (String i : cur.getBlob().keySet()) {
            if (!spiltPoint.getBlob().containsKey(i) && tar.getBlob().containsKey(i)
                    && !cur.getBlob().get(i).equals(tar.getBlob().get(i))) {
                String result = "<<<<<<< HEAD\n" + readContentsAsString(
                        join(BLOBS, cur.getBlob().get(i)))
                        + "=======\n" + readContentsAsString(join(BLOBS, tar.getBlob().get(i)))
                        + ">>>>>>>\n";
                conflict.put(i, result);
            }
        }
        for (String i : tar.getBlob().keySet()) {
            if (!spiltPoint.getBlob().containsKey(i)) {
                if (!cur.getBlob().containsKey(i)) {
                    toWrite.put(i, tar.getBlob().get(i));
                } else if (!cur.getBlob().get(i).equals(tar.getBlob().get(i))) {
                    String result = "<<<<<<< HEAD\n"
                            + readContentsAsString(join(BLOBS, cur.getBlob().get(i))) + "=======\n"
                            + readContentsAsString(join(BLOBS, tar.getBlob().get(i))) + ">>>>>>>\n";
                    conflict.put(i, result);
                }
            }
        }
        for (String i : plainFilenamesIn(CWD)) {
            if (!cur.fileExist(i, readContents(join(CWD, i)))) {
                if (toWrite.containsKey(i) || toDelete.contains(i) || conflict.containsKey(i)) {
                    System.out.println("There is an untracked file in the way;"
                            + " delete it, or add and commit it first.");
                    System.exit(0);
                }
            }
        }
        mergeHelper(toWrite, toDelete, conflict, curBranch);
        commitCommand("Merged " + target + " into " + curBranch
                + ".", curBranch, target);
    }
    private static void mergeCheck(String target, String curBranch) {
        if (!readObject(ADD, TreeMap.class).isEmpty() || !readObject(RM, TreeSet.class).isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        if (!join(HEADS, target).exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (target.equals(curBranch)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }

    }
    private static Commit searchSplitPoint(String target, String curBranch) {
        Queue<Commit> visited = new ArrayDeque<>();
        Commit tar = getCurCommit(target);
        Commit cur = getCurCommit(curBranch);
        TreeSet<String> allCurPrev = new TreeSet<>();
        TreeSet<String> tarPrev = new TreeSet<>();
        visited.add(cur);
        while (!visited.isEmpty()) {
            Commit c = visited.remove();
            allCurPrev.add(c.getHash());
            if (c.prev() != null && !allCurPrev.contains(c.prev())) {
                visited.add(Commit.readCommit(c.prev()));
            }
            if (c.getSecPrev() != null && !allCurPrev.contains(c.getSecPrev())) {
                visited.add(Commit.readCommit(c.getSecPrev()));
            }
        }
        Commit c = tar;
        while (!allCurPrev.contains(c.getHash())) {
            tarPrev.add(c.getHash());
            if (c.prev() != null && !tarPrev.contains(c.prev())) {
                visited.add(Commit.readCommit(c.prev()));
            }
            if (c.getSecPrev() != null && !tarPrev.contains(c.getSecPrev())) {
                visited.add(Commit.readCommit(c.getSecPrev()));
            }
            c = visited.remove();
        }
        return c;
    }
    private static void mergeHelper(TreeMap<String, String> toWrite, TreeSet<String> toDelete,
                                    TreeMap<String, String> conflict, String curBranch) {
        for (String i : toWrite.keySet()) {
            writeContents(join(CWD, i), readContents(join(BLOBS, toWrite.get(i))));
            addCommand(curBranch, i);
        }
        for (String i : toDelete) {
            rmCommand(i, curBranch);
        }
        for (String i : conflict.keySet()) {
            writeContents(join(CWD, i), conflict.get(i));
            addCommand(curBranch, i);
        }
        if (!conflict.isEmpty()) {
            System.out.println("Encountered a merge conflict.");
        }
    }
}
