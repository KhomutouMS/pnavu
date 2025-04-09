package com.example.fakemaleru.service.impl;

import com.example.fakemaleru.model.Question;
import com.example.fakemaleru.model.User;
import com.example.fakemaleru.repository.QuestionRepository;
import com.example.fakemaleru.repository.UserRepository;
import com.example.fakemaleru.service.QuestionService;
import com.example.fakemaleru.util.CacheUtil;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
@Primary
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final CacheUtil cacheUtil;
    private UserRepository userRepository;
    private final String userNotFoundMessage = "User not found";

    @Override
    public List<Question> readAllQuestions() {
        return questionRepository.findAll().stream()
                .toList();
    }

    @Override
    public Question readQuestionById(Long id) {
        String cacheKey = "question_" + id;
        Question cachedUser = cacheUtil.get(cacheKey, Question.class);
        if (cachedUser != null) {
            return cachedUser;
        }
        Question question = questionRepository.findQuestionById(id).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, userNotFoundMessage));
        if (question != null) {
            cacheUtil.put(cacheKey, question);
            return question;
        }
        return null;
    }

    @Override
    public Question createQuestion(String userEmail, Question questionNow) {
        User user = userRepository.findUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        questionNow.setUser(user);
        return questionRepository.save(questionNow);
    }

    @Override
    public void deleteQuestionById(Long id) {
        Question question = questionRepository.findQuestionById(id).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, userNotFoundMessage));
        questionRepository.delete(question);
        cacheUtil.delete("question_" + id);
    }

    @Override
    public Question updateQuestion(Question questionNow) {
        Question question = questionRepository.findQuestionById(questionNow.getId()).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, userNotFoundMessage));
        question.setContent(questionNow.getContent());
        question.setTitle(questionNow.getTitle());
        cacheUtil.delete("question_" + question.getId());
        return questionRepository.save(question);
    }
}
