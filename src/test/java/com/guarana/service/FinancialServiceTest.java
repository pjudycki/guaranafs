package com.guarana.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guarana.model.CurrencyList;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class FinancialServiceTest {

    @Autowired
    private FinancialService financialService;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void testRetrieveAll() throws JsonProcessingException {

        //given
        //when
        String jsonValues = financialService.retrieveLatestFromAPI(null, null);
        CurrencyList currencyList = mapper.readValue(jsonValues, CurrencyList.class);

        //then
        Assertions.assertThat(currencyList.getRates().size()).isEqualTo(170);
        Assertions.assertThat(currencyList.isSuccess()).isEqualTo(true);
        Assertions.assertThat(currencyList.getBase()).isEqualTo("EUR");
        Assertions.assertThat(currencyList.getTimestamp()).isPositive();
    }

    @Test
    public void testRetrieveWithBase() throws JsonProcessingException {
        //given
        //when
        String jsonValues = financialService.retrieveLatestFromAPI("EUR", null);
        CurrencyList currencyList = mapper.readValue(jsonValues, CurrencyList.class);

        //then
        Assertions.assertThat(currencyList.getRates().size()).isEqualTo(170);
        Assertions.assertThat(currencyList.isSuccess()).isEqualTo(true);
        Assertions.assertThat(currencyList.getBase()).isEqualTo("EUR");
        Assertions.assertThat(currencyList.getTimestamp()).isPositive();

    }

    @Test
    public void testRetrieveWithBaseAndList() throws JsonProcessingException {
        //given
        //when
        String jsonValues = financialService.retrieveLatestFromAPI("EUR", "USD, GBP, CHF");
        CurrencyList currencyList = mapper.readValue(jsonValues, CurrencyList.class);

        //then
        Assertions.assertThat(currencyList.getRates().size()).isEqualTo(3);
        Assertions.assertThat(currencyList.isSuccess()).isEqualTo(true);
        Assertions.assertThat(currencyList.getBase()).isEqualTo("EUR");
        Assertions.assertThat(currencyList.getTimestamp()).isPositive();

    }
}
