package com.projects.useraccessmanagementsystem.filters;

import com.projects.useraccessmanagementsystem.util.JWTUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/*")
public class JWTAuthorizationFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();
        if (path.endsWith("/login.jsp") || path.endsWith("/signup.jsp") || path.endsWith("/")) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/");
            return;
        }

        String token = authHeader.substring(7); // Remove "Bearer "
        try {
            Claims claims = JWTUtil.validateToken(token);
            httpRequest.setAttribute("username", claims.getSubject());
            httpRequest.setAttribute("role", claims.get("role"));
        } catch (Exception e) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void destroy() {}
}
