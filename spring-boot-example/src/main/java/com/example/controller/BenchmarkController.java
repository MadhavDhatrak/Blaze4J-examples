package com.example.controller;

import com.example.model.ValidationResult;
import com.example.service.ValidationService;
import com.example.util.DataGenerator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api")
public class BenchmarkController {

    private final ValidationService validationService;
    private final DataGenerator generator = new DataGenerator();

    public BenchmarkController(ValidationService validationService) {
        this.validationService = validationService;
    }

    @GetMapping("/benchmark")
    public ValidationResult benchmark(@org.springframework.web.bind.annotation.RequestParam(value = "size", defaultValue = "10000") int size) throws Exception {
        String json;

        // If default size (10k) requested and a pre-generated file exists, use it for speed & repeatability
        if (size == 10000) {
            try (InputStream is = getClass().getClassLoader().getResourceAsStream("example/employees-large.json")) {
                if (is != null) {
                    json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                } else {
                    json = generator.generate(size);
                }
            }
        } else {
            // Always generate when a custom size is specified
            json = generator.generate(size);
        }

        return validationService.validate(json);
    }
}

