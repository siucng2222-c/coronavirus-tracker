package com.scallionlead.coronavirustracker.controllers;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
        // Sort list by locationStat objects' LatestTotalCases attribute in desc order
        locationStats = locationStats.stream()
                .sorted(Comparator.comparingInt(LocationStats::getLatestTotalCases).reversed())
                .collect(Collectors.toList());
        int totalNewCases = locationStats.stream().mapToInt(stat -> stat.getDiffFromPrevDay()).sum();
        model.addAttribute("testName", "Hello World!");
        model.addAttribute("locationStats", locationStats);
        model.addAttribute("totalCases", totalCases);
        model.addAttribute("totalNewCases", totalNewCases);
        return "home";
    }

}