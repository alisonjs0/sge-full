package com.example.SGE.Config;

import com.example.SGE.Service.CustomUserDetailService;
import com.example.SGE.Service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailService userDetailService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String requestPath = request.getRequestURI();
        String method = request.getMethod();

        // Debug para verificar requisições
        System.out.println("=== SecurityFilter Debug ===");
        System.out.println("Path: " + requestPath + " | Method: " + method);
        
        // Debug específico para PUT/DELETE
        if (method.equals("PUT") || method.equals("DELETE") || method.equals("PATCH")) {
            System.out.println("🔴 REQUISIÇÃO DE ATUALIZAÇÃO/DELEÇÃO DETECTADA");
            System.out.println("Authorization Header: " + request.getHeader("Authorization"));
        }

        // Endpoints públicos que não precisam de autenticação
        if ((requestPath.equals("/auth/login") && method.equals("POST")) ||
            (requestPath.equals("/users") && method.equals("POST")) ||
            requestPath.startsWith("/health") ||
            requestPath.startsWith("/token-infinito")) {
            System.out.println("Endpoint público detectado - permitindo acesso");
            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("Endpoint protegido - validando token");

        // Recupera o token do header Authorization para endpoints protegidos
        String token = this.recoverToken(request);
        
        // Debug mais detalhado para requisições de atualização
        if (method.equals("PUT") || method.equals("DELETE") || method.equals("PATCH")) {
            System.out.println("🔍 Token presente: " + (token != null ? "SIM" : "NÃO"));
            if (token != null) {
                System.out.println("🔍 Token preview: " + token.substring(0, Math.min(30, token.length())) + "...");
            }
        }

        if (requestPath.contains("/inspections")) {
            System.out.println("=== Debug Token ===");
            System.out.println("Token recebido: " + (token != null ? "presente" : "null"));
            System.out.println("Auth context: " + SecurityContextHolder.getContext().getAuthentication());
        }

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                String username = jwtService.extractUsername(token);
                if (requestPath.contains("/inspections") || method.equals("PUT") || method.equals("DELETE")) {
                    System.out.println("Username extraído: " + username);
                }

                UserDetails userDetails = userDetailService.loadUserByUsername(username);
                if (requestPath.contains("/inspections") || method.equals("PUT") || method.equals("DELETE")) {
                    System.out.println("UserDetails carregado: " + userDetails.getUsername());
                }

                if (jwtService.isTokenValid(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    if (requestPath.contains("/inspections") || method.equals("PUT") || method.equals("DELETE")) {
                        System.out.println("✅ Token válido! Usuário: " + username + " | Authorities: " + userDetails.getAuthorities());
                    }
                } else {
                    if (requestPath.contains("/inspections") || method.equals("PUT") || method.equals("DELETE")) {
                        System.out.println("❌ Token inválido para usuário: " + username);
                    }
                }
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
                if (requestPath.contains("/inspections") || method.equals("PUT") || method.equals("DELETE")) {
                    System.out.println("❌ Erro ao validar token: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            if (requestPath.contains("/inspections") || method.equals("PUT") || method.equals("DELETE")) {
                System.out.println("⚠️ Token não encontrado ou autenticação já definida");
                System.out.println("⚠️ Token is null: " + (token == null));
                System.out.println("⚠️ Auth already set: " + (SecurityContextHolder.getContext().getAuthentication() != null));
            }
        }

        if (requestPath.contains("/inspections") || method.equals("PUT") || method.equals("DELETE")) {
            System.out.println("Autenticação final: " + SecurityContextHolder.getContext().getAuthentication());
            System.out.println("=== Fim SecurityFilter Debug ===");
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.replace("Bearer ", "");
    }
}