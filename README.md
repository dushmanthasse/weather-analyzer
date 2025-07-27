# Weather Analyzer

A Spring Boot application that fetches and analyzes weather data from external APIs,
providing weather summaries with advanced features like asynchronous processing and intelligent caching.

## Prerequisites

Before using this project, ensure you have the following:

- Java 17
- Apache Maven 3.8.5
- OpenWeatherMap API Key

## How to use

1. Clone this repository to your local machine.

    ```bash
      git clone https://github.com/dushmanthasse/weather-analyzer.git
      cd weather-analyzer
    ```

2. Add env variables

   ```
     WEATHER_API_KEY=your-openweathermap-api-key
   ```

3. Install Maven dependency

    ```
      mvn clean package -DskipTests
    ```

4. Run the `*.jar` file

    ```
      java -jar weather-analyzer-0.0.1.jar
    ```

## API Endpoint

```
GET /weather?city={cityName}
```

### Response

```
{
  "city": "London",
  "averageTemperature": 15.5,
  "hottestDay": "2024-11-20",
  "coldestDay": "2024-11-18"
}
```

## Example Usage

```
   curl "http://localhost:8080/weather?city=London"
   curl "http://localhost:8080/weather?city=Tokyo"
   curl "http://localhost:8080/weather?city=Kandy"
   curl "http://localhost:8080/weather?city=Sydney"
   curl "http://localhost:8080/weather?city=Colombo"
```

## Testing

Run all tests

```
   mvn test
```

## Contact

For questions or support, please contact:

**Chanaka Dushmantha**  
Email: [dushmantha.sse@gmail.com](mailto:dushmantha.sse@gmail.com)
