package com.nelly.hivtbmonitoringsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TestSmsRequest {

    @NotBlank
    private String phone;

    @NotBlank
    private String message;
}
