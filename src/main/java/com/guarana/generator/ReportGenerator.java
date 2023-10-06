package com.guarana.generator;

import com.guarana.model.CurrencyList;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Map;

@Service
@Slf4j
public class ReportGenerator {

    private static final String CURR_SHEET_NAME = "Currencies Rates Report";
    private static final String TITLE = "Rates of Exchange by exchangeratesapi.io";
    private static final String DATE = "Date";

    public String generateHistoricalReport(CurrencyList historical, String fileName, String base, String date) throws IOException {
        String resultFileName = createFileName(fileName + "_" + base, date);
        Map<String, Double> currToRateOfExchange = convertToCurrency(historical, base);
        generateExcelReport(resultFileName, currToRateOfExchange, base);

        return resultFileName;
    }

    public String generateLatestReport(CurrencyList latest, String fileName, String base) throws IOException {
        String resultFileName = createFileName(fileName + "_" + base, LocalDate.now().toString());
        Map<String, Double> currToRateOfExchange = convertToCurrency(latest, base);
        generateExcelReport(resultFileName, currToRateOfExchange, base);
        return resultFileName;
    }

    public void generateExcelReport(String fileName, Map<String, Double> currToRateOfExchange, String base) throws IOException {
        XSSFWorkbook guaranaFsWorkbook = new XSSFWorkbook();
        XSSFSheet currenciesSheet = guaranaFsWorkbook.createSheet(CURR_SHEET_NAME);

        int rCounter = 0;
        int cCounter = 0;

        FileOutputStream fos = new FileOutputStream(fileName);
        Row firstRow = currenciesSheet.createRow(rCounter++);

        Cell firstCell = firstRow.createCell(cCounter);
        firstCell.setCellValue(TITLE);

        Row secondRow = currenciesSheet.createRow(rCounter++);

        LocalDateTime snapshotDate = LocalDateTime.now();
        Cell secondCell = secondRow.createCell(cCounter++);
        secondCell.setCellValue(DATE);

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

    private void addValuesToRow(String name, Double value, Row row, int cCounter, String base) {
        Cell baseCell = row.createCell(cCounter++);
        baseCell.setCellValue(base);
        Cell nameCells = row.createCell(cCounter++);
        nameCells.setCellValue(name);
        Cell valueCells = row.createCell(cCounter++);
        valueCells.setCellValue(value);
    }

    private Map<String, Double> convertToCurrency(CurrencyList allRates, String currency) {
        Map<String, Double> rates = allRates.getRates();
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
        StringBuilder result = new StringBuilder(fileName);
        result.append("_");
        result.append(date);
        result.append(".xlsx");
        return result.toString();
    }
}
