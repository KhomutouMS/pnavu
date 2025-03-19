package com.example.fakemaleru.controller;

import com.example.fakemaleru.model.User;
import com.example.fakemaleru.service.UserService;
import java.util.List;
import lombok.AllArgsConstructor;
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
@RequestMapping("/api/v1/fakemailru")

@AllArgsConstructor

public class UserController {
    private UserService userService;
    @SuppressWarnings({"checkstyle:EmptyLineSeparator", "checkstyle:WhitespaceAround",
            "checkstyle:MethodName", "checkstyle:Indentation"})
    @GetMapping
    public List<User> findAllUsers(){
        return userService.findAllUsers();
    }

    @SuppressWarnings("checkstyle:WhitespaceAround")
    @PostMapping("save_user")

    public User saveUser(@RequestBody User newUser){
        return userService.saveUser(newUser);
    }

    @SuppressWarnings("checkstyle:WhitespaceAround")
    @GetMapping("/{email}")
    public User findUserByEmail(@PathVariable String email){
        return userService.findUserByEmail(email);
    }

    @SuppressWarnings("checkstyle:WhitespaceAround")
    @PutMapping("update_users")
    public User updateUser(@RequestBody User newUser){
        return userService.updateUser(newUser);
    }

    @SuppressWarnings("checkstyle:WhitespaceAround")
    @DeleteMapping("delete_user/{email}")
    public void deleteUser(@PathVariable String email){
        userService.deleteUserByEmail(email);
    }

    @SuppressWarnings("checkstyle:WhitespaceAround")
    @GetMapping("/saerch")
    public List<User> findUserByEmailQuery(@RequestParam String email){
        return userService.findUserByEmailQuery(email);
    }

}
