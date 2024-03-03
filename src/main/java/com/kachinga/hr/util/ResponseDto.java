package com.kachinga.hr.util;

import lombok.*;

import java.io.Serializable;

/**
 * A generic class representing a response with a message and optional data.
 *
 * @param <T> the type of data contained in the response
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ResponseDto<T> implements Serializable {
    private String message;
    private T data;

    /**
     * Constructs a ResponseDto with the provided data.
     *
     * @param data the data to be included in the response
     */
    public ResponseDto(T data) {
        this.data = data;
    }
}
