#!/usr/bin/env python3

import requests
import time
from datetime import datetime
import json
from ratelimit import limits, sleep_and_retry
from tqdm import tqdm
import os
import logging
from pathlib import Path
import pandas as pd
from bs4 import BeautifulSoup

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('stock_data_fetcher.log'),
        logging.StreamHandler()
    ]
)

# Constants
CALLS_PER_MINUTE = 5
ONE_MINUTE = 60
SECONDS_PER_CALL = ONE_MINUTE / CALLS_PER_MINUTE  # 12 seconds between calls
BASE_URL = "http://localhost:8080/api/stocks"
DATA_DIR = "stock_data"
STOCKS_FILE = os.path.join(DATA_DIR, "stock_symbols.txt")
HISTORICAL_DATA_DIR = os.path.join(DATA_DIR, "historical")

# Create necessary directories
Path(DATA_DIR).mkdir(parents=True, exist_ok=True)
Path(HISTORICAL_DATA_DIR).mkdir(parents=True, exist_ok=True)

def fetch_and_save_stock_symbols():
    """
    Fetch S&P 500 stock symbols from Wikipedia and save them to a file
    """
    try:
        logging.info("Fetching S&P 500 stock symbols from Wikipedia...")
        
        # Fetch the Wikipedia page
        url = 'https://en.wikipedia.org/wiki/List_of_S%26P_500_companies'
        headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36'
        }
        response = requests.get(url, headers=headers)
        response.raise_for_status()
        
        # Parse the HTML content
        soup = BeautifulSoup(response.text, 'html.parser')
        table = soup.find('table', {'class': 'wikitable'})
        
        # Extract symbols from the table
        symbols = []
        for row in table.find_all('tr')[1:]:  # Skip header row
            cols = row.find_all('td')
            if cols:
                symbol = cols[0].text.strip()
                symbol = symbol.replace('.', '-')  # Replace dots with dashes for symbols like BRK.B
                symbols.append(symbol)
        
        # Save symbols to file
        with open(STOCKS_FILE, 'w') as f:
            for symbol in symbols:
                f.write(f"{symbol}\n")
        
        logging.info(f"Successfully saved {len(symbols)} S&P 500 stock symbols to {STOCKS_FILE}")
        return True
    except Exception as e:
        logging.error(f"Error fetching and saving stock symbols: {str(e)}")
        return False

def read_stock_symbols():
    """
    Read stock symbols from the saved file
    """
    try:
        with open(STOCKS_FILE, 'r') as f:
            symbols = [line.strip() for line in f if line.strip()]
        logging.info(f"Read {len(symbols)} stock symbols from {STOCKS_FILE}")
        return symbols
    except Exception as e:
        logging.error(f"Error reading stock symbols: {str(e)}")
        return []

@sleep_and_retry
@limits(calls=CALLS_PER_MINUTE, period=ONE_MINUTE)
def get_historical_data(symbol):
    """
    Fetch historical data for a given stock symbol with rate limiting
    """
    url = f"{BASE_URL}/{symbol}/historical"
    try:
        response = requests.get(url)
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        logging.error(f"Error fetching data for {symbol}: {str(e)}")
        return None

def save_historical_data(symbol, data):
    """
    Save historical data to JSON file
    """
    if data is None:
        return

    filename = os.path.join(HISTORICAL_DATA_DIR, f"{symbol}_historical.json")
    try:
        with open(filename, 'w') as f:
            json.dump(data, f, indent=2)
        logging.info(f"Saved historical data for {symbol}")
    except Exception as e:
        logging.error(f"Error saving data for {symbol}: {str(e)}")

def fetch_historical_data():
    """
    Read symbols and fetch historical data for each
    """
    # Read symbols from file
    symbols = read_stock_symbols()
    
    if not symbols:
        logging.error("No symbols found. Please run with --fetch-symbols first")
        return

    logging.info(f"Starting historical data collection for {len(symbols)} symbols...")
    logging.info(f"Rate limit: {CALLS_PER_MINUTE} calls per minute (1 call every {SECONDS_PER_CALL} seconds)")
    
    # Process each symbol
    for i, symbol in enumerate(tqdm(symbols, desc="Fetching historical data")):
        try:
            # Skip if already processed
            output_file = os.path.join(HISTORICAL_DATA_DIR, f"{symbol}_historical.json")
            if os.path.exists(output_file):
                logging.info(f"Skipping {symbol} - already processed")
                continue

            # Get and save historical data
            data = get_historical_data(symbol)
            if data:
                save_historical_data(symbol, data)
            
            # Enforce rate limiting with a sleep
            # The @limits decorator helps prevent bursts, but we'll add a sleep to be extra safe
            time.sleep(SECONDS_PER_CALL)
            
            # Log progress every 10 symbols
            if (i + 1) % 10 == 0:
                logging.info(f"Processed {i + 1}/{len(symbols)} symbols")
            
        except Exception as e:
            logging.error(f"Error processing {symbol}: {str(e)}")
            continue

    logging.info("Historical data collection completed!")

def main():
    import argparse
    parser = argparse.ArgumentParser(description='Stock Historical Data Fetcher')
    parser.add_argument('--fetch-symbols', action='store_true', 
                      help='Fetch and save stock symbols')
    parser.add_argument('--fetch-data', action='store_true',
                      help='Fetch historical data for saved symbols')
    
    args = parser.parse_args()
    
    if args.fetch_symbols:
        fetch_and_save_stock_symbols()
    elif args.fetch_data:
        fetch_historical_data()
    else:
        parser.print_help()

if __name__ == "__main__":
    main()
