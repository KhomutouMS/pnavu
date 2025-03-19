package com.example.fakemaleru.service;

import com.example.fakemaleru.model.User;
import java.util.List;


public interface UserService {
    @SuppressWarnings("checkstyle:MethodName")
    List<User> findAllUsers();
    @SuppressWarnings("checkstyle:EmptyLineSeparator")
    User saveUser(User newUser);
    @SuppressWarnings("checkstyle:EmptyLineSeparator")
    User updateUser(User newUser);
    @SuppressWarnings("checkstyle:EmptyLineSeparator")
    void deleteUserByEmail(String email);
    @SuppressWarnings("checkstyle:EmptyLineSeparator")
    User findUserByEmail(String email);
    @SuppressWarnings("checkstyle:EmptyLineSeparator")
    List<User> findUserByEmailQuery(String email);
}