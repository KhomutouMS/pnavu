package com.example.fakemaleru.controller;


import com.example.fakemaleru.model.Answer;
import com.example.fakemaleru.service.AnswerService;
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
@RequestMapping("/fakemailru/answers")
@AllArgsConstructor

public class AnswerController {
    private AnswerService answerService;

    @GetMapping
    public ResponseEntity<List<Answer>> readAllAnswers() {
        List<Answer> answers = answerService.readAllAnswers();
        return new ResponseEntity<>(answers, HttpStatus.OK);
    }

    @SuppressWarnings({"checkstyle:WhitespaceAround", "checkstyle:ParameterName"})
    @PostMapping("save/{user_email}/{question_id}")
    public Answer createAnswer(@PathVariable String user_email,
                               @PathVariable Long question_id, @RequestBody Answer answer) {
        return answerService.createAnswer(user_email, question_id, answer);
    }

    @SuppressWarnings("checkstyle:WhitespaceAround")
    @GetMapping("/{id}")
    public Answer readAnswerById(@PathVariable Long id){
        return answerService.findAnswerById(id);
    }

    @SuppressWarnings("checkstyle:WhitespaceAround")
    @PutMapping("update")
    public Answer updateAnswer(@RequestBody Answer newAnswer){
        return answerService.updateAnswer(newAnswer);
    }

    @SuppressWarnings("checkstyle:WhitespaceAround")
    @DeleteMapping("delete/{id}")
    public void deleteAnswer(@PathVariable Long id){
        answerService.deleteAnswerById(id);
    }
}
