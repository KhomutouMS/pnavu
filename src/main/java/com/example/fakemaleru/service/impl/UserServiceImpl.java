package com.example.fakemaleru.service.impl;

import com.example.fakemaleru.model.User;
import com.example.fakemaleru.repository.UserRepository;
import com.example.fakemaleru.service.UserService;
import com.example.fakemaleru.util.CacheUtil;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
@AllArgsConstructor
@Primary
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final String userNotFoundMessage = "User not found";
    private final CacheUtil cacheUtil;

    @Override
    public List<User> readAllUsers() {
        return userRepository.findAll().stream()
                .toList();
    }

    @Override
    public User createUser(User newUser) {
        return userRepository.save(newUser);
    }

    @Override
    public User updateUser(User newUser) {
        User user = userRepository.findUserById(newUser.getId()).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, userNotFoundMessage));
        user.setUsername(newUser.getUsername());
        user.setPassword(newUser.getPassword());
        cacheUtil.delete("user_" + user.getId());
        return userRepository.save(user);
    }

    @SuppressWarnings("checkstyle:Indentation")
    @Override
    public void deleteUserById(Long id) {

        User user = userRepository.findUserById(id).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, userNotFoundMessage));
        userRepository.delete(user);
        cacheUtil.delete("user_" + id);
    }


    @Override
    public User readUserById(Long id) {
        String cacheKey = "user_" + id;
        User cachedUser = cacheUtil.get(cacheKey, User.class);
        if (cachedUser != null) {
            return cachedUser;
        }
        User user = userRepository.findUserById(id).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, userNotFoundMessage));
        if (user != null) {
            cacheUtil.put(cacheKey, user);
            return user;
        }
        return null;
    }


    @Override
    public List<User> readCuriousUsers(int questionAmount) {
        return userRepository.findCuriousUsers(questionAmount).stream()
                .toList();
    }

    @Override
    public List<User> readDiscussingUsers(String titleFragment) {
        return userRepository.findDiscussingUsers(titleFragment).stream()
                .toList();
    }
}
