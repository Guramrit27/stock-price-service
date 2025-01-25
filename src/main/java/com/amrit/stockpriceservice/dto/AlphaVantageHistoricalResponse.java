package com.amrit.stockpriceservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.Map;
import java.time.LocalDate;

@Data
public class AlphaVantageHistoricalResponse {
    @JsonProperty("Meta Data")
    private Map<String, String> metaData;
    
    @JsonProperty("Time Series (Daily)")
    private Map<LocalDate, DailyStockData> timeSeriesDaily;
}
