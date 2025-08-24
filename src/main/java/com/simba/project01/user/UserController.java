package com.simba.project01.user;

import com.simba.project01.LoginUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/itda")
public class UserController
{
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    // 로그인 세션 기반
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());

        Authentication auth = authenticationManager.authenticate(token);

        SecurityContextHolder.getContext().setAuthentication(auth);

        HttpSession session = request.getSession(true);
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );
        return ResponseEntity.ok().build();
    }

    //회원 가입
    @PostMapping("/auth/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest registerRequest) {
        userService.register(registerRequest.getUsername(), registerRequest.getPassword(), registerRequest.getEmail(), registerRequest.getRole());
        return ResponseEntity.noContent().build();
    }

    //회원 정보 반환
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal LoginUser loginUser) {
        Long userId = loginUser.getId();
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));
        return ResponseEntity.ok(new UserResponse(user));
    }
}
