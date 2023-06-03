package com.guarana.service;

import com.guarana.configuration.GuaranaConfigurationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FinancialService {

    @Autowired
    private GuaranaConfigurationProperties properties;

    private String URL;

    @PostConstruct
    public void init() {
        String API_KEY = properties.getKey();
        String API_HOST = properties.getEndpoint();
        URL = API_HOST + "/latest?access_key=" + API_KEY;
    }

    public String retrieveAll() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(URL, String.class);
        HttpStatus statusCode = response.getStatusCode();
        HttpHeaders httpHeaders = response.getHeaders();

        logHttpHeadersAndStatus(httpHeaders, statusCode);

        return response.getBody();
    }

    public String retrieveWithBase(String base) {
        RestTemplate restTemplate = new RestTemplate();
        String baseURL = URL + "&base=" + base;
        ResponseEntity<String> response = restTemplate.getForEntity(baseURL, String.class);
        HttpStatus statusCode = response.getStatusCode();
        HttpHeaders httpHeaders = response.getHeaders();

        logHttpHeadersAndStatus(httpHeaders, statusCode);

        return response.getBody();
    }

    public String retrieveWithBaseAndList(String base, String listOfCurrencies) {
        RestTemplate restTemplate = new RestTemplate();
        String baseURL = URL + "&base=" + base + "&symbols=" + listOfCurrencies;
        ResponseEntity<String> response = restTemplate.getForEntity(baseURL, String.class);
        HttpStatus statusCode = response.getStatusCode();
        HttpHeaders httpHeaders = response.getHeaders();

        logHttpHeadersAndStatus(httpHeaders, statusCode);

        return response.getBody();
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
