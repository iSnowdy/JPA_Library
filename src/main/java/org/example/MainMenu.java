package org.example;

public class MainMenu {
    private final HibernateOperations hibernateOperations;

    public MainMenu() {
        this.hibernateOperations = new HibernateOperations();
        System.out.println("Welcome to Library Manager 101!\n");

        mainMenuLoop();
    }

    private void mainMenuLoop() {
        int userChoice;

        do {
            printOptions();
            System.out.print("Select an option: ");
            try {
                userChoice = ScannerCreator.nextInt();

                if (!validateMenuOption(userChoice)) {
                    System.out.println("Invalid option! Please select a valid option (1 - 7)\n");
                    continue;
                }
                executeMenuChoice(userChoice);
            } catch (ExitException e) {
                System.out.println("Returning to the main menu...\n");
            }
        } while (true);
    }

    private void printOptions() {
        System.out.println("===================================================");
        System.out.println("|                   MAIN MENU                     |");
        System.out.println("---------------------------------------------------");
        System.out.println("|              1. Add New User                    |");
        System.out.println("|              2. Add New Book                    |");
        System.out.println("|              3. Add New Lend                    |");
        System.out.println("|              4. Return Book                     |");
        System.out.println("|              5. Show Lends by Year              |");
        System.out.println("|              6. List All Books in Lend by User  |");
        System.out.println("|              7. Exit                            |");
        System.out.println("===================================================");
    }

    private boolean validateMenuOption(int option) {
        return option >= 1 && option <= 7;
    }

    private void executeMenuChoice(int choice) throws ExitException {
        switch (choice) {
            case 1 -> hibernateOperations.addNewUser();
            case 2 -> hibernateOperations.addNewBook();
            case 3 -> hibernateOperations.addNewLend();
            case 4 -> hibernateOperations.returnBook();
            case 5 -> hibernateOperations.printLendByYear();
            case 6 -> hibernateOperations.printBookByUserLend();
            case 7 -> exitApplication();
            default -> throw new IllegalStateException("Unexpected value: " + choice);
        }
    }

    private void exitApplication() {
        System.out.println("Goodbye!");
        hibernateOperations.closeHibernate();
        System.exit(0);
    }
}