package com.example.fakemaleru.service;

import com.example.fakemaleru.model.User;
import java.util.List;


public interface UserService {
    @SuppressWarnings("checkstyle:MethodName")
    List<User> readAllUsers();

    User createUser(User newUser);

    User updateUser(User newUser);

    void deleteUserById(Long id);

    User readUserById(Long id);

    List<User> readCuriousUsers(int questionAmount);

    List<User> readDiscussingUsers(String titleFragment);


}