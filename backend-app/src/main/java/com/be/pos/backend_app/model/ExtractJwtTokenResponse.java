package com.be.pos.backend_app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExtractJwtTokenResponse {

    private Long id;
    private String email;
    private String roles;
    private String phoneNumber;

}
