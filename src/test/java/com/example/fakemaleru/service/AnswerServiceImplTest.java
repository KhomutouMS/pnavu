package com.example.fakemaleru.service;

import com.example.fakemaleru.exceptions.DataNotFound;
import com.example.fakemaleru.exceptions.WrongRequest;
import com.example.fakemaleru.model.Answer;
import com.example.fakemaleru.model.Question;
import com.example.fakemaleru.model.User;
import com.example.fakemaleru.repository.AnswerRepository;
import com.example.fakemaleru.repository.QuestionRepository;
import com.example.fakemaleru.repository.UserRepository;
import com.example.fakemaleru.service.impl.AnswerServiceImpl;
import com.example.fakemaleru.util.CacheUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AnswerServiceImplTest {

    private AnswerRepository answerRepository;
    private QuestionRepository questionRepository;
    private UserRepository userRepository;
    private CacheUtil cacheUtil;
    private AnswerServiceImpl answerService;

    @BeforeEach
    void setUp() {
        answerRepository = mock(AnswerRepository.class);
        questionRepository = mock(QuestionRepository.class);
        userRepository = mock(UserRepository.class);
        cacheUtil = mock(CacheUtil.class);
        answerService = new AnswerServiceImpl(answerRepository, questionRepository, userRepository, cacheUtil);
    }

    @Test
    void testReadAllAnswers() {
        // Arrange
        when(answerRepository.findAll()).thenReturn(List.of(new Answer()));

        // Act
        List<Answer> answers = answerService.readAllAnswers();

        // Assert
        assertNotNull(answers);
        assertEquals(1, answers.size());
        verify(answerRepository, times(1)).findAll();
    }

    @Test
    void testCreateAnswer_WithNullAnswer_ShouldThrowWrongRequest() {
        // Act & Assert
        assertThrows(WrongRequest.class, () -> answerService.createAnswer(1L, 1L, null));
    }

    @Test
    void testCreateAnswer_WithEmptyContent_ShouldThrowWrongRequest() {
        // Arrange
        Answer answer = new Answer();
        // Act & Assert
        assertThrows(WrongRequest.class, () -> answerService.createAnswer(1L, 1L, answer));
    }

    @Test
    void testCreateAnswer_WithInvalidQuestion_ShouldThrowDataNotFound() {
        // Arrange
        Answer answer = new Answer();
        answer.setContent("Some content");

        when(questionRepository.findQuestionById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DataNotFound.class, () -> answerService.createAnswer(1L, 1L, answer));
    }

    @Test
    void testCreateAnswer_WithInvalidUser_ShouldThrowDataNotFound() {
        // Arrange
        Answer answer = new Answer();
        answer.setContent("Some content");
        Question question = new Question();

        when(questionRepository.findQuestionById(1L)).thenReturn(Optional.of(question));
        when(userRepository.findUserById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DataNotFound.class, () -> answerService.createAnswer(1L, 1L, answer));
    }

    @Test
    void testCreateAnswer_Success() {
        // Arrange
        Answer answer = new Answer();
        answer.setContent("Some content");
        Question question = new Question();
        User user = new User();

        when(questionRepository.findQuestionById(1L)).thenReturn(Optional.of(question));
        when(userRepository.findUserById(1L)).thenReturn(Optional.of(user));
        when(answerRepository.save(any(Answer.class))).thenReturn(answer);

        // Act
        Answer createdAnswer = answerService.createAnswer(1L, 1L, answer);

        // Assert
        assertNotNull(createdAnswer);
        assertEquals("Some content", createdAnswer.getContent());
        verify(answerRepository, times(1)).save(answer);
        assertEquals(question, createdAnswer.getQuestion());
        assertEquals(user, createdAnswer.getUser());
    }

    @Test
    void testUpdateAnswer_WithNullAnswer_ShouldThrowWrongRequest() {
        // Act & Assert
        assertThrows(WrongRequest.class, () -> answerService.updateAnswer(null));
    }

    @Test
    void testUpdateAnswer_WithEmptyContent_ShouldThrowWrongRequest() {
        // Arrange
        Answer answer = new Answer();
        answer.setId(1L);

        // Act & Assert
        assertThrows(WrongRequest.class, () -> answerService.updateAnswer(answer));
    }

    @Test
    void testUpdateAnswer_WithInvalidId_ShouldThrowDataNotFound() {
        // Arrange
        Answer answer = new Answer();
        answer.setId(1L);
        answer.setContent("Updated content");

        when(answerRepository.findAnswerById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DataNotFound.class, () -> answerService.updateAnswer(answer));
    }

    @Test
    void testUpdateAnswer_Success() {
        // Arrange
        Answer existingAnswer = new Answer();
        existingAnswer.setId(1L);
        existingAnswer.setContent("Old content");

        Answer updatedAnswer = new Answer();
        updatedAnswer.setId(1L);
        updatedAnswer.setContent("Updated content");

        when(answerRepository.findAnswerById(1L)).thenReturn(Optional.of(existingAnswer));
        when(answerRepository.save(any(Answer.class))).thenReturn(updatedAnswer);

        // Act
        Answer result = answerService.updateAnswer(updatedAnswer);

        // Assert
        assertEquals("Updated content", result.getContent());
        verify(answerRepository, times(1)).save(existingAnswer);
        verify(cacheUtil, times(1)).evict("answer_1");
    }

    @Test
    void testDeleteAnswerById_WithInvalidId_ShouldThrowDataNotFound() {
        // Arrange
        when(answerRepository.findAnswerById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DataNotFound.class, () -> answerService.deleteAnswerById(1L));
    }

    @Test
    void testDeleteAnswerById_Success() {
        // Arrange
        Answer answer = new Answer();
        answer.setId(1L);

        when(answerRepository.findAnswerById(1L)).thenReturn(Optional.of(answer));

        // Act
        answerService.deleteAnswerById(1L);

        // Assert
        verify(answerRepository, times(1)).delete(answer);
        verify(cacheUtil, times(1)).evict("answer_1");
    }

    @Test
    void testFindAnswerById_WithCachedAnswer() {
        // Arrange
        Answer cachedAnswer = new Answer();
        cachedAnswer.setId(1L);
        when(cacheUtil.get("answer_1", Answer.class)).thenReturn(cachedAnswer);

        // Act
        Answer result = answerService.findAnswerById(1L);

        // Assert
        assertEquals(cachedAnswer, result);
        verify(answerRepository, never()).findAnswerById(anyLong());
    }

    @Test
    void testFindAnswerById_WithNotCachedAnswer() {
        // Arrange
        Answer answer = new Answer();
        answer.setId(1L);
        when(cacheUtil.get("answer_1", Answer.class)).thenReturn(null);
        when(answerRepository.findAnswerById(1L)).thenReturn(Optional.of(answer));

        // Act
        Answer result = answerService.findAnswerById(1L);

        // Assert
        assertEquals(answer, result);
        verify(cacheUtil, times(1)).put("answer_1", answer);
    }

    @Test
    void testFindAnswerById_WithInvalidId_ShouldThrowDataNotFound() {
        // Arrange
        when(cacheUtil.get("answer_1", Answer.class)).thenReturn(null);
        when(answerRepository.findAnswerById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DataNotFound.class, () -> answerService.findAnswerById(1L));
    }
}