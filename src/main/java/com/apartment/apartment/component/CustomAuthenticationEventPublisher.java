package com.apartment.apartment.component;

import java.util.logging.Logger;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationEventPublisher implements AuthenticationEventPublisher {
    private static final Logger log =
            Logger.getLogger(CustomAuthenticationEventPublisher.class.getName());

    @Override
    public void publishAuthenticationSuccess(Authentication authentication) {
        log.info("✅ Успішна автентифікація: " + authentication.getName());
    }

    @Override
    public void publishAuthenticationFailure(AuthenticationException exception,
                                             Authentication authentication) {
        log.warning("❌ Неуспішна автентифікація для " + authentication.getName()
                + ": " + exception.getMessage());
    }
}
