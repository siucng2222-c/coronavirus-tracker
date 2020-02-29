package com.scallionlead.coronavirustracker.controllers;

import java.util.List;

import com.scallionlead.coronavirustracker.models.LocationStats;
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

        List<LocationStats> locationStats = coronaVirusDataService.getCachedStats();

        // Iterate each locationStat object in the list, get latestTotalCases attribute
        // and sum them up
        int totalCases = locationStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();

        model.addAttribute("testName", "Hello World!");
        model.addAttribute("locationStats", locationStats);
        model.addAttribute("totalCases", totalCases);
        return "home";
    }

}