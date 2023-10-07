package com.guarana.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.guarana.model.*;
import com.guarana.service.FinancialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@Slf4j
public class BankApiController {

    @Autowired
    private FinancialService service;

    @GetMapping("/retrieveLatest")
    @ResponseBody
    public ResponseEntity<CurrencyList> retrieveLatest() {
        return ResponseEntity.ok().body(service.retrieveLatest(null, null));
    }

    @GetMapping("/retrieveHistorical")
    @ResponseBody
    public ResponseEntity<CurrencyList> retrieveHistorical(@RequestParam String date) {
        return ResponseEntity.ok().body(service.retrieveHistorical( null, null, date));
    }

    @GetMapping("/retrieveLatestWithBase")
    @ResponseBody
    public ResponseEntity<CurrencyList> retrieveLatestWithBase(@RequestParam String base) {
        return ResponseEntity.ok().body(service.retrieveLatest(base, null));
    }

    @GetMapping("/retrieveLatestWithBaseAndSymbols")
    @ResponseBody
    public ResponseEntity<CurrencyList> retrieveLatestWithBaseAndSymbols(@RequestParam String base,
                                                                         @RequestParam String symbols) {
        return ResponseEntity.ok().body(service.retrieveLatest(base, symbols));
    }

    @GetMapping("/retrieveHistoricalWithBaseAndSymbolsAndDate")
    @ResponseBody
    public ResponseEntity<CurrencyList> retrieveHistoricalWithBaseAndSymbolsAndDate(@RequestParam String base,
                                                                                    @RequestParam String symbols,
                                                                                    @RequestParam String date) {
        return ResponseEntity.ok().body(service.retrieveHistorical(base, symbols, date));
    }

    @GetMapping("/computeChange")
    @ResponseBody
    public ResponseEntity<Double> computeChange(@RequestParam String base,
                                                @RequestParam String symbol,
                                                @RequestParam String startDate,
                                                @RequestParam String endDate) {
        try {
            return ResponseEntity.ok().body(service.computeChange(base, symbol, startDate, endDate));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/computeConversion")
    @ResponseBody
    public ResponseEntity<Double> computeConversion(@RequestParam String base,
                                                    @RequestParam String symbol,
                                                    @RequestParam String amount) {
        return ResponseEntity.ok().body(service.computeConversion(base, symbol, amount));
    }

    @GetMapping("/convert")
    @ResponseBody
    public ResponseEntity<ConvertItem> convert(@RequestParam String from,
                                               @RequestParam String to,
                                               @RequestParam String amount) {
        return ResponseEntity.ok().body(service.convert(from, to, amount));
    }

    @GetMapping("/timeseries")
    @ResponseBody
    public ResponseEntity<TimeseriesList> timeseries(@RequestParam String startDate,
                                                     @RequestParam String endDate) {
        return ResponseEntity.ok().body(service.getTimeSeries(startDate, endDate));
    }

    @GetMapping("/fluctuation")
    @ResponseBody
    public ResponseEntity<FluctuationList> fluctuation(@RequestParam String startDate,
                                                       @RequestParam String endDate) {
        return ResponseEntity.ok().body(service.getFluctuation(startDate, endDate));
    }

    @GetMapping("/symbols")
    @ResponseBody
    public ResponseEntity<SymbolList> symbols() {
        return ResponseEntity.ok().body(service.getSymbols());
    }

    @GetMapping("/generateLatestReport")
    @ResponseBody
    public ResponseEntity<InputStreamResource> generateLatestReport(@RequestParam String base) throws FileNotFoundException {

        String fileName;
        try {
            fileName = service.generateLatestReport("currencies", base);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
        InputStreamResource resource = new InputStreamResource(new FileInputStream(fileName));
        return ResponseEntity.ok().body(resource);
    }

    @GetMapping("/generateHistoricalReport")
    @ResponseBody
    public ResponseEntity<InputStreamResource> generateHistoricalReport(@RequestParam String base,
                                                   @RequestParam String date) throws FileNotFoundException {
        String fileName;
        try {
            fileName = service.generateHistoricalReport("currencies", base, date);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
        InputStreamResource resource = new InputStreamResource(new FileInputStream(fileName));
        return ResponseEntity.ok().body(resource);
    }
}