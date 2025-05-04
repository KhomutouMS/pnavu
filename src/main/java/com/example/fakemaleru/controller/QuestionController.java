package com.example.fakemaleru.controller;

import com.example.fakemaleru.model.Question;
import com.example.fakemaleru.service.QuestionService;
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
@RequestMapping("/questions")
@AllArgsConstructor
public class QuestionController {
    private QuestionService questionService;


    @Operation(summary = "Retrieve all questions",
            description = "Fetch a list of all questions from the database.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                description = "Successfully retrieved list of questions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<Question>> readAllQuestions() {
        List<Question> questions = questionService.readAllQuestions();
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }


    @Operation(summary = "Create a new question",
            description = "Create a new question associated with a specific user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Question successfully created"),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SuppressWarnings({"checkstyle:WhitespaceAround", "checkstyle:ParameterName"})
    @PostMapping("save/{user_id}")
    public Question createQuestion(@PathVariable Long user_id, @RequestBody Question question){
        return questionService.createQuestion(user_id, question);
    }


    @Operation(summary = "Find question by ID",
            description = "Retrieve a question from the database by its ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved question"),
        @ApiResponse(responseCode = "404", description = "Question not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SuppressWarnings("checkstyle:WhitespaceAround")
    @GetMapping("find/{id}")
    public Question readQuestionById(@PathVariable Long id){
        return questionService.readQuestionById(id);
    }


    @Operation(summary = "Update an existing question",
            description = "Update question details in the database.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Question successfully updated"),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "404", description = "Question not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SuppressWarnings("checkstyle:WhitespaceAround")
    @PutMapping("update")
    public Question updateQuestion(@RequestBody Question newQuestion){
        return questionService.updateQuestion(newQuestion);
    }


    @Operation(summary = "Delete a question by ID",
            description = "Remove a question from the database using its ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Question successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Question not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SuppressWarnings("checkstyle:WhitespaceAround")
    @DeleteMapping("delete/{id}")
    public void deleteQuestion(@PathVariable Long id){
        questionService.deleteQuestionById(id);
    }

    @Operation(summary = "Create new questions",
            description = "Create multiple questions associated with a specific user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Questions successfully created"),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SuppressWarnings({"checkstyle:WhitespaceAround", "checkstyle:ParameterName"})
    @PostMapping("save/bulk/{user_id}")
    public List<Question> createQuestions(@PathVariable Long user_id,
                                          @RequestBody List<Question> questions) {
        return questionService.createQuestionsBulk(user_id, questions);
    }

}
