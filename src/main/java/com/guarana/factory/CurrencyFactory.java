package com.guarana.factory;

import com.guarana.entity.CurrencyEntity;
import com.guarana.entity.RetrievalEntity;
import com.guarana.model.CurrencyList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CurrencyFactory {

    public static List<CurrencyEntity> toCurrencyEntity(CurrencyList currencyList) {
        Map<String, Double> rates = currencyList.getRates();
        List<CurrencyEntity> result = new ArrayList<>();

        RetrievalEntity retrievalEntity = new RetrievalEntity();
        retrievalEntity.setBase(currencyList.getBase());
        retrievalEntity.setHistorical(currencyList.isHistorical());
        retrievalEntity.setSuccess(currencyList.isSuccess());
        retrievalEntity.setDate(currencyList.getDate());
        retrievalEntity.setTimestamp(currencyList.getTimestamp());

        for(Map.Entry<String, Double> entry : rates.entrySet()) {
            String name = entry.getKey();
            Double value = entry.getValue();

            CurrencyEntity currency = new CurrencyEntity();
            currency.setCurrencyName(name);
            currency.setRateOfExchange(value);
            currency.setRetrievalId(retrievalEntity);
            result.add(currency);
        }

        return result;
    }

}
