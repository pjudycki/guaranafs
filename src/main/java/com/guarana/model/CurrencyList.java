package com.guarana.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
public class CurrencyList {

    private boolean success;
    private boolean historical;
    private Long timestamp;
    private String base;
    private LocalDate date;
    private Map<String, Double> rates;

}
