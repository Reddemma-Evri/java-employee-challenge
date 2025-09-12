package com.reliaquest.api.exception;

import com.reliaquest.api.dto.ErrorDTO;
import com.reliaquest.api.model.ApiResponse;

public class RemoteApiException extends RuntimeException {
    private final ApiResponse<ErrorDTO> apiResponse;

    public RemoteApiException(ApiResponse<ErrorDTO> apiResponse) {
        super(apiResponse.status()); // use top-level message
        this.apiResponse = apiResponse;
    }

    public ApiResponse<ErrorDTO> getApiResponse() {
        return apiResponse;
    }
}
