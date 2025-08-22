package com.example.controller;

import com.example.service.DataGeneratorService;
import com.example.service.SchemaService;
import com.example.service.ValidationBenchmarkService;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

	private final SchemaService schemaService;
	private final DataGeneratorService dataGeneratorService;
	private final ValidationBenchmarkService benchmarkService;

	public EmployeeController(SchemaService schemaService,
			DataGeneratorService dataGeneratorService,
			ValidationBenchmarkService benchmarkService) {
		this.schemaService = schemaService;
		this.dataGeneratorService = dataGeneratorService;
		this.benchmarkService = benchmarkService;
	}

	@GetMapping(value = "/generate", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> generate(@RequestParam(defaultValue = "1000") int count) {
		String json = dataGeneratorService.generateEmployeesJson(Math.max(1, count));
		return ResponseEntity.ok(json);
	}

	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Object>> upload(
			@RequestParam("file") MultipartFile file,
			@RequestParam(defaultValue = "blaze4j") String validator) throws Exception {
		String instanceJson = new String(file.getBytes());
		String schemaForEverit = schemaService.loadSchemaDraft07();
		String schemaForModern = schemaService.loadSchema2020();

		Map<String, Object> outcome = switch (validator.toLowerCase()) {
			case "blaze4j" -> benchmarkService.validateWithNames(Map.of("blaze4j", schemaForModern), instanceJson);
			case "json-skema", "skema" -> benchmarkService.validateWithNames(Map.of("json-sKema", schemaForModern), instanceJson);
			case "everit" -> benchmarkService.validateWithNames(Map.of("everit", schemaForEverit), instanceJson);
			case "all" -> benchmarkService.validateWithAllSchemas(schemaForModern, schemaForEverit, instanceJson);
			default -> throw new IllegalArgumentException("Unknown validator: " + validator);
		};
		return ResponseEntity.ok(outcome);
	}

	@PostMapping(value = "/benchmark", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Object>> benchmarkAll(@RequestParam("file") MultipartFile file) throws Exception {
		String instanceJson = new String(file.getBytes());
		String schemaForEverit = schemaService.loadSchemaDraft07();
		String schemaForModern = schemaService.loadSchema2020();
		Map<String, Object> all = benchmarkService.validateWithAllSchemas(schemaForModern, schemaForEverit, instanceJson);
		return ResponseEntity.ok(all);
	}
}


