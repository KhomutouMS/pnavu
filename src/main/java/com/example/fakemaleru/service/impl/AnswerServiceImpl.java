package com.example.fakemaleru.service.impl;

import com.example.fakemaleru.exceptions.DataNotFound;
import com.example.fakemaleru.exceptions.WrongRequest;
import com.example.fakemaleru.model.Answer;
import com.example.fakemaleru.model.Question;
import com.example.fakemaleru.model.User;
import com.example.fakemaleru.repository.AnswerRepository;
import com.example.fakemaleru.repository.QuestionRepository;
import com.example.fakemaleru.repository.UserRepository;
import com.example.fakemaleru.service.AnswerService;
import com.example.fakemaleru.util.CacheUtil;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Primary
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final CacheUtil cacheUtil;

    @Override
    public List<Answer> readAllAnswers() {
        return answerRepository.findAll().stream()
                .toList();
    }

    @Override
    public Answer createAnswer(Long userId, Long questionId, Answer answerNow) {
        if (answerNow == null) {
            throw new WrongRequest("Your request is empty.");
        }
        Question question = questionRepository.findQuestionById(questionId).orElseThrow(()
                -> new DataNotFound("Question " + questionId + " not found"));
        User user = userRepository.findUserById(userId).orElseThrow(()
                -> new DataNotFound("User " + userId + " not found"));
        answerNow.setQuestion(question);
        answerNow.setUser(user);
        return answerRepository.save(answerNow);
    }

    @Override
    public Answer updateAnswer(Answer answerNow) {
        if (answerNow == null) {
            throw new WrongRequest("Your request is empty.");
        }
        Answer answer = answerRepository.findAnswerById(answerNow.getId()).orElseThrow(()
                        -> new DataNotFound("Answer " + answerNow.getId() + " not found"));
        answer.setContent(answerNow.getContent());
        cacheUtil.evict("answer_" + answer.getId());
        return answerRepository.save(answer);
    }

    @Override
    public void deleteAnswerById(Long id) {
        Answer answer = answerRepository.findAnswerById(id).orElseThrow(()
                -> new DataNotFound("Answer " + id + " not found"));
        answerRepository.delete(answer);
        cacheUtil.evict("answer_" + answer.getId());
    }

    @Override
    public Answer findAnswerById(Long id) {
        String cacheKey = "answer_" + id;
        Answer cachedUser = cacheUtil.get(cacheKey, Answer.class);
        if (cachedUser != null) {
            return cachedUser;
        }
        Answer answer = answerRepository.findAnswerById(id).orElseThrow(()
                -> new DataNotFound("Answer " + id + " not found"));
        if (answer != null) {
            cacheUtil.put(cacheKey, answer);
            return answer;
        }
        return null;
    }
}
