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

    @SuppressWarnings("checkstyle:WhitespaceAround")
    @PostMapping("save")

    public User saveUser(@RequestBody User newUser){
        return userService.createUser(newUser);
    }

    @SuppressWarnings("checkstyle:WhitespaceAround")
    @GetMapping("/{email}")
    public User readUserByEmail(@PathVariable String email){
        return userService.readUserByEmail(email);
    }

    @SuppressWarnings("checkstyle:WhitespaceAround")
    @PutMapping("update")
    public User updateUser(@RequestBody User newUser){
        return userService.updateUser(newUser);
    }

    @SuppressWarnings("checkstyle:WhitespaceAround")
    @DeleteMapping("delete/{email}")
    public void deleteUser(@PathVariable String email){
        userService.deleteUserByEmail(email);
    }

    @SuppressWarnings("checkstyle:WhitespaceAround")
    @GetMapping("/saerch")
    public User raedUserByEmailQuery(@RequestParam String email){
        return userService.readUserByEmailQuery(email);
    }

}
