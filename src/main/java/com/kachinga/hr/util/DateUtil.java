package com.kachinga.hr.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public final class DateUtil {

    /**
     * Parses a string representation of a date into a LocalDate object.
     *
     * @param formDate the string representation of the date
     * @return the LocalDate object parsed from the input string
     * @throws DateTimeParseException if the input string cannot be parsed
     */
    public static LocalDate stringToLocalDate(String formDate) {
        return LocalDate.parse(formDate);
    }

    /**
     * Converts a long value representing milliseconds since the epoch to a LocalDateTime object.
     *
     * @param valueToConvert the value representing milliseconds since the epoch
     * @return the LocalDateTime object representing the converted date and time
     */
    public static LocalDateTime getLocalDate(long valueToConvert) {
        Date currentDate = new Date(valueToConvert);
        return currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * Converts a LocalDate object to a Date object.
     *
     * @param dateToConvert the LocalDate object to be converted
     * @return the Date object representing the same date as the input LocalDate
     */
    public static Date convertLocalDateToDate(LocalDate dateToConvert) {
        return java.sql.Date.valueOf(dateToConvert);
    }

    /**
     * Converts a LocalDate object to a java.sql.Date object.
     *
     * @param dateToConvert the LocalDate object to be converted
     * @return the java.sql.Date object representing the same date as the input LocalDate
     */
    public static java.sql.Date convertLocalDateToSqlDate(LocalDate dateToConvert) {
        return java.sql.Date.valueOf(dateToConvert);
    }


    /**
     * Formats a given LocalDate into a readable string representation.
     *
     * @param date the LocalDate to be formatted
     * @return a string representing the formatted date in the format "MMM dd, yyyy"
     */
    public static String readableLocalDate(LocalDate date) {
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        return date.format(formatters);
    }

    /**
     * Converts a string representation of a date and time into a LocalDateTime object.
     *
     * @param string the string representation of the date and time
     * @return the LocalDateTime object parsed from the input string
     * @throws DateTimeParseException if the input string cannot be parsed
     */
    public static LocalDateTime stringToLocalDateTime(String string) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(string, formatter);
    }

    /**
     * Formats a given LocalDateTime into a readable string representation.
     *
     * @param dateTime the LocalDateTime to be formatted
     * @return a string representing the formatted date and time in the format "yyyy-MM-dd HH:mm:ss"
     */
    public static String readableLocalDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatters);
    }
    /**
     * Checks if the given string represents a valid LocalDate according to the format "yyyy-MM-dd".
     *
     * @param dateStr the string representing a date
     * @return true if the string represents a valid LocalDate, false otherwise
     */
    public static boolean validLocalDate(String dateStr) {
        DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            LocalDate.parse(dateStr, DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static String transactionTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

    public static String getTimeBasedOnDate(LocalDate givenDate) {
        LocalDate currentDate = LocalDate.now();
        if (givenDate.isBefore(currentDate)) {
            return "23:59:59";
        }
        if (givenDate.isEqual(currentDate)) {
            LocalTime currentTime = LocalTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            return currentTime.format(formatter);
        }
        return "23:59:59";
    }

    public static String transactionTime(LocalDate date) {
        String time = DateUtil.getTimeBasedOnDate(date);
        return (DateUtil.stringToLocalDateTime(date + " " + time)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static LocalDate stringToDate(String dateToConvert) {
        String dateString = dateToConvert != null ? dateToConvert : LocalDate.now().toString();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.now();
        try {
            date = LocalDate.parse(dateString, format);
        } catch (Exception e) {
            System.out.println("Dirty Date");
        }
        return date;
    }
}
