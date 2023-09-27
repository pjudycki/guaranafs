package com.guarana.factory;

import com.guarana.entity.RetrievalEntity;
import com.guarana.model.CurrencyList;

public class RetrievalFactory {

    public static RetrievalEntity toRetrievalEntity(CurrencyList currencyList) {
        RetrievalEntity retrievalEntity = new RetrievalEntity();
        retrievalEntity.setBase(currencyList.getBase());
        retrievalEntity.setHistorical(currencyList.isHistorical());
        retrievalEntity.setSuccess(currencyList.isSuccess());
        retrievalEntity.setDate(currencyList.getDate());
        retrievalEntity.setTimestamp(currencyList.getTimestamp());
        return retrievalEntity;
    }
}
