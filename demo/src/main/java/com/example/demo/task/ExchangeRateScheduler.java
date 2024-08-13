package com.example.demo.task;

import com.example.demo.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ExchangeRateScheduler {

    @Autowired
    private ExchangeRateService exchangeRateService;

    @Scheduled(cron = "0 0 10 * * ?")  // Runs every day at 10 AM
    public void updateExchangeRates() {
        String[] currencies = {"USD", "EUR", "GBP"};
        for (String currency : currencies) {
            double rate = exchangeRateService.getConversionRate("EUR", currency);
            exchangeRateService.saveExchangeRate(currency, rate);
        }
    }
}
