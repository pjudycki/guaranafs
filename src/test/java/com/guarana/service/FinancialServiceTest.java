package com.guarana.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testRetrieveAll() throws JsonProcessingException {

        //given
        //when
        String jsonValues = financialService.retrieveLatestFromAPI(null, null);

        //then
        JsonNode nodeValues = mapper.readTree(jsonValues);
        Assertions.assertThat(nodeValues.size()).isEqualTo(5);
        Assertions.assertThat(nodeValues.findPath("rates").size()).isEqualTo(170);
        Assertions.assertThat(nodeValues.findPath("success").booleanValue()).isEqualTo(true);
        Assertions.assertThat(nodeValues.findPath("base").textValue()).isEqualTo("EUR");
        Assertions.assertThat(nodeValues.findPath("timestamp").intValue()).isPositive();
    }

    @Test
    public void testRetrieveWithBase() throws JsonProcessingException {
        //given
        //when
        String jsonValues = financialService.retrieveLatestFromAPI("EUR", null);

        //then
        JsonNode nodeValues = mapper.readTree(jsonValues);
        Assertions.assertThat(nodeValues.size()).isEqualTo(5);
        Assertions.assertThat(nodeValues.findPath("rates").size()).isEqualTo(170);
        Assertions.assertThat(nodeValues.findPath("success").booleanValue()).isEqualTo(true);
        Assertions.assertThat(nodeValues.findPath("base").textValue()).isEqualTo("EUR");
        Assertions.assertThat(nodeValues.findPath("timestamp").intValue()).isPositive();

    }

    @Test
    public void testRetrieveWithBaseAndList() throws JsonProcessingException {
        //given
        //when
        String jsonValues = financialService.retrieveLatestFromAPI("EUR", "USD, GBP, CHF");

        //then
        JsonNode nodeValues = mapper.readTree(jsonValues);
        Assertions.assertThat(nodeValues.size()).isEqualTo(5);
        Assertions.assertThat(nodeValues.findPath("rates").size()).isEqualTo(3);
        Assertions.assertThat(nodeValues.findPath("success").booleanValue()).isEqualTo(true);
        Assertions.assertThat(nodeValues.findPath("base").textValue()).isEqualTo("EUR");
        Assertions.assertThat(nodeValues.findPath("timestamp").intValue()).isPositive();

    }
}
