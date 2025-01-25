package com.amrit.stockpriceservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AlphaVantageResponse {
    @JsonProperty("Global Quote")
    private GlobalQuoteDTO globalQuote;
}
