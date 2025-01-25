package com.amrit.stockpriceservice.controller;

import com.amrit.stockpriceservice.model.StockPrice;
import com.amrit.stockpriceservice.dto.HistoricalStockData;
import com.amrit.stockpriceservice.service.StockPriceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/stocks")
public class StockPriceController {
    private final StockPriceService stockPriceService;

    public StockPriceController(StockPriceService stockPriceService) {
        this.stockPriceService = stockPriceService;
    }

    @GetMapping("/{symbol}/price")
    public Mono<StockPrice> getStockPrice(@PathVariable String symbol) {
        return stockPriceService.getStockPrice(symbol);
    }

    @GetMapping("/{symbol}/historical")
    public Mono<HistoricalStockData> getHistoricalData(@PathVariable String symbol) {
        return stockPriceService.getHistoricalData(symbol);
    }
}
