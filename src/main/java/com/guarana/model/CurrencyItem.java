package com.guarana.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class CurrencyItem {
    String fromCurrency;
    String toCurrency;
    LocalDate date;
    Double rate;
}
