package com.simba.project01.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService
{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    //회원가입
    @Transactional
    public void register(String username, String password, String email, UserRole role)
    {
        if(userRepository.existsUserByUsername(username))
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        if(userRepository.existsUserByEmail(email))
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");

        String encodedPassword = passwordEncoder.encode(password);

        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);
        user.setEmail(email);
        user.setRole(role);
        userRepository.save(user);
    }

}
