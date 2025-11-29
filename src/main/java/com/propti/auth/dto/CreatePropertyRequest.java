package com.propti.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePropertyRequest {
    private String name;
    private String address;
    private String postcode;
    private Integer rent;
    private Boolean paid;
}
