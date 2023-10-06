package com.guarana.model;

import lombok.Data;

@Data
public class ExchangeItem {

    private String from;
    private String to;
    private Double amount;
}
