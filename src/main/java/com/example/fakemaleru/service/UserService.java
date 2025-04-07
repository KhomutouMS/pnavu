package com.example.fakemaleru.service;

import com.example.fakemaleru.model.User;
import java.util.List;


public interface UserService {
    @SuppressWarnings("checkstyle:MethodName")
    List<User> readAllUsers();
    @SuppressWarnings("checkstyle:EmptyLineSeparator")
    User createUser(User newUser);
    @SuppressWarnings("checkstyle:EmptyLineSeparator")
    User updateUser(User newUser);
    @SuppressWarnings("checkstyle:EmptyLineSeparator")
    void deleteUserByEmail(String email);
    @SuppressWarnings("checkstyle:EmptyLineSeparator")
    User readUserByEmail(String email);
    @SuppressWarnings("checkstyle:EmptyLineSeparator")
    User readUserByEmailQuery(String email);
}