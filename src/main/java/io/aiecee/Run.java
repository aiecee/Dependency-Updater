package io.aiecee;

import io.aiecee.runescape.GameDefinition;
import io.aiecee.runescape.GameType;

/**
 * Date: 30/04/2014
 * Time: 09:13
 *
 * @author Matt Collinge
 */
public class Run {

    private static void printHelp() {
        System.out.println("For EOC updater use \"eoc\" as an argument");
        System.out.println("For OS updater use \"os\" as an argument");
        System.out.println("To dump the gamepack use \"dump\" as an argument");
        System.exit(-1);
    }

    public static void main(String[] args) {

        if (args.length == 0) {
            printHelp();
        }

        boolean eoc = false;
        boolean os = false;
        boolean dump = false;

        for (String arg: args) {
            switch (arg.toLowerCase()) {
                case "eoc":
                    eoc = true;
                    break;
                case "os":
                    os = true;
                    break;
                case "dump":
                    dump = true;
                    break;
                case "help":
                case "h":
                    printHelp();
            }
        }

        if (!eoc && !os) {
            System.out.println("You must define a game type.");
            printHelp();
        }

        GameDefinition definition;
        Updater updater;

        if (eoc) {
            definition = new GameDefinition(GameType.EVOLUTION_OF_COMBAT);
            updater = new Updater(definition);
            updater.run();
        }

        if (os) {
            definition = new GameDefinition(GameType.OLD_SCHOOL);
            updater = new Updater(definition);
            updater.run();
        }

    }

}
