package com.swole.platform.controller.auth;

import com.swole.platform.model.entity.User;
import com.swole.platform.payload.request.LoginRequest;
import com.swole.platform.payload.request.RegisterRequest;
import com.swole.platform.payload.response.JwtResponse;
import com.swole.platform.security.UserDetailsImpl;
import com.swole.platform.service.UserService;
import com.swole.platform.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        // 仅通过员工ID验证用户是否存在
        User user = userService.findByEmployeeId(loginRequest.getEmployeeId())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("Error: Employee ID not found!");
        }

        // 生成JWT token
        String jwt = jwtUtil.generateToken(loginRequest.getEmployeeId());

        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        return ResponseEntity.ok(new JwtResponse(jwt, user.getId(), user.getEmployeeId(), user.getName(), roles));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest signUpRequest) {
        if (userService.existsByEmployeeId(signUpRequest.getEmployeeId())) {
            return ResponseEntity.badRequest().body("Error: Employee ID is already taken!");
        }

        // 创建新用户账户
        User user = new User();
        user.setName(signUpRequest.getName());
        user.setEmployeeId(signUpRequest.getEmployeeId());
        user.setRoleId(signUpRequest.getRoleId());
        // 实际应用中应加密密码
        // user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        User savedUser = userService.createUser(user);

        return ResponseEntity.ok(savedUser);
    }
}