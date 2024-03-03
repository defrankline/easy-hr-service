package com.kachinga.hr.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * A generic class representing a response with a message and optional data.
 *
 */
public final class ApiResponse {
    /**
     * Constructs a ResponseEntity with the provided data, message, and HTTP status.
     *
     * @param data      the data to be included in the response
     * @param message   the message to be included in the response
     * @param httpStatus the HTTP status to be included in the response
     * @return a ResponseEntity containing the response data, message, and HTTP status
     */
    public static ResponseEntity<? extends ResponseDto<?>> respond(Object data, String message, HttpStatus httpStatus) {
        ResponseDto<?> response = new ResponseDto<>(message, data);
        return new ResponseEntity<>(response, httpStatus);
    }

    /**
     * Constructs a ResponseEntity with the provided data and default message ("Data").
     *
     * @param data the data to be included in the response
     * @return a ResponseEntity containing the response data and default message ("Data")
     */
    public static ResponseEntity<? extends ResponseDto<?>> respond(Object data) {
        ResponseDto<?> response = new ResponseDto<>("Data", data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Constructs a ResponseEntity with the provided data and message.
     *
     * @param data    the data to be included in the response
     * @param message the message to be included in the response
     * @return a ResponseEntity containing the response data and message
     */
    public static ResponseEntity<? extends ResponseDto<?>> respond(Object data, String message) {
        ResponseDto<?> response = new ResponseDto<>(message, data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Constructs a ResponseEntity with the provided data and default message ("Data"), and the given HTTP status.
     *
     * @param data      the data to be included in the response
     * @param httpStatus the HTTP status to be included in the response
     * @return a ResponseEntity containing the response data, default message ("Data"), and HTTP status
     */
    public static ResponseEntity<? extends ResponseDto<?>> respond(Object data, HttpStatus httpStatus) {
        ResponseDto<?> response = new ResponseDto<>("Data", data);
        return new ResponseEntity<>(response, httpStatus);
    }

    /**
     * Constructs a ResponseEntity with the provided message and HTTP status.
     *
     * @param message    the message to be included in the response
     * @param httpStatus the HTTP status to be included in the response
     * @return a ResponseEntity containing the provided message and HTTP status
     */
    public static ResponseEntity<? extends ResponseDto<?>> respond(String message, HttpStatus httpStatus) {
        ResponseDto<?> response = new ResponseDto<>(message);
        return new ResponseEntity<>(response, httpStatus);
    }

    /**
     * Constructs a ResponseEntity with the provided data, total elements count, and HTTP status.
     *
     * @param data         the data to be included in the response
     * @param totalElements the total count of elements
     * @param httpStatus   the HTTP status to be included in the response
     * @return a ResponseEntity containing the response data, total elements count, and HTTP status
     */
    public static ResponseEntity<? extends ResponseDto<?>> respond(Object data, Long totalElements, HttpStatus httpStatus) {
        Map<String, Object> res = new HashMap<>();
        res.put("data", data);
        res.put("totalElements", totalElements);
        ResponseDto<?> response = new ResponseDto<>(res);
        return new ResponseEntity<>(response, httpStatus);
    }

    /**
     * Logs a message along with additional information such as company ID, item, and timestamp to the standard output.
     *
     * @param companyName the name of the company associated with the log message
     * @param item      the item related to the log message
     * @param message   the log message to be printed
     */
    public static void logger(String companyName, String item, String message) {
        System.out.println(LocalDateTime.now() + " - " + companyName + " - " + item + " -> " + message);
    }
}
