package com.example.SGE.Controller;

import com.example.SGE.Entity.UserEntity;
import com.example.SGE.Repository.UserRepository;
import com.example.SGE.Service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/token-infinito")
public class TokenTestController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{id}")
    public ResponseEntity<String> gerarTokenInfinito(@PathVariable Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        String token = jwtService.generateInfiniteToken(user);
        return ResponseEntity.ok(token);
    }
}
