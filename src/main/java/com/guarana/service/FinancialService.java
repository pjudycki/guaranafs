package com.guarana.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guarana.configuration.GuaranaConfigurationProperties;
import com.guarana.generator.ReportGenerator;
import com.guarana.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FinancialService {

    @Autowired
    private GuaranaConfigurationProperties properties;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ReportGenerator reportGenerator;

    private String API_HOST;
    private String API_KEY;

    @PostConstruct
    public void init() {
        API_KEY = properties.getKey();
        API_HOST = properties.getEndpoint();
    }

    public <T> T retrieve(String basePath, String base, String symbols, Class<T> clazz) {

        StringBuilder latestURL = new StringBuilder(basePath);

        if (base != null) {
            latestURL.append("&base=");
            latestURL.append(base);
        }

        if (symbols != null) {
            latestURL.append("&symbols=");
            latestURL.append(symbols);
        }

        ResponseEntity<T> response = restTemplate.getForEntity(latestURL.toString(), clazz);
        HttpStatus statusCode = response.getStatusCode();
        HttpHeaders httpHeaders = response.getHeaders();

        logHttpHeadersAndStatus(httpHeaders, statusCode);
        return response.getBody();
    }

    public CurrencyList retrieveHistorical(String base, String symbols, String date) {
        return retrieve(API_HOST + "/" + date + "?access_key=" + API_KEY, base, symbols, CurrencyList.class);
    }

    public CurrencyList retrieveLatest(String base, String symbols) {
        return retrieve(API_HOST + "/latest?access_key=" + API_KEY, base, symbols, CurrencyList.class);
    }

    public Double computeChange(String base, String symbol, String startDate, String endDate) throws JsonProcessingException {
        CurrencyList start = retrieveHistorical(base, symbol, startDate);
        CurrencyList end = retrieveHistorical(base, symbol, endDate);
        double diff;

        Map<String, Double> startMap = start.getRates();
        Map<String, Double> endMap = end.getRates();
        Double startCurrency = startMap.get(symbol);
        Double endCurrency = endMap.get(symbol);
        diff = endCurrency - startCurrency;

        return diff;
    }

    public Double computeConversion(String base, String symbol, String amount) {
        CurrencyList result = retrieveLatest(base, symbol);
        Map<String, Double> rates = result.getRates();
        Double rate = rates.get(symbol);
        return rate * Double.parseDouble(amount);
    }

    public ConvertItem convert(String from, String to, String amount) {
        StringBuilder builder = new StringBuilder(API_HOST + "/convert?access_key=" + API_KEY);
        builder.append("&from=");
        builder.append(from);
        builder.append("&to=");
        builder.append(to);
        builder.append("&amount=");
        builder.append(amount);

        return retrieve(builder.toString(), null, null, ConvertItem.class);
    }

    public TimeseriesList getTimeSeries(String startDate, String endDate) {
        StringBuilder builder = new StringBuilder(API_HOST + "/timeseries?access_key=" + API_KEY);
        builder.append("&start_date=");
        builder.append(startDate);
        builder.append("&end_date=");
        builder.append(endDate);

        return retrieve(builder.toString(), null, null, TimeseriesList.class);
    }

    public FluctuationList getFluctuation(String startDate, String endDate) {
        StringBuilder builder = new StringBuilder(API_HOST + "/fluctuation?access_key=" + API_KEY);
        builder.append("&start_date=");
        builder.append(startDate);
        builder.append("&end_date=");
        builder.append(endDate);

        return retrieve(builder.toString(), null, null, FluctuationList.class);
    }

    public SymbolList getSymbols() {
        StringBuilder builder = new StringBuilder(API_HOST + "/symbols?access_key=" + API_KEY);

        return retrieve(builder.toString(), null, null, SymbolList.class);
    }

    public String generateHistoricalReport(String fileName, String base, String date) throws IOException {
        CurrencyList historical = retrieveHistorical(base, null, date);
        return reportGenerator.generateHistoricalReport(historical, fileName, base, date);
    }

    public String generateLatestReport(String fileName, String base) throws IOException {
        CurrencyList latest = retrieveLatest(null, null);
        return reportGenerator.generateLatestReport(latest, fileName, base);
    }

    private void logHttpHeadersAndStatus(HttpHeaders httpHeaders, HttpStatus statusCode) {

        int statsCodeValue = statusCode.value();

        String responseHeaders = httpHeaders.entrySet().stream()
                .map(f -> f.getKey() + "=" + f.getValue())
                .collect(Collectors.joining());

        StringBuilder builder = new StringBuilder();
        builder.append("Response HTTP Status Code: ").append(statsCodeValue);
        builder.append("\n");
        builder.append("Response HTTP Headers list: ").append(responseHeaders);
        log.info(builder.toString());
    }
}

