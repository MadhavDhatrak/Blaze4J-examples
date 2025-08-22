package com.example.controller;

import com.example.service.DataGeneratorService;
import com.example.service.SchemaService;
import com.example.service.ValidationBenchmarkService;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/benchmark")
public class BenchmarkController {

    private final SchemaService schemaService;
    private final DataGeneratorService dataGeneratorService;
    private final ValidationBenchmarkService benchmarkService;

    public BenchmarkController(SchemaService schemaService,
            DataGeneratorService dataGeneratorService,
            ValidationBenchmarkService benchmarkService) {
        this.schemaService = schemaService;
        this.dataGeneratorService = dataGeneratorService;
        this.benchmarkService = benchmarkService;
    }

    @GetMapping(value = "/direct", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> directBenchmark(
            @RequestParam(defaultValue = "200000") int count) {
        
        
        String instanceJson = dataGeneratorService.generateEmployeesJson(count);
        
        // Get schemas
        String schemaForEverit = schemaService.loadSchemaDraft07();
        String schemaForModern = schemaService.loadSchema2020();
        
        // Run benchmark
        Map<String, Object> results = benchmarkService.validateWithAllSchemas(
                schemaForModern, schemaForEverit, instanceJson);
        
        return ResponseEntity.ok(results);
    }
}
