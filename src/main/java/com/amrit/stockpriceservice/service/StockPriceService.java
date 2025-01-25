package com.amrit.stockpriceservice.service;

import com.amrit.stockpriceservice.config.AlphaVantageConfig;
import com.amrit.stockpriceservice.dto.AlphaVantageResponse;
import com.amrit.stockpriceservice.dto.AlphaVantageHistoricalResponse;
import com.amrit.stockpriceservice.dto.GlobalQuoteDTO;
import com.amrit.stockpriceservice.dto.HistoricalStockData;
import com.amrit.stockpriceservice.model.StockPrice;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import java.net.URI;
import java.time.LocalDateTime;

@Service
public class StockPriceService {
    private final WebClient webClient;
    private final AlphaVantageConfig config;

    public StockPriceService(AlphaVantageConfig config) {
        this.config = config;
        this.webClient = WebClient.builder()
                .baseUrl(config.getApiUrl())
                .build();
    }

    public Mono<StockPrice> getStockPrice(String symbol) {
        return webClient.get()
                .uri(buildRequestUri(symbol, "GLOBAL_QUOTE"))
                .retrieve()
                .bodyToMono(AlphaVantageResponse.class)
                .map(response -> parseStockPrice(response, symbol));
    }

    public Mono<HistoricalStockData> getHistoricalData(String symbol) {
        return webClient.get()
                .uri(buildRequestUri(symbol, "TIME_SERIES_DAILY"))
                .retrieve()
                .bodyToMono(AlphaVantageHistoricalResponse.class)
                .map(response -> parseHistoricalData(response, symbol));
    }

    private URI buildRequestUri(String symbol, String function) {
        return UriComponentsBuilder.fromUriString(config.getApiUrl())
                .queryParam("function", function)
                .queryParam("symbol", symbol)
                .queryParam("apikey", config.getApiKey())
                .build()
                .toUri();
    }

    private StockPrice parseStockPrice(AlphaVantageResponse response, String symbol) {
        GlobalQuoteDTO globalQuote = response.getGlobalQuote();
        validateResponse(globalQuote, symbol);
        return createStockPrice(globalQuote, symbol);
    }

    private HistoricalStockData parseHistoricalData(AlphaVantageHistoricalResponse response, String symbol) {
        if (response.getTimeSeriesDaily() == null || response.getTimeSeriesDaily().isEmpty()) {
            throw new RuntimeException("Historical data not found for symbol: " + symbol);
        }
        
        HistoricalStockData historicalData = new HistoricalStockData();
        historicalData.setSymbol(symbol.toUpperCase());
        historicalData.setTimeSeriesDaily(response.getTimeSeriesDaily());
        return historicalData;
    }

    private void validateResponse(GlobalQuoteDTO globalQuote, String symbol) {
        if (globalQuote == null) {
            throw new RuntimeException("Stock price not found for symbol: " + symbol);
        }
    }

    private StockPrice createStockPrice(GlobalQuoteDTO globalQuote, String symbol) {
        return new StockPrice(
            symbol.toUpperCase(),
            extractPrice(globalQuote),
            LocalDateTime.now().toString()
        );
    }

    private double extractPrice(GlobalQuoteDTO globalQuote) {
        return Double.parseDouble(globalQuote.getPrice());
    }
}
