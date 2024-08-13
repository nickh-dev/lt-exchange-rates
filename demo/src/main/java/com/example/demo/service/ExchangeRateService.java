package com.example.demo.service;

import com.example.demo.client.FxRatesClient;
import com.example.demo.entity.ExchangeRate;
import com.example.demo.repository.ExchangeRateRepository;
import com.example.demo.wsdl.GetFxRatesForCurrencyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;
import jakarta.xml.bind.JAXBElement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ExchangeRateService {

    @Autowired
    private FxRatesClient fxRatesClient;

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    public double getConversionRate(String fromCurrency, String toCurrency) {
        double rateFrom = fetchRate(fromCurrency);
        double rateTo = fetchRate(toCurrency);
        return rateFrom > 0 ? rateTo / rateFrom : 0;  // Ensure no division by zero
    }

    private double fetchRate(String currency) {
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        GetFxRatesForCurrencyResponse response = fxRatesClient.getFxRatesForCurrency("EU", currency, today, today);
        return extractRate(response, currency);
    }



    private void logResponse(GetFxRatesForCurrencyResponse response) {
        List<Object> content = response.getGetFxRatesForCurrencyResult().getContent();
        for (Object item : content) {
            if (item instanceof Element) {
                Element element = (Element) item;
                System.out.println("Element Name: " + element.getTagName());
                System.out.println("Element Value: " + element.getTextContent());
            } else if (item instanceof JAXBElement<?>) {
                JAXBElement<?> jaxbElement = (JAXBElement<?>) item;
                System.out.println("JAXBElement Name: " + jaxbElement.getName());
                System.out.println("JAXBElement Value: " + jaxbElement.getValue());
            } else {
                System.out.println("Unknown Type: " + item.getClass().getName() + " Value: " + item.toString());
            }
        }
    }

    private double extractRate(GetFxRatesForCurrencyResponse response, String targetCurrency) {
        List<Object> content = response.getGetFxRatesForCurrencyResult().getContent();
        for (Object item : content) {
            if (item instanceof Element) {
                Element element = (Element) item;
                String elementValue = element.getTextContent().trim();
                if (!elementValue.startsWith("0001") && elementValue.contains(targetCurrency)) {
                    // Split the string to extract the rate
                    String[] parts = elementValue.split(targetCurrency);
                    if (parts.length > 1) {
                        String rateStr = parts[1];
                        try {
                            return Double.parseDouble(rateStr);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();  // Handle parsing errors
                        }
                    }
                }
            }
        }
        return 1.0;  // Default to 1 if no valid rate found
    }

    public List<ExchangeRate> fetchHistoricalRates(String currency, LocalDate startDate, LocalDate endDate) {
        String formattedStart = startDate.format(DateTimeFormatter.ISO_DATE);
        String formattedEnd = endDate.format(DateTimeFormatter.ISO_DATE);

        // Print the request parameters
        System.out.println("Requesting historical rates for " + currency + " from " + formattedStart + " to " + formattedEnd);

        GetFxRatesForCurrencyResponse response = fxRatesClient.getFxRatesForCurrency("EU", currency, formattedStart, formattedEnd);
        return extractHistoricalRates(response, currency);
    }

    private List<ExchangeRate> extractHistoricalRates(GetFxRatesForCurrencyResponse response, String targetCurrency) {
        List<ExchangeRate> rates = new ArrayList<>();
        List<Object> content = response.getGetFxRatesForCurrencyResult().getContent();

        System.out.println("Response content size: " + content.size());

        for (Object item : content) {
            if (item instanceof Element) {
                Element element = (Element) item;
                String elementValue = element.getTextContent().trim();

                System.out.println("Element Value: " + elementValue);

                // Use a regex pattern to match date and rate with flexible decimal places
                Pattern pattern = Pattern.compile("(\\d{4}-\\d{2}-\\d{2})EUR1" + targetCurrency + "(\\d+\\.\\d+)");
                Matcher matcher = pattern.matcher(elementValue);

                while (matcher.find()) {
                    try {
                        String datePart = matcher.group(1); // Extract the date
                        LocalDate date = LocalDate.parse(datePart, DateTimeFormatter.ISO_DATE);

                        String rateStr = matcher.group(2); // Extract the rate
                        double rate = Double.parseDouble(rateStr);

                        ExchangeRate exchangeRate = new ExchangeRate();
                        exchangeRate.setCurrency(targetCurrency);
                        exchangeRate.setDate(date);
                        exchangeRate.setRate(rate);

                        rates.add(exchangeRate);

                        System.out.println("Parsed Date: " + date + ", Rate: " + rate);
                    } catch (Exception e) {
                        System.out.println("Error parsing match: " + matcher.group());
                        e.printStackTrace(); // Handle parsing errors
                    }
                }
            }
        }
        return rates;
    }




    public void saveExchangeRate(String currency, double rate) {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setCurrency(currency);
        exchangeRate.setRate(rate);
        exchangeRate.setDate(LocalDate.now()); // Setting the current date as the rate date
        exchangeRateRepository.save(exchangeRate);
    }
}
