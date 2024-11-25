package org.example;

public class ValidationUtil {

    // Validate user information
    public boolean isValidUser(String code, String name, String surname, String dob) {
        return isValidCode(code) && isValidText(name, 25) && isValidText(surname, 25) && isValidDate(dob);
    }

    // Validate book information
    public boolean isValidBook(String isbn, String title, String publisher) {
        return isValidISBN(isbn) && isValidText(title, 90) && isValidText(publisher, 60);
    }

    // Validate lend information
    public boolean isValidLend(String lendDate, String returnDate) {
        if (!isValidDate(lendDate)) {
            throw new IllegalArgumentException("Invalid lend date format");
        }
        if (returnDate != null && !isValidDate(returnDate)) {
            throw new IllegalArgumentException("Invalid return date format");
        }
        return true;
    }

    // Validate alphanumeric code (user's) (ex: ABC123)
    // public because it is also used in another class for only checking code
    public boolean isValidCode(String code) {
        return code != null && code.matches("^[A-Z]{3}\\d{1,5}$");
    }

    // Validate text given a maxLength
    private boolean isValidText(String text, int maxLength) {
        return text != null && text.matches("^[a-zA-Z ]{1," + maxLength + "}$");
    }

    // Validate ISBN
    private boolean isValidISBN(String isbn) {
        return isbn != null && isbn.matches("^\\d{13}$");
    }

    // Validate date format (YYYY-MM-DD)
    public boolean isValidDate(String date) {
        return Date.validateStaticDate(date);
    }
}