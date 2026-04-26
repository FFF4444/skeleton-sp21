package gitlet;

// TODO: any imports you need here
import static gitlet.Utils.*;
import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
// TODO: You'll likely use this in this class

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private Date timeStamp;
    private String prev = null;
    private String secPrev = null;
    private TreeMap<String, String> blob;

    /* TODO: fill in the rest of this class. */

    public Commit() {
        message = "initial commit";
        timeStamp = new Date(0);
        blob = new TreeMap<>();
    }
    public Commit(String message, String prev) {
        this.message = message;
        timeStamp = new Date();
        this.prev = prev;
        TreeMap<String, String> prevTree = readCommit(prev).getBlob();
        blob = new TreeMap<>(prevTree);
    }

    public static Commit readCommit(String hash) {
        File c = join(Repository.COMMITS, hash);
        if (c.exists()) {
            return readObject(c, Commit.class);
        }
        return null;
    }

    public static String commitExist(String hash) {
        List<String> commits = Utils.plainFilenamesIn(Repository.COMMITS);
        for(String i : commits) {
            if (i.startsWith(hash)) {
                return i;
            }
        }
        return null;
    }
    public TreeMap<String, String> getBlob() {
        return blob;
    }

    public String getHash() {
        return sha1(serialize(this));
    }
    public boolean fileExist(String name, Serializable file) {
        String hash = sha1(serialize(file));
        if (blob.containsKey(name) && blob.get(name).equals(hash)) {
            return true;
        }
        return false;
    }
    public void add(TreeMap<String, String> map) {
        blob.putAll(map);
    }
    public void rm(TreeSet<String> set) {
        blob.keySet().removeAll(set);
    }
    public void printCommit() {
        System.out.println("===");
        System.out.println("commit " + this.getHash());
        if(secPrev != null) {
            System.out.println("Merge: " + prev.substring(0, 7) + " " + secPrev.substring(0, 7));
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("E MMM dd HH:mm:ss yyyy Z");
        String formattedDate = String.format(Locale.US,
                "%ta %tb %te %tT %tY %tz",
                timeStamp, timeStamp, timeStamp, timeStamp, timeStamp, timeStamp
        );
        System.out.println("Date: " + formattedDate);
        System.out.println(message);
        System.out.println();
    }
    public String prev() {
        return prev;
    }
    public String getMessage() {
        return message;
    }
}
