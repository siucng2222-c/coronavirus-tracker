package com.scallionlead.coronavirustracker.models;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * LocationStats
 */
// https://www.baeldung.com/intro-to-project-lombok
@Getter
@Setter
@NoArgsConstructor
@ToString
public class LocationStats {

    public static final String PROVINCE_STATE_FIELD = "Province/State";
    public static final String COUNTRY_REGION_FIELD = "Country/Region";

    private String provinceState;
    private String countryRegion;
    private int latestTotalCases;
    private int diffFromPrevDay;
    private List<Integer> allData;

}