package flashcards;

import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String importFileName = null;
        String exportFileName = null;

        if (args.length > 0) {
            for (int i = 0; i < args.length; i += 2) {
                if (i + 1 < args.length) {
                    if (args[i].equals("-import")) {
                        importFileName = args[i + 1];
                    } else if (args[i].equals("-export")) {
                        exportFileName = args[i + 1];
                    }
                }
            }
        }

        UserInterface ui = new UserInterface(scanner, importFileName, exportFileName);
        ui.start();
    }

//    public static void bootInterface() {
//        Scanner scanner = new Scanner(System.in);
//
//        UserInterface ui = new UserInterface(scanner);
//        ui.start();
//    }
}
