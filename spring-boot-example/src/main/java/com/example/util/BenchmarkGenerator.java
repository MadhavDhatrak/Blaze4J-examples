package com.example.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility to generate a large employee dataset and save it to resources.
 * Run this once to create a large benchmark file that will be used for all future tests.
 */
public class BenchmarkGenerator {
    
    public static void main(String[] args) throws IOException {
        int size = args.length > 0 ? Integer.parseInt(args[0]) : 1_000_000;
        System.out.println("Generating " + size + " employee records...");
        
        DataGenerator generator = new DataGenerator();
        String json = generator.generate(size);
        
        // Create directories if they don't exist
        Path resourcesPath = Paths.get("src", "main", "resources", "example");
        Files.createDirectories(resourcesPath);
        
        // Write the file
        Path outputPath = resourcesPath.resolve("employees-large.json");
        try (FileOutputStream fos = new FileOutputStream(outputPath.toFile())) {
            fos.write(json.getBytes(StandardCharsets.UTF_8));
        }
        
        System.out.println("Generated " + outputPath.toAbsolutePath() + " (" + 
                Files.size(outputPath) / (1024 * 1024) + " MB)");
    }
}
