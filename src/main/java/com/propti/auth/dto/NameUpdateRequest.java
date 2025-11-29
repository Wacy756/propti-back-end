package com.propti.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NameUpdateRequest {
    @NotBlank
    private String name;
}
