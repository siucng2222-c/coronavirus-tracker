package com.scallionlead.coronavirustracker.controllers;

import com.scallionlead.coronavirustracker.services.CoronaVirusDataService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * HomeController
 */
// This is a Controller for returning to a UI template in resources
// NOT a RestController, which must return JSON
@Controller

public class HomeController {

    @Autowired
    CoronaVirusDataService coronaVirusDataService;

    @GetMapping("/")
    public String home(Model model) {
        // Correspond to resources/template/home.html
        // which is a Thymeleaf template
        model.addAttribute("testName", "Hello World!");
        model.addAttribute("locationStats", coronaVirusDataService.getCachedStats());
        return "home";
    }

}