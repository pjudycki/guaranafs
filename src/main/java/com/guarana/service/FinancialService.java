package com.guarana.service;

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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FinancialService {

    @Autowired
    private GuaranaConfigurationProperties properties;

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String CURR_SHEET_NAME = "Currencies Rates Report";

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

    public void generateExcelReport(String fileName, String base) throws IOException {
        XSSFWorkbook guaranaFsWorkbook = new XSSFWorkbook();
        XSSFSheet currenciesSheet = guaranaFsWorkbook.createSheet(CURR_SHEET_NAME);
        String allValues = retrieveAll();

        JsonNode treeNode = mapper.readTree(allValues);
        Iterator<String> fields = treeNode.path("rates").fieldNames();

        int rCounter = 0;
        int cCounter = 0;

        FileOutputStream fos = new FileOutputStream(createFileName(fileName));
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
        try {
            while (fields.hasNext()) {
                String name = fields.next();
                Double value = treeNode.findPath(name).doubleValue();
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

    private String createFileName(String fileName) {
        return fileName + LocalDate.now() + ".xlsx";
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
