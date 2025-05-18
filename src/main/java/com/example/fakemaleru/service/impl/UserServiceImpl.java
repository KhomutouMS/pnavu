package com.example.fakemaleru.service.impl;

import com.example.fakemaleru.exceptions.ConflictException;
import com.example.fakemaleru.exceptions.DataNotFound;
import com.example.fakemaleru.exceptions.WrongRequest;
import com.example.fakemaleru.model.Answer;
import com.example.fakemaleru.model.Question;
import com.example.fakemaleru.model.User;
import com.example.fakemaleru.repository.UserRepository;
import com.example.fakemaleru.service.UserService;
import com.example.fakemaleru.util.CacheUtil;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
@Primary
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CacheUtil cacheUtil;

    @Override
    public List<User> readAllUsers() {
        return userRepository.findAll().stream()
                .toList();
    }

    @Override
    public User createUser(User newUser) {
        if (newUser.getUsername() == null || newUser.getUsername().length() < 3
                || newUser.getUsername().length() > 18) {
            throw new WrongRequest("Username must be between 3 and 18 characters.");
        }
        try {
            return userRepository.save(newUser);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Email " + newUser.getEmail() + " already exists.");
        }
    }


    @Override
    public User updateUser(User newUser) {
        if (newUser == null) {
            throw new WrongRequest("Your request is empty.");
        }
        if (newUser.getUsername() == null) {
            throw new WrongRequest("Your username is empty.");
        }
        if (newUser.getPassword() == null) {
            throw new WrongRequest("Your password is empty.");
        }
        User user = userRepository.findUserById(newUser.getId()).orElseThrow(()
                -> new DataNotFound("User " + newUser.getId() + " not found"));
        if (newUser.getUsername() == null || newUser.getUsername().length() < 3
                || newUser.getUsername().length() > 18) {
            throw new WrongRequest("Username must be between 3 and 18 characters.");
        }
        user.setUsername(newUser.getUsername());
        user.setPassword(newUser.getPassword());
        for (Question question : user.getQuestions()) {
            question.setUsername(user.getUsername()); // Устанавливаем пользователя для вопроса
            // Если нужно обновить имя в вопросе, добавьте соответствующее поле
        }

        // Обновляем все связанные ответы
        for (Answer answer : user.getAnswers()) {
            answer.setUsername(user.getUsername()); // Устанавливаем пользователя для ответа
            // Если нужно обновить имя в ответе, добавьте соответствующее поле
        }
        cacheUtil.evict("user_" + user.getId());
        return userRepository.save(user);
    }

    @Override
    public void deleteUserById(Long id) {

        User user = userRepository.findUserById(id).orElseThrow(()
                -> new DataNotFound("User " + id + " not found"));
        userRepository.delete(user);
        cacheUtil.evict("user_" + id);
    }


    @Override
    public User readUserById(Long id) {
        String cacheKey = "user_" + id;
        User cachedUser = cacheUtil.get(cacheKey, User.class);
        if (cachedUser != null) {
            return cachedUser;
        }
        User user = userRepository.findUserById(id).orElseThrow(()
                -> new DataNotFound("User " + id + " not found"));
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
