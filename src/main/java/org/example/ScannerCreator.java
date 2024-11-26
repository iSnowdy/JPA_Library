package org.example;

import java.util.Scanner;
import java.util.logging.Logger;

// Utility class to create and use Scanners
public class ScannerCreator {
    // Scanner Object declared as final as a protection method
    private static final Scanner SCANNER = new Scanner(System.in);
    // We only want this class to be able to create and utilize methods from Scanner, hence why private
    private ScannerCreator() {}

    public static String next() {
        return SCANNER.next();
    }

    // Prevents InputMissMatch Exception
    public static int nextInt() {
        while (true) {
            if (SCANNER.hasNextInt()) {
                return SCANNER.nextInt();
            } else {
                System.out.println("Invalid input. It must be an integer. Please try again");
                SCANNER.nextLine();
            }
        }
    }

    public static String nextLine() {
        return SCANNER.nextLine();
    }
    // Customized nextLine(). This way, if the user, at any point, wants to exit the current flow of the
    // program, we will throw this exception
    public static String nextLineWithExitCheck() throws ExitException {
        return checkExit(nextLine());
    }

    private static String checkExit(String input) throws ExitException {
        if ("EXIT".equalsIgnoreCase(input)) {
            throw new ExitException("User chose to exit");
        }
        return input;
    }
}