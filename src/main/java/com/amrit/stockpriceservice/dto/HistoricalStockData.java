package com.amrit.stockpriceservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDate;
import java.util.Map;

@Data
public class HistoricalStockData {
    private String symbol;
    private Map<LocalDate, DailyStockData> timeSeriesDaily;
}
