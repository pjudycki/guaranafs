package com.guarana.controller;

import com.guarana.service.FinancialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
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


}
