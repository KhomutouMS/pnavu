package com.example.fakemaleru.service;

import com.example.fakemaleru.model.Question;
import java.util.List;

public interface QuestionService {
    List<Question> readAllQuestions();

    Question readQuestionById(Long id);

    Question createQuestion(Long userId, Question question);

    void deleteQuestionById(Long id);

    Question updateQuestion(Question question);
}
