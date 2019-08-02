package ru.demi.docworkflow.service;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DateProvider {

    public LocalDateTime getNow() {
        return LocalDateTime.now();
    }
}
