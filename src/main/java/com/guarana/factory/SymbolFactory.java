package com.guarana.factory;

import com.guarana.entity.SymbolEntity;
import com.guarana.model.SymbolItem;

public class SymbolFactory {

    public static SymbolEntity createSymbolEntity(SymbolItem symbolItem) {
        SymbolEntity symbolEntity = new SymbolEntity();
        symbolEntity.setCurrencyCode(symbolItem.getCurrencyCode());
        symbolEntity.setCurrencyName(symbolItem.getCurrencyName());
        return symbolEntity;
    }

    public static SymbolItem createSymbolItem(SymbolEntity symbolEntity) {
        return SymbolItem.builder()
                .currencyCode(symbolEntity.getCurrencyCode())
                .currencyName(symbolEntity.getCurrencyName()).build();
    }
}
