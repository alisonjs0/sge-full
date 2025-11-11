package com.example.SGE.Service;

import com.example.SGE.Entity.UserEntity;
import com.example.SGE.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity userRegister(UserEntity newUser) {
        String CripPassword = passwordEncoder.encode(newUser.getSenha());
        newUser.setSenha(CripPassword);

        if (newUser.getRoles() == null || newUser.getRoles().isEmpty()) {
            newUser.setRoles("ROLE_USER");
        }

        return userRepository.save(newUser);
    }
}
