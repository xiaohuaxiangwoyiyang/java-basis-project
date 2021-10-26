package com.example.form;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class UserRegisterForm {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String email;
}
