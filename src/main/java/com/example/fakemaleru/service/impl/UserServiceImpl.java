package com.example.fakemaleru.service.impl;

import com.example.fakemaleru.model.User;
import com.example.fakemaleru.repository.AnswerRepository;
import com.example.fakemaleru.repository.QuestionRepository;
import com.example.fakemaleru.repository.UserRepository;
import com.example.fakemaleru.service.UserService;
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
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final String userNotFoundMessage = "User not found";

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
        User user = userRepository.findUserByEmail(newUser.getEmail()).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, userNotFoundMessage));
        user.setUsername(newUser.getUsername());
        user.setPassword(newUser.getPassword());
        return userRepository.save(user);
    }

    @SuppressWarnings("checkstyle:Indentation")
    @Override
    public void deleteUserByEmail(String email) {

        User user = userRepository.findUserByEmail(email).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, userNotFoundMessage));
        userRepository.delete(user);
    }


    @Override
    public User readUserByEmail(String email) {
        return userRepository.findUserByEmail(email).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, userNotFoundMessage));
    }

    @Override
    public User readUserByEmailQuery(String email) {
        return userRepository.findUserByEmail(email).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, userNotFoundMessage));

    }
}
