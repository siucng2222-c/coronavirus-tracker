package com.scallionlead.coronavirustracker.services;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import com.scallionlead.coronavirustracker.models.LocationStats;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.Getter;

/**
 * CoronaVirusDataService
 */
@Service
public class CoronaVirusDataService {

    private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/siucng2222-c/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Confirmed.csv";

    @Getter
    private List<LocationStats> cachedStats = new ArrayList<>();

    // Execute this method after Spring app started and service bean
    // constructed
    @PostConstruct
    // Apply a scheduled re-fetch every 1 clock min (sec min hr day wk mth)
    // REMEMBER to EnableScheduling in main class
    @Scheduled(cron = "0 */1 * * * *")
    public void fetchVirusData() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(VIRUS_DATA_URL)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String responseStr = response.body();
        // System.out.println(responseStr);
        StringReader csvStringReader = new StringReader(responseStr);

        List<LocationStats> newStats = new ArrayList<>();
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvStringReader);
        for (CSVRecord record : records) {
            // String provinceState = record.get("Province/State");
            // String countryRegion = record.get("Country/Region");
            // String lat = record.get("Lat");
            // System.out.printf(Locale.getDefault(), "[%s,%s,%s] \n", provinceState,
            // countryRegion, lat);

            LocationStats stats = new LocationStats();
            stats.setProvinceState(record.get(LocationStats.PROVINCE_STATE_FIELD));
            stats.setCountryRegion(record.get(LocationStats.COUNTRY_REGION_FIELD));

            // Get the latest stat from last column
            stats.setLatestTotalCases(Integer.parseInt(record.get(record.size() - 1)));
            System.out.println(stats);
            newStats.add(stats);
        }

        this.cachedStats = newStats;
    }
}