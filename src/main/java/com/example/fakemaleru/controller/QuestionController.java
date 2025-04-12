package com.example.fakemaleru.controller;

import com.example.fakemaleru.model.Question;
import com.example.fakemaleru.service.QuestionService;
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

    @GetMapping
    public ResponseEntity<List<Question>> readAllQuestions() {
        List<Question> questions = questionService.readAllQuestions();
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }

    @SuppressWarnings({"checkstyle:WhitespaceAround", "checkstyle:ParameterName"})
    @PostMapping("save/{user_id}")
    public Question createQuestion(@PathVariable Long user_id, @RequestBody Question question){
        return questionService.createQuestion(user_id, question);
    }

    @SuppressWarnings("checkstyle:WhitespaceAround")
    @GetMapping("/{id}")
    public Question readQuestionById(@PathVariable Long id){
        return questionService.readQuestionById(id);
    }

    @SuppressWarnings("checkstyle:WhitespaceAround")
    @PutMapping("update")
    public Question updateQuestion(@RequestBody Question newQuestion){
        return questionService.updateQuestion(newQuestion);
    }

    @SuppressWarnings("checkstyle:WhitespaceAround")
    @DeleteMapping("delete/{id}")
    public void deleteQuestion(@PathVariable Long id){
        questionService.deleteQuestionById(id);
    }
}
