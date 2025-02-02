package ru.ulstu.parsingservice.config;

import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DateTimeFormatterConfig {

    @Bean
    public DateTimeFormatter partitionDateTimeFormatter() {
        return DateTimeFormatter.ofPattern("yyyy_MM");
    }

}
