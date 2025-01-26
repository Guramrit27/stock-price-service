# Stock Historical Data Fetcher

This script fetches historical stock data for S&P 500 companies using the stock-price-service API. The process is split into two steps:
1. Fetching and saving stock symbols
2. Fetching historical data for the saved symbols

## Features

- Two-step process to separate symbol collection from data fetching
- Rate-limited to 5 API calls per minute
- Saves stock symbols in a text file
- Saves historical data for each stock in JSON format
- Includes progress bar and logging
- Handles errors gracefully
- Skips already processed stocks for resume capability

## Prerequisites

- Python 3.7+
- pip (Python package installer)
- Running instance of stock-price-service on localhost:8080

## Installation

1. Install required packages:
   ```bash
   pip install -r requirements.txt
   ```

## Usage

1. Make sure the stock-price-service is running on localhost:8080

2. First, fetch and save the stock symbols:
   ```bash
   python fetch_historical_data.py --fetch-symbols
   ```

3. Then, fetch historical data for all symbols:
   ```bash
   python fetch_historical_data.py --fetch-data
   ```

## Directory Structure

```
stock_data/
├── stock_symbols.txt      # List of stock symbols
└── historical/           # Directory containing historical data
    ├── AAPL_historical.json
    ├── GOOGL_historical.json
    └── ...
```

## Output

- Stock symbols are saved in `stock_data/stock_symbols.txt`
- Historical data is saved in `stock_data/historical/{SYMBOL}_historical.json`
- Logs are saved in `stock_data_fetcher.log`

## Rate Limiting

The script is configured to make a maximum of 5 API calls per minute to comply with the service's rate limits. This can be adjusted by modifying the `CALLS_PER_MINUTE` constant in the script.

## Resume Capability

If the script is interrupted, you can safely run it again with `--fetch-data`. It will skip any stocks that have already been processed.
