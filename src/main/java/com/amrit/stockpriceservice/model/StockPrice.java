package com.amrit.stockpriceservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockPrice {
    private String symbol;
    private double price;
    private String timestamp;
}
