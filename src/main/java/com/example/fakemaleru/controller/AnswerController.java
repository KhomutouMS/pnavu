package com.example.fakemaleru.controller;


import com.example.fakemaleru.model.Answer;
import com.example.fakemaleru.service.AnswerService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/answers")
@AllArgsConstructor

public class AnswerController {
    private AnswerService answerService;


    @Operation(summary = "Retrieve all answers",
            description = "Fetch a list of all answers from the database.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of answers"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<Answer>> readAllAnswers() {
        List<Answer> answers = answerService.readAllAnswers();
        return new ResponseEntity<>(answers, HttpStatus.OK);
    }


    @Operation(summary = "Create a new answer",
            description = "Create a new answer associated with a specific user and question.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Answer successfully created"),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "404", description = "User or question not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SuppressWarnings({"checkstyle:WhitespaceAround", "checkstyle:ParameterName"})
    @PostMapping("save/{user_id}/{question_id}")
    public Answer createAnswer(@PathVariable Long user_id,
                               @PathVariable Long question_id, @RequestBody Answer answer) {
        return answerService.createAnswer(user_id, question_id, answer);
    }


    @Operation(summary = "Find answer by ID",
            description = "Retrieve an answer from the database by its ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved answer"),
        @ApiResponse(responseCode = "404", description = "Answer not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SuppressWarnings("checkstyle:WhitespaceAround")
    @GetMapping("find/{id}")
    public Answer readAnswerById(@PathVariable Long id){
        return answerService.findAnswerById(id);
    }


    @Operation(summary = "Update an existing answer",
            description = "Update answer details in the database.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Answer successfully updated"),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "404", description = "Answer not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SuppressWarnings("checkstyle:WhitespaceAround")
    @PutMapping("update")
    public Answer updateAnswer(@RequestBody Answer newAnswer){
        return answerService.updateAnswer(newAnswer);
    }


    @Operation(summary = "Delete an answer by ID",
            description = "Remove an answer from the database using its ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Answer successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Answer not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SuppressWarnings("checkstyle:WhitespaceAround")
    @DeleteMapping("delete/{id}")
    public void deleteAnswer(@PathVariable Long id){
        answerService.deleteAnswerById(id);
    }

}
