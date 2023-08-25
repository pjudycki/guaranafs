package com.guarana.controller;

import com.guarana.service.FinancialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@Slf4j
public class BankApiController {

    @Autowired
    private FinancialService service;

    @GetMapping("/retrieveAll")
    @ResponseBody
    public String retrieveAll() {
        return service.retrieveAll();
    }

    @GetMapping("/retrieveWithBase")
    @ResponseBody
    public String retrieveWithBase(@RequestParam String base) {
        return service.retrieveWithBase(base);
    }

    @GetMapping("/retrieveWithBaseAndList")
    @ResponseBody
    public String retrieveWithBaseAndCurrenciesList(@RequestParam String base, @RequestParam String symbols) {
        return service.retrieveWithBaseAndList(base, symbols);
    }

    @GetMapping("/generateExcelReport")
    @ResponseBody
    public ResponseEntity generateExcelReport(@RequestParam String base) throws FileNotFoundException {
        String fileName;
        try {
            fileName = service.generateExcelReport("currencies", base);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
        InputStreamResource resource = new InputStreamResource(new FileInputStream(fileName));
        return ResponseEntity.ok().body(resource);
    }


}
