package com.shopqr.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 관리자 전용 경로에 권한이 없는 사용자가 접근한 경우 콘솔에 로그를 남기고 메인(/)으로 이동합니다.
 */
@Component
public class AdminAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger log = LoggerFactory.getLogger(AdminAccessDeniedHandler.class);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userInfo = (auth != null && auth.isAuthenticated()) ? auth.getName() : "anonymous";
        log.warn("권한 없음: 관리자 페이지 접근 거부 — 사용자={}, 요청 URI={}", userInfo, request.getRequestURI());
        response.sendRedirect(request.getContextPath() + "/");
    }
}
