package com.guarana.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SymbolItem {

    private String currencyCode;
    private String currencyName;

}
