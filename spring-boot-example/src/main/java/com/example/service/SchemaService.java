package com.example.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class SchemaService {

	public String loadSchema2020() {
		return loadResourceAsString("schemas/employee-schema-2020-12.json");
	}

	public String loadSchemaDraft07() {
		return loadResourceAsString("schemas/employee-schema-draft07.json");
	}

	private String loadResourceAsString(String path) {
		try {
			ClassPathResource resource = new ClassPathResource(path);
			try (InputStream in = resource.getInputStream()) {
				byte[] bytes = in.readAllBytes();
				return new String(bytes, StandardCharsets.UTF_8);
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to load resource: " + path, e);
		}
	}
}


