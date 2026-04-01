package com.shopqr.security;

import com.shopqr.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RoleBasedAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        ShopUserPrincipal principal = (ShopUserPrincipal) authentication.getPrincipal();
        User.Role role = principal.getUser().getRole();
        String target = switch (role) {
            case ADMIN -> "/admin/dashboard";
            case USER, MANAGER -> "/user/home";
        };
        response.sendRedirect(request.getContextPath() + target);
    }
}
