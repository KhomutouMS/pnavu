package com.example.fakemaleru.service.impl;

import com.example.fakemaleru.model.User;
import com.example.fakemaleru.repository.InMemoryMailDao;
import com.example.fakemaleru.service.UserService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor

public class InMemoryMailRu implements UserService {
    private final InMemoryMailDao repository;
    @SuppressWarnings("checkstyle:EmptyLineSeparator")
    @Override
    public List<User> findAllUsers() {
        return repository.findAllUsers();
    }

    @Override
    public User saveUser(User newUser) {
        return repository.saveUser(newUser);
    }

    @Override
    public User updateUser(User newUser) {
        return repository.updateUser(newUser);
    }

    @Override
    public void deleteUserByEmail(String email) {
        repository.deleteUserByEmail(email);

    }

    @Override
    public User findUserByEmail(String email) {
        return repository.findUserByEmail(email);
    }
}
