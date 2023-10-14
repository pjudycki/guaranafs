package com.guarana.controller;

import com.guarana.service.FinancialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class HomeController {

    @Autowired
    private FinancialService financialService;

    @GetMapping("/")
    public ModelAndView index() {
        return new ModelAndView("index");
    }

    @GetMapping("/viewSymbols")
    public ModelAndView viewBooks(Model model) {
        model.addAttribute("symbolList", financialService.getSymbols());
        return new ModelAndView("view-symbols");
    }
}