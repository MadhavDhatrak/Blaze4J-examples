package com.example;

import com.github.madhavdhatrak.blaze4j.BlazeValidator;
import com.github.madhavdhatrak.blaze4j.CompiledSchema;
import com.github.madhavdhatrak.blaze4j.SchemaCompiler;
import com.github.madhavdhatrak.blaze4j.SchemaRegistry;

public class PreRegisterSchemaTest {
    public static void main(String[] args) {
        String integerSchema = """
            {
              "$schema": "https://json-schema.org/draft/2020-12/schema",
              "type": "integer"
            }""";
        
        String schemaUri = "my-integer-schema";
        
        SchemaRegistry registry = new SchemaRegistry();
        registry.register(schemaUri, integerSchema);
        
        SchemaCompiler compiler = new SchemaCompiler(registry);
        
        String mainSchema = """
            {
              "$schema": "https://json-schema.org/draft/2020-12/schema",
              "$ref": "my-integer-schema"
            }""";
        
        try (CompiledSchema schema = compiler.compile(mainSchema)) {
            // Test valid integer
            BlazeValidator validator = new BlazeValidator();
            boolean validResult = validator.validate(schema, "42");
            System.out.println("validResult: " + validResult);
            
            // Test invalid string
            boolean invalidResult = validator.validate(schema, "\"not an integer\"");
            System.out.println("invalidResult: " + invalidResult);
            
            // Test another valid integer
            boolean anotherValidResult = validator.validate(schema, "100");
            System.out.println("anotherValidResult: " + anotherValidResult);
            
            // Test invalid boolean
            boolean invalidBoolResult = validator.validate(schema, "true");
            System.out.println("invalidBoolResult: " + invalidBoolResult);
        }
    }
}

// use this command to run this test : mvn compile exec:java -Dexec.mainClass=com.example.PreRegisterSchemaTest
