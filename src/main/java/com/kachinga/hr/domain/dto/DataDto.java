package com.kachinga.hr.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataDto<T> implements Serializable {
    private List<T> data;
    private long totalElements;
}
