package core;

import tileengine.TETile;
import tileengine.Tileset;
import utils.FileUtils;

public class AutograderBuddy {

    /**
     * Simulates a game, but doesn't render anything or call any StdDraw
     * methods. Instead, returns the world that would result if the input string
     * had been typed on the keyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quit and
     * save. To "quit" in this method, save the game to a file, then just return
     * the TETile[][]. Do not call System.exit(0) in this method.
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public static TETile[][] getWorldFromInput(String input) {
        World world = new World();
        String[] commands = input.split("");

        final String saveFile = "src/core/SaveInfo.txt";

        long seed = 0;
        boolean seedStarted = false;
        boolean seedEntered = false;
        StringBuilder seedBuilder = new StringBuilder();

        String inputs = "";
        boolean colonTyped = false;

        for (int i = 0; i < commands.length; i += 1) {
            // check if seed input has started
            String command = commands[i];
            if (seedStarted && !command.equals("s") && !command.equals("S")) {
                seedBuilder.append(command);
            }

            // check if seed has been entered
            if (!seedEntered) {
                if (command.equals("n") || command.equals("N")) {
                    seedStarted = true;
                } else if (command.equals("s") || command.equals("S")) {
                    // parse seed from seedBuilder
                    seed = Long.parseLong(seedBuilder.toString());
                    seedEntered = true;
                    seedStarted = false;
                }
            }

            // handle inputs
            char key = command.charAt(0);
            if (key != 's' && key != ':' && key != 'q' && key != 'l') {
                Main.handleUserInput(key, world.getWorld());
                inputs += command;
                colonTyped = false;
            } else if (key == ':') {
                colonTyped = true;
            } else if (key == 'q' && colonTyped) {
                FileUtils.writeFile(saveFile, "n" + seed + "s" + inputs);
            }
        }




        // generate world using the extracted seed
        world.generateWorld(seed);

        // return resulting world state
        return world.getWorld();
    }

    /**
     * Used to tell the autograder which tiles are the floor/ground (including
     * any lights/items resting on the ground). Change this
     * method if you add additional tiles.
     */
    public static boolean isGroundTile(TETile t) {
        return t.character() == Tileset.FLOOR.character()
                || t.character() == Tileset.AVATAR.character()
                || t.character() == Tileset.FLOWER.character();
    }

    /**
     * Used to tell the autograder while tiles are the walls/boundaries. Change
     * this method if you add additional tiles.
     */
    public static boolean isBoundaryTile(TETile t) {
        return t.character() == Tileset.WALL.character()
                || t.character() == Tileset.LOCKED_DOOR.character()
                || t.character() == Tileset.UNLOCKED_DOOR.character();
    }
}
