package com.example.dto;

import java.util.List;

public record ValidationResponse(boolean valid, List<String> errors) {}
