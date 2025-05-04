package com.example.fakemaleru.controller;

import com.example.fakemaleru.exceptions.WrongRequest;
import com.example.fakemaleru.model.User;
import com.example.fakemaleru.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

@RestController
@RequestMapping("/users")

@AllArgsConstructor

public class UserController {
    private UserService userService;

    @Operation(summary = "Retrieve all users",
            description = "Fetch a list of all users from the database.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved list of users"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    @GetMapping
    public ResponseEntity<List<User>> readAllUsers() {
        List<User> users = userService.readAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }


    @Operation(summary = "Post new user",
            description =
                    "Create new user and save it to database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "409", description = "Email conflict"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("save")
    public User saveUser(@RequestBody User newUser) {
        if (newUser == null) {
            throw new WrongRequest("Your request is empty.");
        }
        if (newUser.getUsername() == null) {
            throw new WrongRequest("Your username is empty.");
        }
        if (newUser.getEmail() == null) {
            throw new WrongRequest("Your Email is empty.");
        }
        if (!isValidEmail(newUser.getEmail())) {
            throw new WrongRequest("Your Email is not valid.");
        }
        if (newUser.getPassword() == null) {
            throw new WrongRequest("Your password is empty.");
        }
        return userService.createUser(newUser);
    }

    boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return email.matches(emailRegex);
    }

    @Operation(summary = "Find user by ID",
            description = "Retrieve a user from the database by their ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("find/{id}")
    public User readUserById(@PathVariable Long id) {
        return userService.readUserById(id);
    }


    @Operation(summary = "Update an existing user",
            description = "Update user details in the database.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User successfully updated"),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("update")
    public User updateUser(@RequestBody User newUser) {
        return userService.updateUser(newUser);
    }


    @Operation(summary = "Delete a user by ID",
            description = "Remove a user from the database using their ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User successfully deleted"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("delete/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
    }


    @Operation(summary = "Find user by email",
            description = "Retrieve a user from the database using their email.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/saerch")
    public User raedUserByEmailQuery(@RequestParam Long id) {
        return userService.readUserById(id);
    }


    @Operation(summary = "Retrieve curious users",
            description = "Fetch users based on the number of questions they have.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                description = "Successfully retrieved list of curious users"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SuppressWarnings("checkstyle:ParameterName")
    @GetMapping("/question_amount")
    public ResponseEntity<List<User>> readCuriousUsers(@RequestParam int question_amount) {
        List<User> users = userService.readCuriousUsers(question_amount);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }


    @Operation(summary = "Retrieve discussing users",
            description = "Fetch users who are discussing a specific title.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                description = "Successfully retrieved list of discussing users"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SuppressWarnings("checkstyle:ParameterName")
    @GetMapping("/title")
    public ResponseEntity<List<User>> readDiscussingUsers(@RequestParam String title) {
        List<User> users = userService.readDiscussingUsers(title);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    
}
