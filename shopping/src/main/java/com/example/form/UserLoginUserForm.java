package com.example.form;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class UserLoginUserForm {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
