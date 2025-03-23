package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World {
    public static final int WIDTH = 60;
    public static final int HEIGHT = 30;
    private TETile[][] WORLD = new TETile[WIDTH][HEIGHT];
    private List<Room> rooms = new ArrayList<>();

    private static final int MIN_NUM_ROOMS = 8;

    private TETile[][] ENCOUNTER = new TETile[WIDTH][HEIGHT];

    private TETile defaultTile = Tileset.FLOOR;
    private int worldTheme;
    private int numGrass = 5;


    public void generateWorld(long seed) {
        // fill world with nothing tiles
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                WORLD[x][y] = Tileset.NOTHING;
            }
        }

        // generate a random number of rooms
        Random R = new Random(seed);
        int numRooms = R.nextInt(5) + MIN_NUM_ROOMS; // random int between 8 and 12

        /*
        // pick a theme
        worldTheme = R.nextInt(3);
        if (worldTheme == 0) {
            defaultTile = Tileset.FLOOR;
        } else if (worldTheme == 1) {
            defaultTile = Tileset.SAND;
        } else if (worldTheme == 2) {
            defaultTile = Tileset.WATER;
        }
         */

        // generate rooms
        for (int i = 0; i < numRooms; i++) {
            generateRoom(R);
        }

        // connect rooms
        connectRooms();


        // add 5 grass tiles in the main world
        for (int i = 0; i < 5; i += 1) {
            int[] grassCoords = getRandomDefaultTile(seed);
            int grassX = grassCoords[0];
            int grassY = grassCoords[1];
            if (WORLD[grassX][grassY] == Tileset.GRASS) {
                i += 1;
            } else {
                WORLD[grassX][grassY] = Tileset.GRASS;
            }
        }



        /*
        // (original) render world
        renderWorld() done in Main.java
        */
    }

    private void generateRoom(Random random) {
        // generate a room at a random position with random dimensions
        int roomWidth = random.nextInt(6) + 5; // random width between 5 and 10
        int roomHeight = random.nextInt(6) + 5; // random height between 5 and 10
        int roomX = random.nextInt(WIDTH - roomWidth);
        int roomY = random.nextInt(HEIGHT - roomHeight);

        // check for overlapping rooms
        if (!doesOverlap(roomX, roomY, roomWidth, roomHeight)) {

            // add room to world
            for (int x = roomX; x < roomX + roomWidth; x += 1) {
                for (int y = roomY; y < roomY + roomHeight; y += 1) {
                    WORLD[x][y] = defaultTile;
                }
            }
            // override walls of room with wall tiles
            for (int x = roomX; x < roomX + roomWidth; x += 1) {
                WORLD[x][roomY] = Tileset.WALL; // top wall
                WORLD[x][roomY + roomHeight - 1] = Tileset.WALL; // bottom wall
            }
            for (int y = roomY; y < roomY + roomHeight; y += 1) {
                WORLD[roomX][y] = Tileset.WALL; // left wall
                WORLD[roomX + roomWidth - 1][y] = Tileset.WALL; // right wall
            }
            // add room to the list of rooms
            rooms.add(new Room(roomX, roomY, roomWidth, roomHeight));
        } else { // if generated room overlaps
            generateRoom(random);
        }
    }

    private void connectRooms() {
        // connect each room to neighbor room
        for (int i = 0; i < rooms.size() - 1; i += 1) {
            Room room0 = rooms.get(i);
            Room room1 = rooms.get(i + 1);
            connectRooms(room0, room1);
        }
    }

    private void connectRooms(Room room0, Room room1) {
        // connect room0 to room1 by generating a hallway
        int x0 = room0.getX() + room0.getWidth() / 2;
        int y0 = room0.getY() + room0.getHeight() / 2;
        int x1 = room1.getX() + room1.getWidth() / 2;
        int y1 = room1.getY() + room1.getHeight() / 2;

        // generate horizontal hallway
        generateHorizontalHallway(x0, x1, y0);

        // generate vertical hallway
        generateVerticalHallway(x1, y0, y1);
    }

    private void generateHorizontalHallway(int x0, int x1, int y) {
        int startX = Math.min(x0, x1);
        int endX = Math.max(x0, x1);

        for (int x = startX; x <= endX; x += 1) {
            if (WORLD[x][y] == Tileset.NOTHING) {
                WORLD[x][y] = defaultTile;
            }
            if (WORLD[x][y - 1] == Tileset.NOTHING) {
                WORLD[x][y - 1] = Tileset.WALL;
            }
            if (WORLD[x][y + 1] == Tileset.NOTHING) {
                WORLD[x][y + 1] = Tileset.WALL;
            }
            // replace wall tile of room with floor tile at connection with hallway
            if (WORLD[x][y] == Tileset.WALL) {
                WORLD[x][y] = defaultTile;
            }
        }
    }

    private void generateVerticalHallway(int x, int y0, int y1) {
        int startY = Math.min(y0, y1);
        int endY = Math.max(y0, y1);

        for (int y = startY; y <= endY; y += 1) {
            if (WORLD[x][y] == Tileset.NOTHING) {
                WORLD[x][y] = defaultTile;
            }
            if (WORLD[x - 1][y] == Tileset.NOTHING) {
                WORLD[x - 1][y] = Tileset.WALL;
            }
            if (WORLD[x + 1][y] == Tileset.NOTHING) {
                WORLD[x + 1][y] = Tileset.WALL;
            }
            // replace wall tile of room with floor tile at connection with hallway
            if (WORLD[x][y] == Tileset.WALL) {
                WORLD[x][y] = defaultTile;
            }
        }
    }

    private boolean doesOverlap(int x, int y, int width, int height) {
        // check if generated room overlaps with existing rooms
        for (Room room : rooms) {
            if (x < room.getX() + room.getWidth()
                    && x + width > room.getX()
                    && y < room.getY() + room.getHeight()
                    && y + height > room.getY()) {
                return true;
            }
        }
        return false;
    }

    // old renderWorld(), replaced for HUD generation
    public void renderWorld() {
        // renders world using TERenderer
        TERenderer ter = new TERenderer();

        // original initialize() call
        // ter.initialize(WIDTH, HEIGHT);

        // initialize with offset for HUD (part 3B)
        ter.initialize(WIDTH, HEIGHT);

        ter.renderFrame(WORLD);
        /*
        StdDraw.clear(Color.BLACK);
        ter.drawTiles(WORLD);
         */
    }

    public TETile[][] getWorld() {
        // returns the state of the world
        return WORLD;
    }

    public TETile getDefaultTile() {
        return defaultTile;
    }

    /**
     * If/else statement generated by ChatGPT
     * I wrote the body of the if statement
     * @return the String description of the tile the cursor is hovering over
     */
    public String getTileInfo() {
        int mouseX = (int) StdDraw.mouseX();
        int mouseY = (int) StdDraw.mouseY() - 2;

        if (mouseX >= 0 && mouseX < WIDTH && mouseY >= 0 && mouseY < HEIGHT) {
            // Get tile information based on mouse position (replace this with your logic)
            return "        Hovered tile: " + WORLD[mouseX][mouseY].description();
        } else {
            return ""; // No tile information if mouse is outside world bounds
        }
    }

    /**
     * Generated by ChatGPT
     * Picks a random floor tile
     * @return an int[] array with the tile's x and y coordinates
     */
    public int[] getRandomDefaultTile(long seed) {
        int x, y;
        Random R = new Random(seed);

        // Keep picking random coordinates until a floor tile is found
        do {
            x = R.nextInt(WIDTH);
            y = R.nextInt(HEIGHT);
        } while (WORLD[x][y] != defaultTile);

        int[] coordinates = {x, y};
        return coordinates;
    }

    public void generateEncounterRoom(long seed) {
        // numbers written like this to avoid magic numbers
        int roomWidth = 8 + 4;
        int roomHeight = 8 + 4;
        int roomX = 8 * 3;
        int roomY = 8;

        // fill world with NOTHING tiles
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                ENCOUNTER[x][y] = Tileset.NOTHING;
            }
        }

        for (int x = roomX; x < roomX + roomWidth; x += 1) {
            for (int y = roomY; y < roomY + roomHeight; y += 1) {
                ENCOUNTER[x][y] = Tileset.FLOOR;
            }
        }
        // override walls of room with wall tiles
        for (int x = roomX; x < roomX + roomWidth; x += 1) {
            ENCOUNTER[x][roomY] = Tileset.WALL; // top wall
            ENCOUNTER[x][roomY + roomHeight - 1] = Tileset.WALL; // bottom wall
        }
        for (int y = roomY; y < roomY + roomHeight; y += 1) {
            ENCOUNTER[roomX][y] = Tileset.WALL; // left wall
            ENCOUNTER[roomX + roomWidth - 1][y] = Tileset.WALL; // right wall
        }

        // generate 8 grass tiles in the encounter room
        for (int i = 0; i < 8; i += 1) {
            int[] grassCoords = getRandomDefaultTile(seed);
            int grassX = grassCoords[0];
            int grassY = grassCoords[1];
            if (ENCOUNTER[grassX][grassY] == Tileset.GRASS) {
                i += 1;
            } else {
                ENCOUNTER[grassX][grassY] = Tileset.GRASS;
            }
        }
    }

    public void updateTouchedGrass(int x, int y) {
        if (WORLD[x][y] == Tileset.GRASS) {
            numGrass -= 1;
        }
        WORLD[x][y] = defaultTile;
    }

    public int getNumGrass() {
        return numGrass;
    }

    public TETile[][] getEncounter() {
        // returns the state of the encounter world
        return ENCOUNTER;
    }

}
