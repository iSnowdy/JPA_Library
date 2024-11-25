package org.example;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Date {
    private String date;
    private int year;
    private int month;
    private int day;
    // The Regex of Dates :) https://howtodoinjava.com/java/date-time/localdate-format-example/
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Date(String date) {
        this.date = date;
    }

    public void dateValidation() throws ExitException {
        if (!validateStaticDate(this.date)) {
            throw new ExitException("The provided date format is incorrect. Must follow: (YYYY-MM-DD)");
        }
        separateDate();
    }

    private void separateDate() {
        LocalDate parsedDate = LocalDate.parse(this.date, FORMATTER);
        this.year = parsedDate.getYear();
        this.month = parsedDate.getMonthValue();
        this.day = parsedDate.getDayOfMonth();
    }

    public static boolean validateStaticDate(String date) {
        try {
            LocalDate.parse(date, FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return this.date;
    }

    // Getters
    public String getDate() {
        return date;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }
}
