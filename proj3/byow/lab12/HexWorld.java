package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;
    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);

    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(6);
        switch (tileNum) {
            case 0: return Tileset.WALL;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.AVATAR;
            case 3: return Tileset.FLOOR;
            case 4: return Tileset.GRASS;
            case 5: return Tileset.MOUNTAIN;
            default: return Tileset.NOTHING;
        }
    }
    /** (x,y)作为六边形中间靠上的最左点的坐标.
     * 用砖块类型生成边长为n的六边形*/
    public static void addHexagon(int n, int x, int y, TETile teTile, TETile[][] tiles) {
        halfHexagon(n, x, y, teTile, tiles, 1);
        halfHexagon(n, x, y - 1, teTile, tiles, -1);
    }
    private static void halfHexagon(int n, int x, int y, TETile teTile, TETile[][] tiles, int yStep) {
        int i, j;
        for (i = 0; i < n; i++) {
            for (j = 0; j < 3 * n - 2 -2 * i; j++) {
                tiles[x + j + i][y + i * yStep] = teTile;
            }
        }
    }
    /**初始化tiles全为nothing方块*/
    public static void nothingInit(TETile[][] tiles) {
        for (int i = 0; i < tiles[0].length; i++) {
            for (int j = 0; j < tiles.length; j++) {
                tiles[i][j] = Tileset.NOTHING;
            }
        }
    }
    private static void someHex(int n, TETile[][] tiles, int hexagonLength, int x, int y) {
        for (int i = 0; i < n; i++) {
            addHexagon(hexagonLength, x,
                     y + 2 * i * hexagonLength, randomTile(), tiles);
        }
    }
    public static void asembleHex(int n, TETile[][] tiles, int hexagonLength) {
        int width = tiles[0].length, i;
        int initXPoint = width / 2 - 3 * hexagonLength / 2 + 1, initYPoint = hexagonLength;
        someHex(n, tiles, hexagonLength, initXPoint, initYPoint);
        for (i = 1; i <= n / 2; i++) {
            someHex(n - i, tiles, hexagonLength, initXPoint + i * (2 * hexagonLength - 1),
                    initYPoint + i * hexagonLength);
            someHex(n - i, tiles, hexagonLength, initXPoint - i * (2 * hexagonLength - 1),
                    initYPoint + i * hexagonLength);
        }
    }
    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] HexTiles = new TETile[WIDTH][HEIGHT];
        nothingInit(HexTiles);
        asembleHex(6, HexTiles, 3);
        ter.renderFrame(HexTiles);
    }

}
