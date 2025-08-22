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
import org.everit.json.schema.ValidationException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.stereotype.Service;

@Service
public class ValidationBenchmarkService {

	public Map<String, Object> validateWithAll(String schemaJson, String instanceJson) {
		Map<String, Object> result = new LinkedHashMap<>();
		result.put("blaze4j", runBlaze(schemaJson, instanceJson));
		result.put("json-sKema", runJsonSkema(schemaJson, instanceJson));
		result.put("everit", runEverit(schemaJson, instanceJson));
		return result;
}

	public Map<String, Object> validateWithNames(Map<String, String> nameToSchema, String instanceJson) {
		Map<String, Object> result = new LinkedHashMap<>();
		nameToSchema.forEach((name, schema) -> {
			switch (name) {
				case "blaze4j" -> result.put(name, runBlaze(schema, instanceJson));
				case "json-sKema" -> result.put(name, runJsonSkema(schema, instanceJson));
				case "everit" -> result.put(name, runEverit(schema, instanceJson));
				default -> result.put(name, Map.of("error", "unknown validator"));
			}
		});
		return result;
	}

	public Map<String, Object> validateWithAllSchemas(String modernSchema, String draft07Schema, String instanceJson) {
		Map<String, Object> result = new LinkedHashMap<>();
		result.put("blaze4j", runBlaze(modernSchema, instanceJson));
		result.put("json-sKema", runJsonSkema(modernSchema, instanceJson));
		result.put("everit", runEverit(draft07Schema, instanceJson));
		return result;
	}

	private Map<String, Object> runBlaze(String schemaJson, String instanceJson) {
		long start = System.nanoTime();
		boolean valid;
		try (CompiledSchema compiled = Blaze.compile(schemaJson)) {
			boolean res = Blaze.validate(compiled, instanceJson);
			valid = res;
		}
		long end = System.nanoTime();
		return map(valid, end - start);
	}

	private Map<String, Object> runJsonSkema(String schemaJson, String instanceJson) {
		long start = System.nanoTime();
		boolean valid;
		Schema schema = new SchemaLoader(new JsonParser(schemaJson).parse()).load();
		ValidationFailure failure = com.github.erosb.jsonsKema.Validator.forSchema(schema).validate(new JsonParser(instanceJson).parse());
		valid = failure == null;
		long end = System.nanoTime();
		return map(valid, end - start);
	}

	private Map<String, Object> runEverit(String schemaJson, String instanceJson) {
		long start = System.nanoTime();
		boolean valid;
		try {
			// Parse the schema
			JSONObject rawSchema = new JSONObject(new JSONTokener(schemaJson));
			org.everit.json.schema.Schema schema = org.everit.json.schema.loader.SchemaLoader.load(rawSchema);
			
			// Parse the instance based on whether it's an object or array
			String trimmed = instanceJson.trim();
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
		} catch (Exception e) {
			valid = false;
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


