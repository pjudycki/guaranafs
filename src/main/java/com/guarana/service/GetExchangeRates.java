package com.guarana.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guarana.entity.CurrencyEntity;
import com.guarana.entity.RetrievalEntity;
import com.guarana.factory.CurrencyFactory;
import com.guarana.factory.RetrievalFactory;
import com.guarana.model.CurrencyList;
import com.guarana.repository.CurrencyRepository;
import com.guarana.repository.RetrievalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetExchangeRates {

    @Autowired
    private FinancialService financialService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private RetrievalRepository retrievalRepository;

    @Scheduled(fixedDelay = 60000)
    public void  processAndSave() throws JsonProcessingException {
        processAndSaveForCurrency("EUR");
        processAndSaveForCurrency("USD");
        processAndSaveForCurrency("GBP");
        processAndSaveForCurrency("CHF");
    }

    private void processAndSaveForCurrency(String currency) throws JsonProcessingException {
        String result = financialService.retrieveLatestFromAPI(currency, null);
        CurrencyList currencyList = objectMapper.readValue(result, CurrencyList.class);

        RetrievalEntity retrievalEntity = retrievalRepository.save(RetrievalFactory.toRetrievalEntity(currencyList));
        List<CurrencyEntity> currencyEntities = CurrencyFactory.toCurrencyEntity(currencyList, retrievalEntity);

        currencyRepository.saveAll(currencyEntities);
    }
}
