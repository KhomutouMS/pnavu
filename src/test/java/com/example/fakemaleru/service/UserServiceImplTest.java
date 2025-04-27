package com.example.fakemaleru.service;

import com.example.fakemaleru.exceptions.ConflictException;
import com.example.fakemaleru.exceptions.DataNotFound;
import com.example.fakemaleru.exceptions.WrongRequest;
import com.example.fakemaleru.model.User;
import com.example.fakemaleru.repository.UserRepository;
import com.example.fakemaleru.service.impl.UserServiceImpl;
import com.example.fakemaleru.util.CacheUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private UserRepository userRepository;
    private CacheUtil cacheUtil;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        cacheUtil = mock(CacheUtil.class);
        userService = new UserServiceImpl(userRepository, cacheUtil);
    }

    @Test
    void testReadAllUsers() {
        // Arrange
        when(userRepository.findAll()).thenReturn(List.of(new User()));

        // Act
        List<User> users = userService.readAllUsers();

        // Assert
        assertNotNull(users);
        assertEquals(1, users.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testCreateUser_WithNullUser_ShouldThrowWrongRequest() {
        // Act & Assert
        assertThrows(WrongRequest.class, () -> userService.createUser(null));
    }

    @Test
    void testCreateUser_WithEmptyUsername_ShouldThrowWrongRequest() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");

        // Act & Assert
        assertThrows(WrongRequest.class, () -> userService.createUser(user));
    }

    @Test
    void testCreateUser_WithEmptyEmail_ShouldThrowWrongRequest() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");

        // Act & Assert
        assertThrows(WrongRequest.class, () -> userService.createUser(user));
    }

    @Test
    void testCreateUser_WithEmptyPassword_ShouldThrowWrongRequest() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        // Act & Assert
        assertThrows(WrongRequest.class, () -> userService.createUser(user));
    }

    @Test
    void testCreateUser_WithExistingEmail_ShouldThrowConflictException() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");

        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException(""));

        // Act & Assert
        assertThrows(ConflictException.class, () -> userService.createUser(user));
    }

    @Test
    void testCreateUser_Success() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");

        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User createdUser = userService.createUser(user);

        // Assert
        assertNotNull(createdUser);
        assertEquals("testuser", createdUser.getUsername());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateUser_WithNullUser_ShouldThrowWrongRequest() {
        // Act & Assert
        assertThrows(WrongRequest.class, () -> userService.updateUser(null));
    }

    @Test
    void testUpdateUser_WithEmptyUsername_ShouldThrowWrongRequest() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setPassword("password");

        // Act & Assert
        assertThrows(WrongRequest.class, () -> userService.updateUser(user));
    }

    @Test
    void testUpdateUser_WithEmptyPassword_ShouldThrowWrongRequest() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        // Act & Assert
        assertThrows(WrongRequest.class, () -> userService.updateUser(user));
    }

    @Test
    void testUpdateUser_WithInvalidId_ShouldThrowDataNotFound() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password");

        when(userRepository.findUserById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DataNotFound.class, () -> userService.updateUser(user));
    }

    @Test
    void testUpdateUser_Success() {
        // Arrange
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("oldusername");
        existingUser.setPassword("oldpassword");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setUsername("newusername");
        updatedUser.setPassword("newpassword");

        when(userRepository.findUserById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // Act
        User result = userService.updateUser(updatedUser);

        // Assert
        assertEquals("newusername", result.getUsername());
        assertEquals("newpassword", result.getPassword());
        verify(userRepository, times(1)).save(existingUser);
        verify(cacheUtil, times(1)).evict("user_" + existingUser.getId());
    }

    @Test
    void testDeleteUserById_WithInvalidId_ShouldThrowDataNotFound() {
        // Arrange
        when(userRepository.findUserById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DataNotFound.class, () -> userService.deleteUserById(1L));
    }

    @Test
    void testDeleteUserById_Success() {
        // Arrange
        User user = new User();
        user.setId(1L);

        when(userRepository.findUserById(1L)).thenReturn(Optional.of(user));

        // Act
        userService.deleteUserById(1L);

        // Assert
        verify(userRepository, times(1)).delete(user);
        verify(cacheUtil, times(1)).evict("user_" + user.getId());
    }

    @Test
    void testReadUserById_WithCachedUser() {
        // Arrange
        User cachedUser = new User();
        cachedUser.setId(1L);
        when(cacheUtil.get("user_1", User.class)).thenReturn(cachedUser);

        // Act
        User result = userService.readUserById(1L);

        // Assert
        assertEquals(cachedUser, result);
        verify(userRepository, never()).findUserById(anyLong());
    }

    @Test
    void testReadUserById_WithNotCachedUser() {
        // Arrange
        User user = new User();
        user.setId(1L);
        when(cacheUtil.get("user_1", User.class)).thenReturn(null);
        when(userRepository.findUserById(1L)).thenReturn(Optional.of(user));

        // Act
        User result = userService.readUserById(1L);

        // Assert
        assertEquals(user, result);
        verify(cacheUtil, times(1)).put("user_1", user);
    }

    @Test
    void testReadUserById_WithInvalidId_ShouldThrowDataNotFound() {
        // Arrange
        when(cacheUtil.get("user_1", User.class)).thenReturn(null);
        when(userRepository.findUserById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DataNotFound.class, () -> userService.readUserById(1L));
    }

    @Test
    void testCreateUsersBulk_WithNullList_ShouldThrowWrongRequest() {
        // Act & Assert
        assertThrows(WrongRequest.class, () -> userService.createUsersBulk(null));
    }

    @Test
    void testCreateUsersBulk_WithEmptyList_ShouldThrowWrongRequest() {
        // Act & Assert
        assertThrows(WrongRequest.class, () -> userService.createUsersBulk(new ArrayList<>()));
    }

    @Test
    void testCreateUsersBulk_WithInvalidUser_ShouldThrowWrongRequest() {
        // Arrange
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        users.add(user);

        // Act & Assert
        assertThrows(WrongRequest.class, () -> userService.createUsersBulk(users));
    }

    @Test
    void testCreateUsersBulk_WithExistingEmail_ShouldThrowConflictException() {
        // Arrange
        List<User> users = new ArrayList<>();
        User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("test1@example.com");
        user1.setPassword("password");
        users.add(user1);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("test2@example.com");
        user2.setPassword("password");
        users.add(user2);

        when(userRepository.save(user1)).thenThrow(new DataIntegrityViolationException(""));
        when(userRepository.save(user2)).thenReturn(user2);

        // Act & Assert
        assertThrows(ConflictException.class, () -> userService.createUsersBulk(users));
    }

    @Test
    void testCreateUsersBulk_Success() {
        // Arrange
        List<User> users = new ArrayList<>();
        User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("test1@example.com");
        user1.setPassword("password");
        users.add(user1);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("test2@example.com");
        user2.setPassword("password");
        users.add(user2);

        when(userRepository.save(user1)).thenReturn(user1);
        when(userRepository.save(user2)).thenReturn(user2);

        // Act
        List<User> createdUsers = userService.createUsersBulk(users);

        // Assert
        assertEquals(2, createdUsers.size());
        verify(userRepository, times(2)).save(any(User.class));
    }
}