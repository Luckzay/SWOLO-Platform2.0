package com.swole.platform.service;

import com.swole.platform.exception.ResourceNotFoundException;
import com.swole.platform.model.entity.User;
import com.swole.platform.repository.UserRepository;
import com.swole.platform.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl();
        // 使用反射设置userRepository字段
        org.springframework.test.util.ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmployeeId("test001");
        testUser.setRoleId(1L);
    }

    @Test
    public void testGetAllUsers() {
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("Test User", result.get(0).getName());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals("Test User", result.get().getName());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetUserByIdNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(999L);

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    public void testCreateUser() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.createUser(testUser);

        assertNotNull(result);
        assertEquals("Test User", result.getName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testUpdateUser() {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("Updated User");
        updatedUser.setEmployeeId("updated001");
        updatedUser.setRoleId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        User result = userService.updateUser(1L, updatedUser);

        assertNotNull(result);
        assertEquals("Updated User", result.getName());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testUpdateUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            User userToUpdate = new User();
            userToUpdate.setId(999L);
            userService.updateUser(999L, userToUpdate);
        });

        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    public void testDeleteUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.deleteUser(999L);
        });

        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    public void testFindByEmployeeId() {
        when(userRepository.findByEmployeeId("test001")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findByEmployeeId("test001");

        assertTrue(result.isPresent());
        assertEquals("test001", result.get().getEmployeeId());
        verify(userRepository, times(1)).findByEmployeeId("test001");
    }

    @Test
    public void testExistsByEmployeeId() {
        when(userRepository.existsByEmployeeId("test001")).thenReturn(true);

        boolean result = userService.existsByEmployeeId("test001");

        assertTrue(result);
        verify(userRepository, times(1)).existsByEmployeeId("test001");
    }
}