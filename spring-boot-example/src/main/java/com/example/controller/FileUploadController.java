package com.example.controller;

import com.example.model.ValidationResult;
import com.example.service.ValidationService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api")
public class FileUploadController {

    private final ValidationService validationService;

    public FileUploadController(ValidationService validationService) {
        this.validationService = validationService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ValidationResult upload(@RequestParam("file") MultipartFile file) throws IOException {
        String json = new String(file.getBytes(), StandardCharsets.UTF_8);
        return validationService.validate(json);
    }
}

