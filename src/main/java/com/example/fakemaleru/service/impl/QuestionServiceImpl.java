package com.example.fakemaleru.service.impl;

import com.example.fakemaleru.exceptions.DataNotFound;
import com.example.fakemaleru.exceptions.WrongRequest;
import com.example.fakemaleru.model.Question;
import com.example.fakemaleru.model.User;
import com.example.fakemaleru.repository.QuestionRepository;
import com.example.fakemaleru.repository.UserRepository;
import com.example.fakemaleru.service.QuestionService;
import com.example.fakemaleru.util.CacheUtil;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Primary
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final CacheUtil cacheUtil;
    private UserRepository userRepository;

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
                -> new DataNotFound("Question " + id + " not found"));
        if (question != null) {
            cacheUtil.put(cacheKey, question);
            return question;
        }
        return null;
    }

    @Override
    public Question createQuestion(Long userId, Question questionNow) {
        if (questionNow == null) {
            throw new WrongRequest("Your request is empty.");
        }
        if (questionNow.getContent() == null) {
            throw new WrongRequest("Your content is empty.");
        }
        if (questionNow.getTitle() == null) {
            throw new WrongRequest("Your title is empty.");
        }
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new DataNotFound("User " + userId + " not found"));
        questionNow.setUser(user);
        return questionRepository.save(questionNow);
    }

    @Override
    public void deleteQuestionById(Long id) {
        Question question = questionRepository.findQuestionById(id).orElseThrow(()
                -> new DataNotFound("Question " + id + " not found"));
        questionRepository.delete(question);
        cacheUtil.evict("question_" + id);
    }

    @Override
    public Question updateQuestion(Question questionNow) {
        if (questionNow == null) {
            throw new WrongRequest("Your request is empty.");
        }
        if (questionNow.getContent() == null) {
            throw new WrongRequest("Your content is empty.");
        }
        if (questionNow.getTitle() == null) {
            throw new WrongRequest("Your title is empty.");
        }
        Question question = questionRepository.findQuestionById(questionNow.getId()).orElseThrow(()
                -> new DataNotFound("Question " + questionNow.getId() + " not found"));
        question.setContent(questionNow.getContent());
        question.setTitle(questionNow.getTitle());
        cacheUtil.evict("question_" + question.getId());
        return questionRepository.save(question);
    }

    @Override
    public List<Question> createQuestionsBulk(Long userId, List<Question> questions) {
        if (questions == null || questions.isEmpty()) {
            throw new WrongRequest("Your request is empty.");
        }

        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new DataNotFound("User " + userId + " not found"));

        List<Question> validQuestions = questions.stream()
                .peek(question -> {
                    if (question.getContent() == null) {
                        throw new WrongRequest("Content of question is empty.");
                    }
                    if (question.getTitle() == null) {
                        throw new WrongRequest("Title of question is empty.");
                    }
                    question.setUser(user);
                })
                .toList();


        return validQuestions.stream()
                .map(questionRepository::save)
                .collect(Collectors.toList());
    }
}
