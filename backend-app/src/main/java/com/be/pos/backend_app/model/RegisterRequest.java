package com.be.pos.backend_app.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Email is required !")
    @Email(message = "Email in valid !")
    private String email;
    @NotBlank(message = "Password is required !")
    private String password;
    private Long rolesId;
    @NotBlank(message = "Phone Number is required !")
    private String phoneNumber;
    @NotBlank(message = "Full name is required !")
    private String name;

}
