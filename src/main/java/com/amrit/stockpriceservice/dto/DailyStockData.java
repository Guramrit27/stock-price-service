package com.amrit.stockpriceservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DailyStockData {
    @JsonProperty("1. open")
    private String open;
    
    @JsonProperty("2. high")
    private String high;
    
    @JsonProperty("3. low")
    private String low;
    
    @JsonProperty("4. close")
    private String close;
    
    @JsonProperty("5. volume")
    private String volume;
}
