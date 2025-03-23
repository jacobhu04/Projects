package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;
import utils.FileUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

public class Main {
    private static final double POINT_TWO = 0.2;
    private static final double POINT_FOUR = 0.4;
    private static final double POINT_SIX = 0.6;
    private static final double POINT_EIGHT = 0.8;
    private static final int TEN = 10;
    private static final int FIFTY = 50;

    private static boolean loadWorldSelected = false;

    private static int avatarX;
    private static int avatarY;

    private static String inputsAsText = "";

    // colonTyped used to check if a colon is typed immediately before q
    private static boolean colonTyped = false;

    private static final String SAVE_FILE = "src/core/SaveInfo.txt";

    private static boolean worldIlluminated = true;

    private static ArrayList<Integer> grassTouched;

    public static void main(String[] args) {
        displayMainMenu();
        long seed = getSeedFromMainMenu();

        // taken out of while loop from above; based on WaterWorld code
        World world = new World();
        world.generateWorld(seed);
        TERenderer ter = new TERenderer();
        ter.initialize(world.WIDTH, world.HEIGHT + 2, 0, 2);

        // initialize avatar
        int[] avatarStartCoords = world.getRandomDefaultTile(seed);
        avatarX = avatarStartCoords[0];
        avatarY = avatarStartCoords[1];
        if (loadWorldSelected) { // get saved avatar position if load world option selected
            updateAvatarPositionFromSave(world);
            /*
            if (!grassTouched.isEmpty()) {
                int numGrassTilesTouched = grassTouched.size() / 2;
                TETile[][] worldArray = world.getWorld();
                for (int i = 0; i < numGrassTilesTouched; i += 1) {
                    worldArray[grassTouched.get(i)][grassTouched.get(i + 1)] = world.getDefaultTile();
                }
            }
             */
        }

        int numGrassTiles = world.getNumGrass();

        // main game loop
        while (true) {
            TETile[][] worldArray = world.getWorld();
            StdDraw.clear(Color.BLACK);

            if (worldIlluminated) { // draw all tiles
                ter.drawTiles(worldArray);

                // draw tile info HUD
                StdDraw.setPenColor(Color.WHITE);
                StdDraw.text(4, 1, world.getTileInfo());
            } else { // draw the visible tiles
                ter.drawVisibleTiles(worldArray, avatarX, avatarY);
                if (StdDraw.mouseX() >= avatarX - 4 && StdDraw.mouseX() <= avatarX + 5
                        && StdDraw.mouseY() >= avatarY - 2 && StdDraw.mouseY() <= avatarY + 7) {
                    // draw tile info HUD
                    StdDraw.setPenColor(Color.WHITE);
                    StdDraw.text(4, 1, world.getTileInfo());
                } else { // if the mouse position is outside the illuminated area
                    StdDraw.setPenColor(Color.WHITE);
                    StdDraw.text(4, 1, "Spooky!");
                }
            }

            // HUD
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.text(FIFTY, 1, "World illuminated: " + worldIlluminated
                    + ". Press 'i' to toggle.");
            StdDraw.text(5 * 6, 1, "Touch all the grass.");

            // handle user inputs during game
            handleUserInputDuringGame(worldArray);

            // draw avatar
            drawAvatar();

            if (worldArray[avatarX][avatarY] == Tileset.GRASS) {
                worldArray[avatarX][avatarY] = world.getDefaultTile();
                numGrassTiles -= 1;
                if (numGrassTiles == 0) {
                    displayWin();
                    break;
                }
            }


            StdDraw.show();
        }
    }

    private static void displayMainMenu() {
        StdDraw.clear();
        StdDraw.text(0.5, POINT_EIGHT, "Main Menu:");
        StdDraw.text(0.5, POINT_SIX, "N: New World");
        StdDraw.text(0.5, POINT_FOUR, "L: Load World");
        StdDraw.text(0.5, POINT_TWO, "Q: Quit");
        StdDraw.show();
    }

    private static long getSeedFromMainMenu() {
        StringBuilder seedInput = new StringBuilder();
        long seed;
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (key == 'L') {
                    // load saved world
                    inputsAsText += FileUtils.readFile(SAVE_FILE);
                    seed = getSeedFromInput(inputsAsText);
                    loadWorldSelected = true;
                    break;
                } else if (key == 'N') { // create new world
                    StdDraw.clear();
                    StdDraw.text(0.5, POINT_SIX, "Enter a random seed:");
                    StdDraw.show();
                    // add typed key to inputsAsText for saving
                    inputsAsText += key;
                } else if (Character.isDigit(key)) {
                    // append typed character to seedInput
                    seedInput.append(key);
                    // display entered seed so far
                    StdDraw.clear();
                    StdDraw.text(0.5, 0.5, "Seed: " + seedInput.toString());
                    StdDraw.show();
                    // add typed key to inputsAsText for saving
                    inputsAsText += key;
                } else if (key == 'S') {
                    seed = Long.parseLong(seedInput.toString());
                    // add typed key to inputsAsText for saving
                    inputsAsText += key;
                    break;
                } else if (key == 'Q') {
                    System.exit(0);
                }
            }
        }
        return seed;
    }

    public static void handleUserInput(char key, TETile[][] world) {
        // key is already in uppercase
        if (key == 'W') {
            if (world[avatarX][avatarY + 1] != Tileset.WALL) {
                avatarY += 1;
                // reset :q call
                colonTyped = false;
                /*
                if (world[avatarX][avatarY] == Tileset.GRASS) {
                    grassTouched.add(avatarX);
                    grassTouched.add(avatarY);
                }

                 */

            }
        } else if (key == 'A') {
            if (world[avatarX - 1][avatarY] != Tileset.WALL) {
                avatarX -= 1;
                // reset :q call
                colonTyped = false;
                /*
                if (world[avatarX][avatarY] == Tileset.GRASS) {
                    grassTouched.add(avatarX);
                    grassTouched.add(avatarY);
                }

                 */
            }
        } else if (key == 'S') {
            if (world[avatarX][avatarY - 1] != Tileset.WALL) {
                avatarY -= 1;
                // reset :q call
                colonTyped = false;
                /*
                if (world[avatarX][avatarY] == Tileset.GRASS) {
                    grassTouched.add(avatarX);
                    grassTouched.add(avatarY);
                }

                 */
            }
        } else if (key == 'D') {
            if (world[avatarX + 1][avatarY] != Tileset.WALL) {
                avatarX += 1;
                // reset :q call
                colonTyped = false;

                /*
                if (world[avatarX][avatarY] == Tileset.GRASS) {
                    grassTouched.add(avatarX);
                    grassTouched.add(avatarY);
                }

                 */
            }
        } else if (key == ':') {
            colonTyped = true;
        } else if (key == 'Q' && colonTyped) {
            // save inputsAsString onto a file and exit
            FileUtils.writeFile(SAVE_FILE, inputsAsText);
            System.exit(0);
        } else if (key == 'I') { // change state of worldIlluminated
            if (worldIlluminated) {
                worldIlluminated = false;
            } else {
                worldIlluminated = true;
            }
        } else { // for all other keys, reset the :q call
            colonTyped = false;
        }
    }

    private static long getSeedFromInput(String input) {
        String[] inputsAsTextAsArray = input.split("");
        int i = 1;
        String seedAsString = "";
        while (!Objects.equals(inputsAsTextAsArray[i], "S")) {
            seedAsString += inputsAsTextAsArray[i];
            i += 1;
        }
        return Long.parseLong(seedAsString);
    }

    private static void drawAvatar() { // draws the avatar on the screen
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(avatarX + .5, avatarY + 2 + .5, "@");
        StdDraw.pause(TEN);
    }

    private static void handleUserInputDuringGame(TETile[][] worldArray) {
        if (StdDraw.hasNextKeyTyped()) {
            char key = StdDraw.nextKeyTyped();
            key = Character.toUpperCase(key);
            handleUserInput(key, worldArray);
            // add typed key to inputsAsText for saving and loading
            if (key != ':') {
                inputsAsText += key;
            }
        }
    }

    private static void updateAvatarPositionFromSave(World world) {
        // get to location of first (zeroth?) input after S
        int i = 1;
        while (inputsAsText.charAt(i) != 'S') {
            i += 1;
        }
        for (int j = i + 1; j < inputsAsText.length(); j += 1) {
            char key = inputsAsText.charAt(j);
            handleUserInput(key, world.getWorld());
            world.updateTouchedGrass(avatarX, avatarY);
        }
    }

    private static void displayWin() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(6 * 5, 3 * 5, "You touched all the grass! Congratulations!");
        StdDraw.show();
    }
}
