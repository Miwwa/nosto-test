# Nosto Currency Converter Backend

This project is a service for a currency conversion application.
It allows converting an amount from one currency to another using real-time exchange rates provided by an external API.

The application is built on the [Vert.x](https://vertx.io/) framework and [React](https://react.dev/)

## Prerequisites

- Docker
- A valid [swop.cx](https://swop.cx/) api key for fetching currency exchange rates

## Running the Application Locally

#### Clone the Repository

```shell
git clone https://github.com/Miwwa/nosto-test.git
cd nosto-test
```

#### Build and Run with Docker

1. Build the Docker image

```shell
docker build -t currency-converter .
```

2. Run the container:

```shell
   docker run -p 8888:8888 -e SWOP_API_KEY=<YOUR_SWOP_API_KEY> currency-converter
```

---
## Interacting with the API
The backend exposes one primary REST API endpoint for currency conversion:
#### Endpoint: `/api/convert/:baseCurrency/:quoteCurrency?amount={number}`
- **Method**: `GET`
- **Description**: Converts a given amount from one currency to another.
- **Path Parameters**:
    - `baseCurrency` (string): The currency to convert from.
    - `quoteCurrency` (string): The currency to convert to.

- **Query Parameters**:
    - `amount` (number): The amount in the base currency to convert.

- **Response Format**: JSON
- **HTTP Status Codes**:
    - `200`: Success.
    - `400`: Invalid parameters or unsupported currencies.
    - `500`: Internal server error.

#### Request Example:
``` http
GET /api/convert/EUR/USD?amount=100
```
#### Sample Response:
``` json
{
  "baseCurrency": "EUR",
  "quoteCurrency": "USD",
  "baseAmount": 100.0,
  "quoteAmount": 105.5
}
```
#### Error Response Example:
``` json
{
  "error": "Bad Request",
  "code": "EXCHANGE_RATE_NOT_FOUND"
}
```
