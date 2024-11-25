package org.example;

import jakarta.persistence.PersistenceException;
import org.hibernate.*;
import org.hibernate.exception.ConstraintViolationException;

import java.time.LocalDate;
import java.util.List;

public class HibernateOperations implements Operations {
    private Book book;
    private User user;
    private Lend lend;

    private String
            userCode, userName, userSurname, userDateOfBirth,
            bookISBN, bookTitle, bookPublisher,
            lendStartDate, lendEndDate;

    private int bookCopies;

    private final ValidationUtil validationUtil;

    // TODO: ExitException inside catch? Session Factory static from HibernateUtil?
    public HibernateOperations() {
        this.validationUtil = new ValidationUtil();
    }

    // This method will ask the user if they want to continue giving inputs. If not,
    // it will throw an exception to go back to the main menu
    private void continuePromptingData(final String origin) throws ExitException {
        while (true) {
            System.out.println("Invalid input(s)");
            System.out.println("Would you like to try again? (Y/n)");
            String answer = ScannerCreator.nextLine().trim().toLowerCase();

            if (answer.equals("y") || answer.isEmpty()) {
                switch (origin.toLowerCase()) {
                    case "user" -> askForUserData();
                    case "book" -> askForBookData();
                    case "lend" -> askForLendData();
                    default -> System.out.println("Unknown origin: " + origin);
                }
                break;

            } else if (answer.equals("n")) {
                //System.out.println("Returning to the main menu");
                throw new ExitException("User does not want to continue");

            } else {
                System.out.println("Invalid answer. Please type Y or N.");
            }
        }
    }
    // Abstracted method to handle errors during transactions and make the user
    // go back to the main menu
    private void handleTransactionError(Transaction transaction, Exception e, String origin) throws ExitException {
        System.err.println("Error during the transaction. Rolling back...");
        e.printStackTrace();
        transaction.rollback();
        System.out.println("Returning to the main menu...");
        throw new ExitException("Error during the transaction for " + origin + " data");
    }

    // Abstracted method to ask for user input. WIth the 'EXIT' keyword to redirect to main menu
    private String askForInput(String fieldName) throws ExitException {
        System.out.print(fieldName + ": ");
        return ScannerCreator.nextLineWithExitCheck();
    }
    // TODO: Add ExitException to nextInt() ?
    private int askForIntInput(String fieldName) throws ExitException {
        System.out.print(fieldName + ": ");
        try {
            return Integer.parseInt(ScannerCreator.nextLine());
        } catch (NumberFormatException e) { // If the user gives a String as input
            continuePromptingData("book");
        }
        return 1;
    }

    // Asks for the user/book/lend data information, validates it and if it is valid
    // persists the data. Else, asks the user if they want to continue. If
    // not, return to the main menu.
    // https://stackoverflow.com/questions/3220336/whats-the-use-of-session-flush-in-hibernate
    @Override
    public void addNewUser() throws ExitException {
        ScannerCreator.nextLine();
        System.out.println("Initializing new user session...");
        try (Session userSession = HibernateUtil.openSession()) {
            Transaction transaction = userSession.beginTransaction();
            try {
                askForUserData();
                User userToAdd = createUser();
                userSession.flush(); // Synchronizes the in-memory state of the current session with the DB
                userSession.persist(userToAdd);
                transaction.commit();

                System.out.println("User with the following information has been successfully added:");
                System.out.println(userToAdd);
            } catch (IllegalStateException | PersistenceException e) {
                handleTransactionError(transaction, e, "user");
            }
        }
    }

    private void askForUserData() throws ExitException {
        System.out.println("If you would like to exit at any point, please type 'EXIT'");
        System.out.println("Please provide the following information regarding the user to add:");

        this.userCode = askForInput("User Code");
        isUserInTheSystem();

        this.userName = askForInput("Name");
        this.userSurname = askForInput("Surname");
        this.userDateOfBirth = askForInput("Date of Birth (YYYY-MM-DD)");

        if (!isValidUserData()) {
            continuePromptingData("user");
        }
    }

    private void isUserInTheSystem() throws ExitException {
        User user = selectUser(true);
        if (user != null) {
            System.out.println("User already exists");
            continuePromptingData("user");
        }
    }

    private boolean isValidUserData() {
        return validationUtil.isValidUser(this.userCode, this.userName, this.userSurname, this.userDateOfBirth);
    }

    private User createUser() {
        User user = new User();
        user.setCodigo(this.userCode);
        user.setNombre(this.userName);
        user.setApellidos(this.userSurname);
        user.setFechanacimiento(LocalDate.parse(this.userDateOfBirth));
        return user;
    }

    @Override
    public void addNewBook() throws ExitException {
        ScannerCreator.nextLine();
        System.out.println("Initializing new book session...");

        try (Session bookSession = HibernateUtil.openSession()) {
            Transaction transaction = bookSession.beginTransaction();
            try {
                askForBookData();
                Book bookToAdd = createBook();
                bookSession.flush();
                bookSession.persist(bookToAdd);
                transaction.commit();

                System.out.println("Book with the following information has been successfully added:");
                System.out.println(bookToAdd);
            } catch (ConstraintViolationException e) {
                transaction.rollback();
                System.err.println("Book already exists");
                throw new ExitException("Book duplicate insert");
            } catch (IllegalStateException | PersistenceException e) {
                handleTransactionError(transaction, e, "book");
            }
        }
    }

    private void askForBookData() throws ExitException {
        System.out.println("If you would like to exit at any point, please type 'EXIT'");
        System.out.println("Please provide the following information regarding the book you wish to add:");

        this.bookISBN = askForInput("ISBN");
        //isBookInTheSystem();
        this.bookTitle = askForInput("Title");
        this.bookCopies = askForIntInput("Amount of Copies");
        this.bookPublisher = askForInput("Publisher");

        if (!isValidBookData()) {
            continuePromptingData("book");
        }
    }

    private void isBookInTheSystem() throws ExitException {
        Book book = selectBook(true);
        if (book != null) {
            System.out.println("Book already exists");
            continuePromptingData("book");
        }
    }

    private boolean isValidBookData() {
        return validationUtil.isValidBook(this.bookISBN, this.bookTitle, this.bookPublisher);
    }

    private Book createBook() {
        Book book = new Book();
        book.setIsbn(this.bookISBN);
        book.setTitulo(this.bookTitle);

        if (this.bookCopies > 0) {
            book.setCopias(this.bookCopies);
        }

        book.setEditorial(this.bookPublisher);
        return book;
    }

    /*
    1. Ask if the lending date is today or not
    2. Ask if there's a returning date
    3. Validate the dates
    4. Check if the desired book exists and/or is available to be lent
    5. Check if the user exists
     */

    @Override
    public void addNewLend() throws ExitException {
        ScannerCreator.nextLine();
        System.out.println("Initializing new lending session...");

        try (Session lendSession = HibernateUtil.openSession()) {
            Transaction transaction = lendSession.beginTransaction();
            try {
                askForLendData();
                Lend lendInformation = createLend();
                lendSession.flush();
                lendSession.persist(lendInformation);
                transaction.commit();

                System.out.println("Lend with the following information has been successfully added");
                System.out.println(lendInformation);
            } catch (IllegalStateException | PersistenceException e) {
                handleTransactionError(transaction, e, "lend");
            }
        }
    }

    private void askForLendData() throws ExitException {
        System.out.println("If you would like to exit at any point, please type 'EXIT'");
        System.out.println("Please provide the following information regarding the book to be lent:");

        this.lendStartDate = getLendingStartDate();
        this.lendEndDate = getLendingEndDate();

        validateLendDates();

        if (!isValidUser()) {
            System.out.println("The user does not exist");
            continuePromptingData("lend");
        }

        if (!isValidBookToLend()) {
            System.out.println("The chosen book is not available");
            continuePromptingData("lend");
        }
    }

    private String getLendingStartDate() throws ExitException {
        System.out.println("Lending date starts today? (y/N)");
        String answer = ScannerCreator.nextLineWithExitCheck();
        if (answer.equalsIgnoreCase("n") || answer.isEmpty()) {
            System.out.print("Lending date: ");
            return ScannerCreator.nextLineWithExitCheck();
        }
        return String.valueOf(LocalDate.now());
    }

    private String getLendingEndDate() throws ExitException {
        System.out.println("Is there a given ending date? (Y/n)");
        String answer = ScannerCreator.nextLineWithExitCheck();
        if (answer.equalsIgnoreCase("y") || answer.isEmpty()) {
            System.out.print("Ending date: ");
            return ScannerCreator.nextLineWithExitCheck();
        }
        return null;
    }

    private void validateLendDates() throws ExitException {
        if (!validationUtil.isValidLend(this.lendStartDate, this.lendEndDate)) {
            continuePromptingData("lend");
        }
    }

    private boolean isValidUser() {
        return selectUser(false) != null;
    }

    private boolean isValidBookToLend() {
        Book book = selectBook(false);
        return book != null && !isBookAlreadyLent(book);
    }
    // singleResult, singleResultOrNull, MaxResults + uniqueResults?
    private User selectUser(boolean hasUserInput) {
        if (!hasUserInput) {
            System.out.println("User code: ");
            String userCode = ScannerCreator.nextLine();
        }

        String query =
                "FROM User u " +
                "WHERE u.codigo = :userCode";

        try (Session session = HibernateUtil.openSession()) {
            this.user =
                    session.createQuery(query, User.class)
                    .setParameter("userCode", userCode)
                    .setReadOnly(true)
                    .setMaxResults(1)
                    .uniqueResult();
            return this.user;
        }
    }
    // Checks if a book has been lent
    private boolean isBookAlreadyLent(Book book) {
        String query =
                "FROM Lend l " +
                "WHERE l.libro = :chosenBook AND l.fechadevolucion < :currentDate";

        try (Session session = HibernateUtil.openSession()) {
            this.lend =
                     session.createQuery(query, Lend.class)
                     .setParameter("chosenBook", book.getIsbn())
                     .setParameter("currentDate", LocalDate.now())
                     .setReadOnly(true)
                     .setMaxResults(1)
                     .uniqueResult();
            return this.lend != null;
        }
    }

    private Book selectBook(boolean hasUserInput) {
        if (!hasUserInput) {
            System.out.println("Book ISBN to lend: ");
            String bookISBN = ScannerCreator.nextLine();
        }

        String query =
                "FROM Book b " +
                "WHERE b.isbn = :bookISBN";

        try (Session session = HibernateUtil.openSession()) {
            this.book =
                    session.createQuery(query, Book.class)
                    .setParameter("bookISBN", bookISBN)
                    .setReadOnly(true)
                    .setMaxResults(1)
                    .uniqueResult();
            return this.book;
        }
    }

    private Lend createLend() {
        Lend lend = new Lend();
        lend.setLibro(this.book);
        lend.setUsuario(this.user);
        lend.setFechaprestamo(LocalDate.parse(this.lendStartDate));
        lend.setFechadevolucion(this.lendEndDate != null ? LocalDate.parse(this.lendEndDate) : null);
        return lend;
    }

    // Given a date by the user, print the list
    @Override
    public void printLendByYear() throws ExitException {
        ScannerCreator.nextLine();
        System.out.println("Initializing lend by year operation...");
        String yearToQuery = promptUserForYear();
        List<Lend> lendsByYear = getLendsByYear(yearToQuery);

        if (lendsByYear.isEmpty()) System.out.println("No lends found for the year " + yearToQuery);
        else {
            lendsByYear.forEach(System.out::println);  // Print each lend record
            changeUserByLend(lendsByYear); // Redirect flow to change username given the previous list
        }
    }

    private String promptUserForYear() throws ExitException {
        System.out.print("Enter the year to query lends: ");
        String year;
        while (true) {
            year = askForInput("year");
            if (isValidYear(year)) break;  // If valid year, exit loop
            System.out.print("Invalid year. Please enter a valid year (1900-2030): ");
        }
        return year;
    }

    private boolean isValidYear(String year) {
        if (year.length() != 4) return false;  // Must be a 4-digit number
        try {
            int parsedYear = Integer.parseInt(year);
            return parsedYear >= 1924 && parsedYear <= 2030;  // Year must be between 1924 and 2030
        } catch (NumberFormatException e) {
            return false;  // If the year can't be parsed as an integer, return false
        }
    }

    private List<Lend> getLendsByYear(String year) {
        String query =
                "FROM Lend l " +
                "WHERE TO_CHAR(l.fechaprestamo, 'YYYY') = :year";

        try (Session session = HibernateUtil.openSession()) {
            return session.createQuery(query, Lend.class)
                    .setParameter("year", year)
                    .setReadOnly(false) // We might change user
                    .getResultList();
        }
    }
    // TODO: Consider not redirecting to the main menu straight away?
    @Override
    public void changeUserByLend(List<Lend> lendList) throws ExitException {
        boolean changeUser = askUserIfChangeUser();
        if (changeUser) {
            User newUser = selectUser(false);
            if (newUser == null) {
                System.out.println("The user does not exist. Operation canceled");
                System.out.println("Returning to the main menu...");
                throw new ExitException("User does not exist.");
            }
            updateLendUser(lendList, newUser);
        }
    }

    private boolean askUserIfChangeUser() throws ExitException {
        System.out.print("Do you want to change the user for these lends? (yes/no): ");
        String response = ScannerCreator.nextLineWithExitCheck().trim().toLowerCase();
        return response.equals("yes");
    }

    private void updateLendUser(List<Lend> lendList, User newUser) throws ExitException {
        try (Session updateLendByUserSession = HibernateUtil.openSession()) {
            Transaction transaction = updateLendByUserSession.beginTransaction();
            try {
                for (Lend lend : lendList) {
                    lend.setUsuario(newUser); // Change the user for all lends
                    updateLendByUserSession.persist(lend);
                }

                transaction.commit();
                System.out.println("User updated successfully for all lends.");
            } catch (IllegalStateException | PersistenceException e) {
                handleTransactionError(transaction, e, "user");
            }
        }
    }
    // List all the books that a certain user has in lend
    @Override
    public void printBookByUserLend() throws ExitException {
        ScannerCreator.nextLine();
        System.out.println("Initializing user's lend list operation...");

        String userToQuery = promptUserForCode();
        List<Lend> lendsByUser = getLendsByUser(userToQuery);

        if (lendsByUser.isEmpty()) System.out.println("No lends found for the user code: " + userToQuery);
        else lendsByUser.forEach(System.out::println);
    }

    private String promptUserForCode() throws ExitException {
        System.out.print("Enter the user code: ");
        String code;
        while (true) {
            code = ScannerCreator.nextLineWithExitCheck();
            if (validationUtil.isValidCode(code)) break;
            System.out.println("Invalid user code. Please enter a valid user code. Else, type 'EXIT'");
        }
        return code;
    }

    private List<Lend> getLendsByUser(String userToQuery) {
        String query =
                "SELECT * FROM Lend l " +
                "WHERE l.usuario = ?1";

        try (Session session = HibernateUtil.openSession()) {
            return session.createNativeQuery(query, Lend.class)
                    .addSynchronizedEntityClass(Lend.class) // Synchronizes that class with the current session
                    .setParameter(1, userToQuery)
                    .setReadOnly(true)
                    .getResultList();
        }
    }
    // Close the Session Factory. Sessions are closed inside each method
    // they are called
    public void closeHibernate() {
        HibernateUtil.closeSessionFactory();
    }
}