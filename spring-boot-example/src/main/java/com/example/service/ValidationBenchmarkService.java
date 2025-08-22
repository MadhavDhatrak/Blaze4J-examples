package com.example.service;

import com.github.madhavdhatrak.blaze4j.Blaze;
import com.github.madhavdhatrak.blaze4j.CompiledSchema;
import com.github.erosb.jsonsKema.JsonParser;
import com.github.erosb.jsonsKema.Schema;
import com.github.erosb.jsonsKema.SchemaLoader;
import com.github.erosb.jsonsKema.ValidationFailure;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.networknt.schema.InputFormat;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.everit.json.schema.ValidationException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.stereotype.Service;

@Service
public class ValidationBenchmarkService {

    public Map<String, Object> validateWithAll(String schemaJson, String instanceJson, int iterationCount) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("blaze4j", runBlaze(schemaJson, instanceJson, iterationCount));
        result.put("json-sKema", runJsonSkema(schemaJson, instanceJson, iterationCount));
        result.put("everit", runEverit(schemaJson, instanceJson, iterationCount));
        return result;
    }

    public Map<String, Object> validateWithNames(Map<String, String> nameToSchema, String instanceJson, int iterationCount) {
        Map<String, Object> result = new LinkedHashMap<>();
        nameToSchema.forEach((name, schema) -> {
            switch (name) {
                case "blaze4j" -> result.put(name, runBlaze(schema, instanceJson, iterationCount));
                case "json-sKema" -> result.put(name, runJsonSkema(schema, instanceJson, iterationCount));
                case "everit" -> result.put(name, runEverit(schema, instanceJson, iterationCount));
                case "networknt" ->  result.put(name, runNetworkNt(schema, instanceJson, iterationCount));
                default -> result.put(name, Map.of("error", "unknown validator"));
            }
        });
        return result;
    }

    public Map<String, Object> validateWithAllSchemas(String modernSchema, String draft07Schema, String instanceJson,
                                                      int iterationCount) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("blaze4j", runBlaze(modernSchema, instanceJson, iterationCount));
        result.put("json-sKema", runJsonSkema(modernSchema, instanceJson, iterationCount));
        result.put("everit", runEverit(draft07Schema, instanceJson, iterationCount));
        result.put("networknt", runNetworkNt(modernSchema, instanceJson, iterationCount));
        return result;
    }

    private Map<String, Object> runBlaze(String schemaJson, String instanceJson, int iterationCount) {
        boolean valid = true;
        try (CompiledSchema compiled = Blaze.compile(schemaJson)) {
            long start = System.nanoTime();
            for (int i = 0; i < iterationCount; ++i) {
                valid = Blaze.validate(compiled, instanceJson);
            }
            long end = System.nanoTime();
            return map(valid, end - start);
        }
    }

    private Map<String, Object> runJsonSkema(String schemaJson, String instanceJson, int iterationCount) {
        boolean valid = true;
        Schema schema = new SchemaLoader(new JsonParser(schemaJson).parse()).load();
        long start = System.nanoTime();
        for (int i = 0; i < iterationCount; ++i) {
            ValidationFailure failure = com.github.erosb.jsonsKema.Validator.forSchema(schema).validate(
                    new JsonParser(instanceJson).parse());
            valid = failure == null;
        }
        long end = System.nanoTime();
        return map(valid, end - start);
    }

    private Map<String, Object> runEverit(String schemaJson, String instanceJson, int iterationCount) {
        boolean valid = true;
        long start = -1;
        // Parse the schema
        JSONObject rawSchema = new JSONObject(new JSONTokener(schemaJson));
        org.everit.json.schema.Schema schema = org.everit.json.schema.loader.SchemaLoader.load(rawSchema);
        // Parse the instance based on whether it's an object or array
        String trimmed = instanceJson.trim();
        start = System.nanoTime();
        for (int i = 0; i < iterationCount; ++i) {
            try {
                if (trimmed.startsWith("{")) {
                    JSONObject instance = new JSONObject(new JSONTokener(instanceJson));
                    schema.validate(instance);
                } else {
                    JSONArray instance = new JSONArray(new JSONTokener(instanceJson));
                    schema.validate(instance);
                }
                valid = true;

            } catch (ValidationException e) {
                valid = false;
            }
        }
        long end = System.nanoTime();
        return map(valid, end - start);
    }

    private Map<String, Object> runNetworkNt(String schemaJson, String instanceJson, int iterationCount) {
        JsonSchema schema = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012).getSchema(schemaJson);
        boolean valid = true;
        long start = System.nanoTime();
        for (int i = 0; i < iterationCount; ++i) {
            Set<ValidationMessage> assertions = schema.validate(instanceJson, InputFormat.JSON, executionContext -> {
                // By default since Draft 2019-09 the format keyword only generates annotations and not assertions
                executionContext.getExecutionConfig().setFormatAssertionsEnabled(true);
            });
            valid = assertions.isEmpty();
        }
        long end = System.nanoTime();
        return map(valid, end - start);
    }

    private Map<String, Object> map(boolean valid, long nanos) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("valid", valid);
        m.put("nanos", nanos);
        m.put("millis", Duration.ofNanos(nanos).toMillis());
        return m;
    }
}


