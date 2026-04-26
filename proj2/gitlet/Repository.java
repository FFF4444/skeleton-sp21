package gitlet;

import edu.princeton.cs.algs4.ST;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
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

    /* TODO: fill in the rest of this class. */
    public static final File COMMITS = join(GITLET_DIR, "commits");
    public static final File HEADS = join(GITLET_DIR, "heads");
    public static final File BLOBS = join(GITLET_DIR, "blobs");
    public static final File STAGE = join(GITLET_DIR, "stage");
    public static final File ADD = join(STAGE, "add");
    public static final File RM = join(STAGE, "remove");
    public static final File CURBRANCH = Utils.join(Repository.HEADS, "curBranch");

    private static void setUp() {
        GITLET_DIR.mkdir();
        COMMITS.mkdir();
        HEADS.mkdir();
        BLOBS.mkdir();
        STAGE.mkdir();
        TreeMap<String, String> add = new TreeMap<>();
        TreeSet<String> rm = new TreeSet<>();
        Utils.writeObject(ADD, add);
        Utils.writeObject(RM, rm);
    }

    public static void initCommand() {
        Commit c = new Commit();
        File i = Utils.join(Repository.COMMITS, c.getHash());
        if (new File(".gitlet").exists()) {
            System.out.print("A Gitlet version-control system already exists in the current directory.");
        } else {
            Repository.setUp();
            Utils.writeObject(i, c);
            String head = c.getHash();
            String curBranch = "master";
            File h = Utils.join(Repository.HEADS, curBranch);
            Utils.writeContents(CURBRANCH, curBranch);
            Utils.writeContents(h, head);
        }
    }

    public static void addCommand(String curBranch, String name) {
        if (!join(CWD, name).exists()) {
            System.out.println("File does not exist.");
            return;
        }
        Commit curCommit = getCurCommit(curBranch);
        byte[] file = Utils.readContents(join(CWD, name));
        @SuppressWarnings("unchecked")
        TreeMap<String, String> add = readObject(ADD, TreeMap.class);
        @SuppressWarnings("unchecked")
        TreeSet<String> rm = readObject(RM, TreeSet.class);
        rm.remove(name);
        if (curCommit.fileExist(name, file)) {
            add.remove(name);
        } else {
            add.put(name, Utils.sha1(serialize(file)));
        }
        Utils.writeObject(ADD, add);
        Utils.writeObject(RM, rm);
        addBlobs(file);
    }

    public static Commit getCurCommit(String curBranch) {
        File ch = Utils.join(HEADS, curBranch);
        return Commit.readCommit(Utils.readContentsAsString(ch));
    }

    public static void addBlobs(Serializable file) {
        String hash = Utils.sha1(serialize(file));
        Utils.writeContents(join(BLOBS, hash), file);
    }

    public static void commitCommand(String message, String curBranch) {
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
        String curHead = readContentsAsString(join(HEADS, curBranch));
        Commit prev = getCurCommit(curBranch);
        Commit cur = new Commit(message, Utils.sha1(serialize(prev)));
        cur.add(add);
        cur.rm(rm);
        add.clear();
        rm.clear();
        Utils.writeObject(ADD, add);
        Utils.writeObject(RM, rm);
        File target = Utils.join(COMMITS, cur.getHash());
        Utils.writeObject(target, cur);
        Utils.writeContents(Utils.join(HEADS, curBranch), cur.getHash());
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
            Utils.writeObject(RM, rm);
            Utils.restrictedDelete(join(CWD, name));
        }
        if (add.containsKey(name)) {
            add.remove(name);
            Utils.writeObject(ADD, add);

        }
    }

    public static void logCommand(String curBranch) {
        Commit cur = getCurCommit(curBranch);
        while (cur.prev() != null) {
            cur.printCommit();
            cur = Commit.readCommit(cur.prev());
        }
        cur.printCommit();
    }

    public static void glo_logCommand() {
        List<String> commits = Utils.plainFilenamesIn(COMMITS);
        for (String c : commits) {
            Commit cur = Commit.readCommit(c);
            cur.printCommit();
        }
    }

    public static void findCommand(String message) {
        List<String> commits = Utils.plainFilenamesIn(COMMITS);
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
        List<String> branches = Utils.plainFilenamesIn(HEADS);
        List<String> cwd = Utils.plainFilenamesIn(CWD);
        Commit c = getCurCommit(curBranch);
        TreeMap<String, String> blobs = c.getBlob();
        @SuppressWarnings("unchecked")
        TreeMap<String, String> add = Utils.readObject(ADD, TreeMap.class);
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
            byte[] file = Utils.readContents(join(CWD, i));
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
            byte[] file = Utils.readContents(target);
            Utils.writeContents(join(CWD, name), file);
        } else {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
    }

    public static void checkoutCommand_file(String name, String curBranch) {
        Commit c = getCurCommit(curBranch);
        checkoutHelper(name, c);
    }

    public static void checkoutCommand(String commitID, String name) {
        String ID = Commit.commitExist(commitID);
        if (ID != null) {
            checkoutHelper(name, Commit.readCommit(ID));
        } else {
            System.out.println("No commit with that id exists.");
        }
    }

    public static void checkoutCommand_branch(String branch, String curBranch) {
        File b = join(HEADS, branch);
        if (!b.exists()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        } else if (branch.equals(curBranch)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        resetHelper(getCurCommit(curBranch), getCurCommit(branch));
        Utils.writeContents(CURBRANCH, branch);
    }

    private static void resetHelper(Commit cur, Commit target) {
        TreeMap<String, String> curBlobs = cur.getBlob();
        TreeMap<String, String> tarBlobs = target.getBlob();
        for (String i : tarBlobs.keySet()) {
            File f = join(CWD, i);
            if (f.exists() && (!curBlobs.containsKey(i) || !getHash(f).equals(curBlobs.get(i)))) {
                System.out.println("1.There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        for (String i : curBlobs.keySet()) {
            File f = join(CWD, i);
            if (!tarBlobs.containsKey(i) && !curBlobs.get(i).equals(getHash(f))) {
                System.out.println("2.There is an untracked file in the way; delete it, or add and commit it first.");
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
        Utils.writeObject(ADD, add);
        Utils.writeObject(RM, rm);
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
    public static void resetCommand(String ID, String curBranch) {
        if (Commit.commitExist(ID) == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        resetHelper(Commit.readCommit(readContentsAsString(join(HEADS, curBranch))),Commit.readCommit(Commit.commitExist(ID)));
        writeContents(join(HEADS, curBranch), Commit.commitExist(ID));
    }


}

