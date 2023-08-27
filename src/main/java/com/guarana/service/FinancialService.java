package com.guarana.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guarana.configuration.GuaranaConfigurationProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FinancialService {

    @Autowired
    private GuaranaConfigurationProperties properties;

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String CURR_SHEET_NAME = "Currencies Rates Report";

    private String API_HOST;
    private String API_KEY;

    @PostConstruct
    public void init() {
        API_KEY = properties.getKey();
        API_HOST = properties.getEndpoint();

    }

    public String retrieveAll() {
        RestTemplate restTemplate = new RestTemplate();
        String LATEST_URL = API_HOST + "/latest?access_key=" + API_KEY;
        ResponseEntity<String> response = restTemplate.getForEntity(LATEST_URL, String.class);
        HttpStatus statusCode = response.getStatusCode();
        HttpHeaders httpHeaders = response.getHeaders();

        logHttpHeadersAndStatus(httpHeaders, statusCode);

        return response.getBody();
    }

    public String retrieveWithBase(String base) {
        RestTemplate restTemplate = new RestTemplate();
        String LATEST_URL = API_HOST + "/latest?access_key=" + API_KEY;
        String baseURL = LATEST_URL + "&base=" + base;
        ResponseEntity<String> response = restTemplate.getForEntity(baseURL, String.class);
        HttpStatus statusCode = response.getStatusCode();
        HttpHeaders httpHeaders = response.getHeaders();

        logHttpHeadersAndStatus(httpHeaders, statusCode);

        return response.getBody();
    }

    public String retrieveWithBaseAndList(String base, String listOfCurrencies) {
        RestTemplate restTemplate = new RestTemplate();
        String LATEST_URL = API_HOST + "/latest?access_key=" + API_KEY;
        String baseURL = LATEST_URL + "&base=" + base + "&symbols=" + listOfCurrencies;
        ResponseEntity<String> response = restTemplate.getForEntity(baseURL, String.class);
        HttpStatus statusCode = response.getStatusCode();
        HttpHeaders httpHeaders = response.getHeaders();

        logHttpHeadersAndStatus(httpHeaders, statusCode);

        return response.getBody();
    }

    public String retrieveWithBaseAndListAndDate(String base, String listOfCurrencies, String date) {
        RestTemplate restTemplate = new RestTemplate();
        String HISTORICAL_URL = API_HOST + "/" + date + "?access_key=" + API_KEY;
        String baseURL = HISTORICAL_URL + "&base=" + base + "&symbols=" + listOfCurrencies;
        ResponseEntity<String> response = restTemplate.getForEntity(baseURL, String.class);
        HttpStatus statusCode = response.getStatusCode();
        HttpHeaders httpHeaders = response.getHeaders();

        logHttpHeadersAndStatus(httpHeaders, statusCode);

        return response.getBody();
    }

    public String generateExcelReport(String fileName, String base) throws IOException {
        XSSFWorkbook guaranaFsWorkbook = new XSSFWorkbook();
        XSSFSheet currenciesSheet = guaranaFsWorkbook.createSheet(CURR_SHEET_NAME);
        String allRates = retrieveAll();
        Map<String, Double> currToRateOfExchange = getRatesForBase(allRates, base);

        int rCounter = 0;
        int cCounter = 0;

        FileOutputStream fos = new FileOutputStream(createFileName(fileName + "_" + base));
        Row firstRow = currenciesSheet.createRow(rCounter++);

        Cell firstCell = firstRow.createCell(cCounter);
        firstCell.setCellValue("Rates of Exchange by exchangeratesapi.io");

        Row secondRow = currenciesSheet.createRow(rCounter++);

        LocalDateTime snapshotDate = LocalDateTime.now();
        Cell secondCell = secondRow.createCell(cCounter++);
        secondCell.setCellValue("Date");

        Cell thirdCell = secondRow.createCell(cCounter++);
        thirdCell.setCellValue(snapshotDate.toString());
        cCounter = 0;

        Iterator<String> fields = currToRateOfExchange.keySet().iterator();
        try {
            while (fields.hasNext()) {
                String name = fields.next();
                Double value = currToRateOfExchange.get(name);
                Row row = currenciesSheet.createRow(rCounter++);
                addValuesToRow(name, value, row, cCounter, base);
            }
            guaranaFsWorkbook.write(fos);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            fos.flush();
            fos.close();
        }
        return fileName;
    }

    private Map<String, Double> getRatesForBase(String allRates, String currency) throws JsonProcessingException {
        Map<String, Double> result = new TreeMap<>();
        JsonNode treeNode = mapper.readTree(allRates);
        Iterator<String> fields = treeNode.path("rates").fieldNames();

        while (fields.hasNext()) {
            String name = fields.next();
            result.put(name, treeNode.findPath(name).doubleValue());
        }

        convertToCurrency(result, currency);

        return result;
    }

    private void convertToCurrency(Map<String, Double> result, String currency) {

        Double rateOfExchange = result.get(currency);

        for (Map.Entry<String, Double> entry : result.entrySet()) {
            String name = entry.getKey();
            Double value = entry.getValue();
            Double converted = value / rateOfExchange;
            result.put(name, converted);
        }
    }

    private String createFileName(String fileName) {
        return fileName + "_" + LocalDate.now() + ".xlsx";
    }

    private void addValuesToRow(String name, Double value, Row row, int cCounter, String base) {
        Cell baseCell = row.createCell(cCounter++);
        baseCell.setCellValue(base);
        Cell nameCells = row.createCell(cCounter++);
        nameCells.setCellValue(name);
        Cell valueCells = row.createCell(cCounter++);
        valueCells.setCellValue(value);
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
