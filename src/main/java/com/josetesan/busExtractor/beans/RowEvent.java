package com.josetesan.busExtractor.beans;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class RowEvent {

    private Long id;
    private Date create_date;
    private String payload;
}
