package com.example.service;

import com.example.model.EngineResult;
import com.example.model.ValidationResult;
import com.github.madhavdhatrak.blaze4j.Blaze;
import com.github.madhavdhatrak.blaze4j.CompiledSchema;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class ValidationService {

    private CompiledSchema blazeSchema;

    @PostConstruct
    public void init() throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("schema/employee-schema.json")) {
            if (is == null) {
                throw new IllegalStateException("employee-schema.json not found in classpath");
            }
            String schemaJson = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            blazeSchema = Blaze.compile(schemaJson);
        }
    }

    public ValidationResult validate(String json) {
        // Parse JSON once - measure parsing time
        long parseStart = System.nanoTime();
        String trimmed = json.trim();
        // Just parse to verify it's valid JSON, we don't use the result
        if (trimmed.startsWith("[")) {
            new JSONArray(new JSONTokener(json));
        } else {
            new JSONObject(new JSONTokener(json));
        }
        long parseTimeMs = (System.nanoTime() - parseStart) / 1_000_000;
        
        // Blaze4j validation - operates on raw JSON string
        long blazeStart = System.nanoTime();
        boolean blazeValid;
        try {
            blazeValid = Blaze.validate(blazeSchema, json);
        } catch (Exception ex) {
            blazeValid = false;
        }
        long blazeTimeMs = (System.nanoTime() - blazeStart) / 1_000_000;
        EngineResult blazeResult = new EngineResult(blazeValid, blazeTimeMs);

        return new ValidationResult(blazeResult, parseTimeMs);
    }

    @PreDestroy
    public void close() {
        if (blazeSchema != null) {
            blazeSchema.close();
        }
    }
}
