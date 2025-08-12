package com.simba.project01;

import com.simba.project01.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService
{
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var u = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾지 못했습니다."));
        var auth = List.of(new SimpleGrantedAuthority("ROLE_" + u.getRole().name()));
        return new LoginUser(u.getId(), u.getUsername(), u.getPassword(), auth);
    }
}
