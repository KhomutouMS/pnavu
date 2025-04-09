package com.example.fakemaleru.controller;

import com.example.fakemaleru.model.User;
import com.example.fakemaleru.service.UserService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings({"checkstyle:TypeName", "checkstyle:OuterTypeFilename"})
@RestController
@RequestMapping("/fakemailru/users")

@AllArgsConstructor

public class UserController {
    private UserService userService;
    @SuppressWarnings({"checkstyle:EmptyLineSeparator", "checkstyle:WhitespaceAround",
            "checkstyle:MethodName", "checkstyle:Indentation"})
    @GetMapping
    public ResponseEntity<List<User>> readAllUsers(){
        List<User> users = userService.readAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping("save")
    public User saveUser(@RequestBody User newUser) {
        return userService.createUser(newUser);
    }

    @GetMapping("/{email}")
    public User readUserByEmail(@PathVariable String email) {
        return userService.readUserByEmail(email);
    }

    @PutMapping("update")
    public User updateUser(@RequestBody User newUser) {
        return userService.updateUser(newUser);
    }

    @DeleteMapping("delete/{email}")
    public void deleteUser(@PathVariable String email) {
        userService.deleteUserByEmail(email);
    }

    @GetMapping("/saerch")
    public User raedUserByEmailQuery(@RequestParam String email) {
        return userService.readUserByEmail(email);
    }

    @SuppressWarnings("checkstyle:ParameterName")
    @GetMapping("/question_amount")
    public ResponseEntity<List<User>> readCuriousUsers(@RequestParam int question_amount) {
        List<User> users = userService.readCuriousUsers(question_amount);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @SuppressWarnings("checkstyle:ParameterName")
    @GetMapping("/question_id")
    public ResponseEntity<List<User>> readDiscussingUsers(@RequestParam Long question_id) {
        List<User> users = userService.readDiscussingUsers(question_id);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}
