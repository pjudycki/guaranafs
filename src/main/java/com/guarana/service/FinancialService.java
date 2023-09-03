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

    public String retrieveAllForDate(String date) {
        RestTemplate restTemplate = new RestTemplate();
        String HISTORICAL_URL = API_HOST + "/" + date + "?access_key=" + API_KEY;
        ResponseEntity<String> response = restTemplate.getForEntity(HISTORICAL_URL, String.class);
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

    public String generateReportForDate(String fileName, String base, String date) throws IOException {
        String allForDate = retrieveAllForDate(date);
        String resultFileName = createFileName(fileName + "_" + base, date);
        Map<String, Double> currToRateOfExchange = convertToCurrency(allForDate, base);
        generateExcelReport(resultFileName, currToRateOfExchange, base);

        return resultFileName;
    }

    public String generateLatestReport(String fileName, String base) throws IOException {
        String allRates = retrieveAll();
        String resultFileName = createFileName(fileName + "_" + base, LocalDate.now().toString());
        Map<String, Double> currToRateOfExchange = convertToCurrency(allRates, base);
        generateExcelReport(resultFileName, currToRateOfExchange, base);
        return resultFileName;
    }

    public void generateExcelReport(String fileName,  Map<String, Double> currToRateOfExchange, String base) throws IOException {
        XSSFWorkbook guaranaFsWorkbook = new XSSFWorkbook();
        XSSFSheet currenciesSheet = guaranaFsWorkbook.createSheet(CURR_SHEET_NAME);

        int rCounter = 0;
        int cCounter = 0;

        FileOutputStream fos = new FileOutputStream(fileName);
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
    }

    private Map<String, Double> getRatesForBase(String allRates) throws JsonProcessingException {
        Map<String, Double> result = new TreeMap<>();
        JsonNode treeNode = mapper.readTree(allRates);
        Iterator<String> fields = treeNode.path("rates").fieldNames();

        while (fields.hasNext()) {
            String name = fields.next();
            result.put(name, treeNode.findPath(name).doubleValue());
        }

        return result;
    }

    private Map<String, Double> convertToCurrency(String allRates, String currency) throws JsonProcessingException {
        Map<String, Double> rates = getRatesForBase(allRates);
        Double rateOfExchange = rates.get(currency);

        for (Map.Entry<String, Double> entry : rates.entrySet()) {
            String name = entry.getKey();
            Double value = entry.getValue();
            Double converted = value / rateOfExchange;
            rates.put(name, converted);
        }

        return rates;
    }

    private String createFileName(String fileName, String date) {
        return fileName + "_" + date + ".xlsx";
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

    public Double differenceBetweenDates(String base, String symbol, String startDate, String endDate) {
        String start = retrieveWithBaseAndListAndDate(base, symbol, startDate);
        String end = retrieveWithBaseAndListAndDate(base, symbol, endDate);
        Double diff = null;

        try {
            Map<String, Double> startMap = getRatesForBase(start);
            Map<String, Double> endMap = getRatesForBase(end);
            Double startCurrency = startMap.get(symbol);
            Double endCurrency = endMap.get(symbol);
            diff = endCurrency - startCurrency;
            System.out.println("test");
        } catch (JsonProcessingException e) {
            log.error("Cannot process server response");
        }

        return diff;
    }

    public Double convert(String base, String symbol, String amount) throws JsonProcessingException {
        String result = retrieveWithBaseAndList(base, symbol);
        Map<String, Double> rates = getRatesForBase(result);
        Double rate = rates.get(symbol);
        return rate * Double.parseDouble(amount);
    }
}
