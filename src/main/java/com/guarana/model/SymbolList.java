package com.guarana.model;

import lombok.Data;

import java.util.Map;

@Data
public class SymbolList {

    private boolean success;
    private Map<String, String> symbols;

}
