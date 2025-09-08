package com.reliaquest.api.model;

import java.util.Optional;

public record ApiResponse<T>(Optional<T> data, boolean error, int statusCode, String status) {}
