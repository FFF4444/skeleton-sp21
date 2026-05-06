package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class WorldGenerator {
    private final TETile WALL = Tileset.WALL;
    private final TETile FLOOR = Tileset.FLOOR;
    private final int MAXDEPTH = 8;
    public int width;
    public int height;
    private WorldNode root;
    private TETile[][] world;
    private long seed;
    public WorldGenerator(TETile[][] world, long seed) {
        this.world = world;
        this.seed = seed;
        width = world.length;
        height = world[0].length;
        initWorld(world);
        root = new WorldNode(0, 0, height, width, seed);
    }
    private void initWorld(TETile[][] world) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                world[i][j] = Tileset.NOTHING;
            }
        }
    }
    private boolean inBound(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
    private void createWall() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (world[i][j] == Tileset.NOTHING) {
                    for (int[] dir : new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1},
                            {1, 1}, {1, -1}, {-1, -1}, {-1, 1}}) {
                        int nx = i + dir[0], ny = j + dir[1];
                        if (inBound(nx, ny) && world[nx][ny] == FLOOR) {
                            world[i][j] = WALL;
                            break;
                        }
                    }
                }
            }
        }
    }
    public void createWorld() {
        root.splitNode(MAXDEPTH, 0);
        root.createRoom(world, FLOOR);
        root.createHallway(world, FLOOR);
        createWall();
    }
    public boolean canMove(int x, int y) {
        return world[x][y] == FLOOR;
    }
}
