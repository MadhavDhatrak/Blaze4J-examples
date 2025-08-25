package com.example;

import com.example.dto.ValidationRequest;
import com.example.dto.ValidationResponse;
import com.github.madhavdhatrak.blaze4j.BlazeValidator;
import com.github.madhavdhatrak.blaze4j.CompiledSchema;
import com.github.madhavdhatrak.blaze4j.SchemaCompiler;
import com.github.madhavdhatrak.blaze4j.ValidationError;
import com.github.madhavdhatrak.blaze4j.ValidationResult;
import java.util.Collections;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ValidationController {

    @PostMapping("/validate")
    public ResponseEntity<ValidationResponse> validate(@RequestBody ValidationRequest request) {
        SchemaCompiler compiler = new SchemaCompiler();
        try (CompiledSchema schema = compiler.compile(request.schema())) {
            BlazeValidator validator = new BlazeValidator();
            ValidationResult result = validator.validateWithDetails(schema, request.instance());

            List<String> errors = result.isValid() ? Collections.emptyList() :
                    result.getErrors().stream().map(ValidationError::toString).toList();

            return ResponseEntity.ok(new ValidationResponse(result.isValid(), errors));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ValidationResponse(false, List.of(e.getMessage())));
        }
    }
}
