package com.practice.trainingapi.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;

import java.util.TimeZone;

@Configuration
public class TimezoneConfig {

    @PostConstruct
    public void TimezoneConfig() {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
    }

}
