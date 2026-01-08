package com.swole.platform.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swole.platform.model.entity.User;
import com.swole.platform.payload.request.LoginRequest;
import com.swole.platform.service.UserService;
import com.swole.platform.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmployeeId("test001");
        testUser.setRoleId(1L);
    }

    @Test
    public void testLoginSuccess() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmployeeId("test001");
        loginRequest.setPassword("password");

        when(userService.findByEmployeeId("test001")).thenReturn(Optional.of(testUser));
        when(jwtUtil.generateToken("test001")).thenReturn("mocked.jwt.token");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value("test001"))
                .andExpect(jsonPath("$.name").value("Test User"));

        verify(userService, times(1)).findByEmployeeId("test001");
        verify(jwtUtil, times(1)).generateToken("test001");
    }

    @Test
    public void testLoginUserNotFound() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmployeeId("nonexistent");
        loginRequest.setPassword("password");

        when(userService.findByEmployeeId("nonexistent")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).findByEmployeeId("nonexistent");
    }

    @Test
    public void testRegisterUser() throws Exception {
        User newUser = new User();
        newUser.setName("New User");
        newUser.setEmployeeId("newuser001");
        newUser.setRoleId(1L);

        when(userService.existsByEmployeeId("newuser001")).thenReturn(false);
        when(userService.createUser(any(User.class))).thenReturn(newUser);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New User"))
                .andExpect(jsonPath("$.employeeId").value("newuser001"));

        verify(userService, times(1)).existsByEmployeeId("newuser001");
        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    public void testRegisterUserAlreadyExists() throws Exception {
        User newUser = new User();
        newUser.setName("New User");
        newUser.setEmployeeId("existing001");
        newUser.setRoleId(1L);

        when(userService.existsByEmployeeId("existing001")).thenReturn(true);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).existsByEmployeeId("existing001");
    }
}