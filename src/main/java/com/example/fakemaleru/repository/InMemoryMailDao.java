package com.example.fakemaleru.repository;

import com.example.fakemaleru.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@Repository
public class InMemoryMailDao {
    private final List<User> users = new ArrayList<>();
    @SuppressWarnings({"checkstyle:EmptyLineSeparator", "checkstyle:MethodName"})
    public List<User> FindAllUsers() {
        return users;
    }

    public User saveUser(User newUser) {
        users.add(newUser);
        return newUser;
    }

    public User updateUser(User newUser) {
        var studentIndex = IntStream.range(0, users.size())
                .filter(index -> users.get(index).getEmail().equals(newUser.getEmail()))
                .findFirst()
                .orElse(-1);
        if (studentIndex > -1) {
            users.set(studentIndex, newUser);
            return newUser;
        }
        return null;
    }

    @SuppressWarnings({"checkstyle:RightCurly", "checkstyle:WhitespaceAround"})
    @GetMapping("/fakemailru/{email}")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void deleteUserByEmail(@PathVariable String email) {
        var user = findUserByEmail(email);
        if (user != null) {
            users.remove(user);
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @SuppressWarnings({"checkstyle:RightCurly", "checkstyle:WhitespaceAround",
            "checkstyle:SeparatorWrap", "checkstyle:Indentation"})
    public User findUserByEmail(String email) {
        var userNow = users.stream().filter(user -> user.getEmail().equals(email)).findFirst().
                orElse(null);
        if (userNow != null) {
            return userNow;
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        /*return users.stream().filter(user -> user.getEmail().equals(email)).findFirst()
        .orElse(null);*/
    }
}
