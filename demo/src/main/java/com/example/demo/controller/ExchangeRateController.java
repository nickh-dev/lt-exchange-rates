package com.example.demo.controller;

import com.example.demo.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.demo.entity.ExchangeRate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
public class ExchangeRateController {

    @Autowired
    private ExchangeRateService exchangeRateService;

    @GetMapping("/")
    public String showForm(Model model) {
        return "index";
    }

    @PostMapping("/calculate")
    public String calculate(@RequestParam double amount,
                            @RequestParam String fromCurrency,
                            @RequestParam String toCurrency,
                            Model model) {
        double rate = exchangeRateService.getConversionRate(fromCurrency, toCurrency);
        double convertedAmount = amount * rate;

        model.addAttribute("amount", amount);
        model.addAttribute("fromCurrency", fromCurrency);
        model.addAttribute("toCurrency", toCurrency);
        model.addAttribute("rate", rate);
        model.addAttribute("convertedAmount", convertedAmount);

        return "index";
    }
    @GetMapping("/viewHistory")
    public String redirectToHistory(@RequestParam String currency, @RequestParam String startDate, @RequestParam String endDate, RedirectAttributes redirectAttributes) {
        // Convert string dates to LocalDate
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        // Add parameters as flash attributes for redirect
        redirectAttributes.addAttribute("currency", currency);
        redirectAttributes.addAttribute("startDate", start);
        redirectAttributes.addAttribute("endDate", end);

        return "redirect:/history";
    }

    @GetMapping("/history")
    public String showExchangeRateHistory(
            @RequestParam String currency,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            Model model) {

        // Use default dates if none are provided
        if (startDate == null || endDate == null) {
            endDate = LocalDate.now().minusDays(1);  // Use yesterday's date to ensure data
            startDate = endDate.minusDays(7); // Fetch last 7 days
        }

        System.out.println("Using date range: " + startDate + " to " + endDate);

        List<ExchangeRate> history = exchangeRateService.fetchHistoricalRates(currency, startDate, endDate);

        model.addAttribute("currency", currency);
        model.addAttribute("history", history);

        return "history";
    }
}
