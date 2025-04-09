package com.example.fakemaleru.service.impl;

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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
@Primary
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final String dataNotFoundMessage = "Data not found";
    private final UserRepository userRepository;
    private final CacheUtil cacheUtil;

    @Override
    public List<Answer> readAllAnswers() {
        return answerRepository.findAll().stream()
                .toList();
    }

    @Override
    public Answer createAnswer(String userEmail, Long questionId, Answer answerNow) {
        Question question = questionRepository.findQuestionById(questionId).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, dataNotFoundMessage));
        User user = userRepository.findUserByEmail(userEmail).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, dataNotFoundMessage));
        answerNow.setQuestion(question);
        answerNow.setUser(user);
        return answerRepository.save(answerNow);
    }

    @Override
    public Answer updateAnswer(Answer answerNow) {
        Answer answer = answerRepository.findAnswerById(answerNow.getId()).orElseThrow(()
                        -> new ResponseStatusException(HttpStatus.NOT_FOUND, dataNotFoundMessage));
        answer.setContent(answerNow.getContent());
        cacheUtil.delete("answer_" + answer.getId());
        return answerRepository.save(answer);
    }

    @Override
    public void deleteAnswerById(Long id) {
        Answer answer = answerRepository.findAnswerById(id).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, dataNotFoundMessage));
        answerRepository.delete(answer);
        cacheUtil.delete("answer_" + answer.getId());
    }

    @Override
    public Answer findAnswerById(Long id) {
        String cacheKey = "answer_" + id;
        Answer cachedUser = cacheUtil.get(cacheKey, Answer.class);
        if (cachedUser != null) {
            return cachedUser;
        }
        Answer answer = answerRepository.findAnswerById(id).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, dataNotFoundMessage));
        if (answer != null) {
            cacheUtil.put(cacheKey, answer);
            return answer;
        }
        return null;
    }
}
