package byow.Core;

import byow.TileEngine.TETile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldNode {
    private int x, y, height, width;
    private List<Room> rooms = new ArrayList<>();
    private WorldNode left = null, right = null;
    private Random random;
    private long seed;
    private static final int MIN_SPLIT_SIZE = 3;
    private static final int EXP_ROOM_SIZE = 8;
    public WorldNode(int x, int y, int height, int width, Random random) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        this.random = random;
    }
    public WorldNode(int x, int y, int height, int width, long seed) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        this.seed = seed;
        this.random = new Random(seed);

    }
    public void splitNode(int maxDepth, int depth) {
        if (width <= MIN_SPLIT_SIZE || height <= MIN_SPLIT_SIZE) {
            return;
        }
        if (depth == maxDepth || width <= EXP_ROOM_SIZE || height <= EXP_ROOM_SIZE) {
            int roomHeight = random.nextInt(height - 3) + 2;
            int roomWidth = random.nextInt(width - 3) + 2;
            int roomX = random.nextInt(Math.max(1, width - roomWidth - 1)) + x + 1;
            int roomY = random.nextInt(Math.max(1, height - roomHeight - 1)) + y + 1;
            rooms.add(new Room(roomX, roomY, roomHeight, roomWidth));
            return;
        }
        if (height > width) {
            splitY();
        } else if (height == width) {
            int n = random.nextInt(2);
            if (n == 0) {
                splitY();
            } else {
                spiltX();
            }
        } else {
            spiltX();
        }
        left.splitNode(maxDepth, depth + 1);
        right.splitNode(maxDepth, depth + 1);
    }
    private void splitY() {
        int childHeight = random.nextInt(height / 2) + height / 4;
        left = new WorldNode(x, y, childHeight, width, random);
        right = new WorldNode(x, y + childHeight, height - childHeight, width, random);
    }
    private void spiltX() {
        int childWidth = random.nextInt(width / 2) + width / 4;
        left = new WorldNode(x, y, height, childWidth, random);
        right = new WorldNode(x + childWidth, y, height, width - childWidth, random);
    }
    public void createRoom(TETile[][] world, TETile road) {
        if (left == null && right == null) {
            if (!rooms.isEmpty()) {
                rooms.get(0).makeRoom(world, road);
            }
            return;
        }
        left.createRoom(world, road);
        right.createRoom(world, road);
    }
    public void createHallway(TETile[][] world, TETile road) {
        if (left == null && right == null) {
            return;
        }
        left.createHallway(world, road);
        right.createHallway(world, road);
        rooms.addAll(left.rooms);
        rooms.addAll(right.rooms);
        Room.randomConnectRoom(world, left.getRandomRoom(), right.getRandomRoom(), random, road);
    }
    private Room getRandomRoom() {
        if (rooms.isEmpty()) {
            return null;
        }
        return rooms.get(random.nextInt(rooms.size()));
    }
}
