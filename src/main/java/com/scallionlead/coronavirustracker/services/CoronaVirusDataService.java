package com.scallionlead.coronavirustracker.services;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.scallionlead.coronavirustracker.models.LocationStats;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.Getter;

/**
 * CoronaVirusDataService
 */
@Service
public class CoronaVirusDataService {

    // private static String VIRUS_DATA_URL =
    // "https://raw.githubusercontent.com/siucng2222-c/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Confirmed.csv";
    private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/siucng2222-c/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    @Getter
    private List<LocationStats> cachedStats = new ArrayList<>();

    @Getter
    private List<String> headers = new ArrayList<>();

    @Getter
    private Map<String, List<Integer>> countryStat = new HashMap<>();

    // Execute this method after Spring app started and service bean
    // constructed
    @PostConstruct
    // Scheduled re-fetch every 20th clock min (00, 20, 40) (sec min hr day wk mth)
    // https://crontab.guru/
    // REMEMBER to EnableScheduling in main class
    @Scheduled(cron = "0 */20 * * * *")
    public void fetchVirusData() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(VIRUS_DATA_URL)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String responseStr = response.body();
        // System.out.println(responseStr);
        StringReader csvStringReader = new StringReader(responseStr);

        List<LocationStats> newStats = new ArrayList<>();

        CSVParser parsedCSV = new CSVParser(csvStringReader, CSVFormat.DEFAULT.withFirstRecordAsHeader());

        // Dates headers start from the 5th column
        this.headers = parsedCSV.getHeaderNames();
        this.headers = this.headers.subList(4, this.headers.size());

        // Iterable<CSVRecord> records =
        // CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvStringReader);

        Iterable<CSVRecord> records = parsedCSV.getRecords();
        for (CSVRecord record : records) {
            // String provinceState = record.get("Province/State");
            // String countryRegion = record.get("Country/Region");
            // String lat = record.get("Lat");
            // System.out.printf(Locale.getDefault(), "[%s,%s,%s] \n", provinceState,
            // countryRegion, lat);
            LocationStats stats = new LocationStats();
            stats.setProvinceState(record.get(LocationStats.PROVINCE_STATE_FIELD));
            stats.setCountryRegion(record.get(LocationStats.COUNTRY_REGION_FIELD));

            try {
                // Get the latest stat from last column
                int latestCases = Integer.parseInt(record.get(record.size() - 1));
                stats.setLatestTotalCases(latestCases);

                int prevDayCases = Integer.parseInt(record.get(record.size() - 2));
                stats.setDiffFromPrevDay(latestCases - prevDayCases);

                // Extract all cases numbers under date columns
                List<Integer> allData = new ArrayList<>();
                for (int i = 4; i < record.size(); i++) {
                    allData.add(Integer.parseInt(record.get(i)));
                }
                stats.setAllData(allData);

                // Consolidate "per country" data
                if (this.countryStat.containsKey(stats.getCountryRegion())) {
                    List<Integer> countryData = this.countryStat.get(stats.getCountryRegion());
                    for (int i = 0; i < countryData.size(); i++) {
                        Integer newData = allData.get(i) + countryData.get(i);
                        countryData.set(i, newData);
                    }

                    countryStat.put(stats.getCountryRegion(), countryData);
                } else {
                    this.countryStat.put(stats.getCountryRegion(), allData);
                }

            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
            // System.out.println(stats);

            newStats.add(stats);
        }

        parsedCSV.close();

        this.cachedStats = newStats;
    }
}