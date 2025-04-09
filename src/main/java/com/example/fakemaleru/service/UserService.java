package com.example.fakemaleru.service;

import com.example.fakemaleru.model.User;
import java.util.List;


public interface UserService {
    @SuppressWarnings("checkstyle:MethodName")
    List<User> readAllUsers();

    User createUser(User newUser);

    User updateUser(User newUser);

    void deleteUserByEmail(String email);

    User readUserByEmail(String email);

    List<User> readCuriousUsers(int questionAmount);

    List<User> readDiscussingUsers(Long questionNumber);
}