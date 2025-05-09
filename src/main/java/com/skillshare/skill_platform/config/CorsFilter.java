package com.skillshare.skill_platform.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        
        String origin = request.getHeader("Origin");
<<<<<<< HEAD
        if (origin != null && (origin.equals("http://localhost:5173") || origin.equals("http://localhost:3000") || origin.equals("http://localhost:5174"))) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        } else {
            response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        }
=======
        String method = request.getMethod();
        String uri = request.getRequestURI();
>>>>>>> main
        
        System.out.println("CORS Filter: Processing " + method + " request to " + uri + " from origin: " + origin);
        
        response.setHeader("Access-Control-Allow-Origin", origin != null ? origin : "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", 
                "Origin, X-Requested-With, Content-Type, Accept, Authorization, X-Auth-Token, Cache-Control, Pragma");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Expose-Headers", 
                "Authorization, Content-Type, Access-Control-Allow-Origin, Access-Control-Allow-Credentials");

        if ("OPTIONS".equalsIgnoreCase(method)) {
            System.out.println("CORS Filter: Handling OPTIONS preflight request - returning 200 OK");
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            if (uri.startsWith("/api/users/") && uri.contains("/profile")) {
                System.out.println("CORS Filter: Processing user profile request, ensuring no OAuth redirect");
            }
            
            chain.doFilter(req, res);
        }
        
        System.out.println("CORS Filter: Completed processing request with status: " + response.getStatus());
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("CORS Filter initialized");
    }

    @Override
    public void destroy() {
    }
} 