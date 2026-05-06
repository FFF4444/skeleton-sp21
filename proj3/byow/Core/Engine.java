package byow.Core;

import byow.InputDemo.StringInputDevice;
import byow.InputDemo.KeyboardInputSource;
import byow.InputDemo.InputSource;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;
import java.io.*;


public class Engine implements Serializable {
    private final transient TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    private long seed;
    private transient TETile[][] world;
    private transient WorldGenerator WG;
    private boolean init = false;
    private int playerX;
    private int playerY;
    private transient TETile playerTile = Tileset.AVATAR;
    private transient Menu menu;
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File SAVE = Utils.join(CWD, "savefile.txt");
    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public Engine() {
        world = new TETile[WIDTH][HEIGHT];
    }

    public void interactWithKeyboard() throws IOException {
        KeyboardInputSource inputDevice = new KeyboardInputSource();
        menu = new Menu(WIDTH, HEIGHT);
        menu.drawMenu();
        interactWithInput(inputDevice, true);
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        StringInputDevice inputDevice = new StringInputDevice(input);
        interactWithInput(inputDevice, false);
        return world;
    }

    public void interactWithInput(InputSource inputSource, boolean isKey) {
        StringBuilder stringBuilder;
        while (inputSource.possibleNextInput()) {
            char c = Character.toLowerCase(inputSource.getNextKey());
            if (!init && c == 'n') {
                if (menu != null) {
                    menu.printSeed();
                }
                stringBuilder = new StringBuilder();
                c = Character.toLowerCase(inputSource.getNextKey());
                while (c != 's') {
                    if (Character.isDigit(c)) {
                        stringBuilder.append(c);
                        if (menu != null) {
                            menu.printSeed(String.valueOf(stringBuilder));
                        }
                    }
                    c = Character.toLowerCase(inputSource.getNextKey());
                }
                seed = Long.parseLong(String.valueOf(stringBuilder));
                init = true;
                WG = new WorldGenerator(world, seed);
                WG.createWorld();
                swapnPlayer();
            }
            if (c == 'w' && init && WG.canMove(playerX, playerY + 1)) {
                playerY++;
            }
            if (c == 'a' && init && WG.canMove(playerX - 1, playerY)) {
                playerX--;
            }
            if (c == 's' && init && WG.canMove(playerX, playerY - 1)) {
                playerY--;
            }
            if (c == 'd' && init && WG.canMove(playerX + 1, playerY)) {
                playerX++;
            }
            if (c == 'q') {
                System.exit(0);
            }
            if (c == ':' && init) {
                c = Character.toLowerCase(inputSource.getNextKey());
                if (c == 'q') {
                    try {
                        Utils.writeObject(SAVE, this);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    System.exit(0);
                }
            }
            if (c == 'l' && !init) {
                load();
            }
            if (isKey && init) {
                render();
            }
        }
    }
    private void render() {
        TETile ori = world[playerX][playerY];
        world[playerX][playerY] = playerTile;
        ter.renderFrame(world);
        world[playerX][playerY] = ori;
    }

    private void swapnPlayer() {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (WG.canMove(i, j)) {
                    playerY = j;
                    playerX = i;
                    return;
                }
            }
        }
    }
    private void load() {
        if (!SAVE.exists()) {
            return;
        }
        Engine l = Utils.readObject(SAVE, Engine.class);
        playerY = l.playerY;
        playerX = l.playerX;
        seed = l.seed;
        WG = new WorldGenerator(world, seed);
        WG.createWorld();
        init = l.init;
    }
}
