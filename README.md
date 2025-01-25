# Stock Price Service

A Spring Boot reactive application that provides real-time stock prices through Alpha Vantage API.

## Requirements
- Java 17 or higher
- Maven 3.6 or higher
- Alpha Vantage API key

## Setup

1. Get your free API key from [Alpha Vantage](https://www.alphavantage.co/support/#api-key)

2. Set your API key as an environment variable:
```bash
export ALPHA_VANTAGE_API_KEY=your_api_key_here
```

## Building the Application
```bash
mvn clean install
```

## Running the Application
```bash
mvn spring-boot:run
```

## API Usage
Get stock price for a symbol:
```
GET http://localhost:8080/api/stocks/{symbol}/price
```

Example:
```
GET http://localhost:8080/api/stocks/AAPL/price
```

Response:
```json
{
    "symbol": "AAPL",
    "price": 150.75,
    "timestamp": "2025-01-25T12:55:35"
}
```

## Rate Limits
The free tier of Alpha Vantage API has the following limitations:
- 5 API calls per minute
- 500 API calls per day

For production use, consider upgrading to a paid tier or using alternative stock data providers.
