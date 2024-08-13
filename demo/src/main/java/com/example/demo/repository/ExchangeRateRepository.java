package com.example.demo.repository;

import com.example.demo.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
}
