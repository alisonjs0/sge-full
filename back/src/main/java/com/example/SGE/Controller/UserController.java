package com.example.SGE.Controller;

import com.example.SGE.DTO.RegisterRequestDTO;
import com.example.SGE.DTO.UserResponseDTO;
import com.example.SGE.Entity.UserEntity;
import com.example.SGE.Repository.UserRepository;
import com.example.SGE.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final UserService userService;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> register(@RequestBody @Valid RegisterRequestDTO data) {
        UserEntity newUser = new UserEntity();
        newUser.setNome(data.nome());
        newUser.setEmail(data.email());
        newUser.setCpf(data.cpf());
        newUser.setSenha(data.senha());
        newUser.setRoles(data.roles());
        newUser.setCargo(data.cargo());
        newUser.setCreatedDate(LocalDateTime.now());

        UserEntity saveUser = userService.userRegister(newUser);
        UserResponseDTO responseDTO = new UserResponseDTO(saveUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> findAll() {
        List<UserEntity> userList = userRepository.findAll();
        List<UserResponseDTO> responseList = userList.stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserId(@PathVariable Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        UserResponseDTO userResponse = new UserResponseDTO(userEntity);
        return ResponseEntity.ok(userResponse);
    }
}
