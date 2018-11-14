package com.josetesan.busExtractor.beans;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class RowEvent {

    private Long sec;
    private Date eventDate;
    private String origen;
    private String eventType;
    private String campoModif;
    private String pks;
    private String vaVn;
    private String payload;
    private Integer processed;
}

