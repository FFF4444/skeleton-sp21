package byow.Core;

import byow.TileEngine.TETile;

import java.util.Random;

public class Room {
    private int x, y, height, width;
    /**(x,y)为矩形房间左下角的点的坐标
     * 高度宽度不包含围墙*/
    public Room(int x, int y, int height, int width) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
    }

    public void makeRoom(TETile[][] world, TETile road) {
        int i, j;
        for (i = 0; i < width; i++) {
            for (j = 0; j < height; j++) {
                world[x + i][y + j] = road;
            }
        }
    }
    public static void randomConnectRoom(TETile[][] world, Room roomA, Room roomB, Random random, TETile road) {
        if (roomA == null || roomB == null) {
            return;
        }
        int ax = roomA.x + random.nextInt(roomA.width - 1) + 1;
        int ay = roomA.y + 1 + random.nextInt(roomA.height - 1);
        int bx = roomB.x + random.nextInt(roomB.width - 1) + 1;
        int by = roomB.y + 1 + random.nextInt(roomB.height - 1);
        for (int i = Math.min(ax, bx); i <= Math.max(ax, bx); i++) {
            world[i][ay] = road;
        }
        for (int i = Math.min(ay, by); i <= Math.max(ay, by); i++) {
            world[bx][i] = road;
        }
    }
}
