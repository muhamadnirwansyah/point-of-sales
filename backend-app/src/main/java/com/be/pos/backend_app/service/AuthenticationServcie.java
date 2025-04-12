package com.be.pos.backend_app.service;

import com.be.pos.backend_app.model.*;

public interface AuthenticationServcie {

    RegisterResponse register(RegisterRequest request);
    AuthenticationResponse signIn(AuthenticationRequest request);
    ExtractJwtTokenResponse extractJwtToken(ExtractJwtTokenRequest request);

}
