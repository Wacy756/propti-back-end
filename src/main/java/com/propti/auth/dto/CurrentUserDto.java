package com.propti.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUserDto {
    private String id;
    private String role;
    private String email;
    private String name;
    private String phone;
    private String companyName;
}
