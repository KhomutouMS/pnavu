package com.example.fakemaleru.service;

import com.example.fakemaleru.model.Answer;
import java.util.List;

public interface AnswerService {
    List<Answer> readAllAnswers();

    Answer createAnswer(Long userId, Long questionId, Answer answer);

    Answer updateAnswer(Answer answer);

    void deleteAnswerById(Long id);

    Answer findAnswerById(Long id);
}
