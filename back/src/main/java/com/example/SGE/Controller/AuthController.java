package com.example.SGE.Controller;

import com.example.SGE.DTO.LoginRequest;
import com.example.SGE.DTO.LoginResponse;
import com.example.SGE.Entity.UserEntity;
import com.example.SGE.Service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    private final java.util.Map<String, io.github.bucket4j.Bucket> cache = new java.util.concurrent.ConcurrentHashMap<>();

    private io.github.bucket4j.Bucket resolveBucket(String key) {
        return cache.computeIfAbsent(key, k -> {
            io.github.bucket4j.Bandwidth limit = io.github.bucket4j.Bandwidth.classic(5, io.github.bucket4j.Refill.intervally(5, java.time.Duration.ofMinutes(1)));
            return io.github.bucket4j.Bucket.builder()
                    .addLimit(limit)
                    .build();
        });
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, jakarta.servlet.http.HttpServletRequest httpRequest) {
        String ip = httpRequest.getRemoteAddr();
        io.github.bucket4j.Bucket bucket = resolveBucket(ip);

        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Muitas tentativas. Tente novamente em 1 minuto.");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
            var userDetails = (UserEntity) authentication.getPrincipal();

            String token = jwtService.generateToken(userDetails);
            Long id = userDetails.getId();
            return ResponseEntity.ok(new LoginResponse(token, id));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials");
        }
    }

    // Endpoint de diagnóstico para verificar o usuário autenticado
    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Não autenticado");
        }
        Map<String, Object> body = new HashMap<>();
        body.put("principal", authentication.getName());
        body.put("authenticated", authentication.isAuthenticated());
        body.put("authorities", authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        return ResponseEntity.ok(body);
    }
}
