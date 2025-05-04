package com.example.fakemaleru.service;

import com.example.fakemaleru.exceptions.DataNotFound;
import com.example.fakemaleru.exceptions.WrongRequest;
import com.example.fakemaleru.model.Question;
import com.example.fakemaleru.model.User;
import com.example.fakemaleru.repository.QuestionRepository;
import com.example.fakemaleru.repository.UserRepository;
import com.example.fakemaleru.service.impl.QuestionServiceImpl;
import com.example.fakemaleru.util.CacheUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class QuestionServiceImplTest {

    private QuestionRepository questionRepository;
    private UserRepository userRepository;
    private CacheUtil cacheUtil;
    private QuestionServiceImpl questionService;

    @BeforeEach
    void setUp() {
        questionRepository = mock(QuestionRepository.class);
        userRepository = mock(UserRepository.class);
        cacheUtil = mock(CacheUtil.class);
        questionService = new QuestionServiceImpl(questionRepository, cacheUtil, userRepository);
    }

    @Test
    void testReadAllQuestions() {
        // Arrange
        when(questionRepository.findAll()).thenReturn(List.of(new Question()));

        // Act
        List<Question> questions = questionService.readAllQuestions();

        // Assert
        assertNotNull(questions);
        assertEquals(1, questions.size());
        verify(questionRepository, times(1)).findAll();
    }

    @Test
    void testReadQuestionById_WithCachedQuestion() {
        // Arrange
        Question cachedQuestion = new Question();
        cachedQuestion.setId(1L);
        when(cacheUtil.get("question_1", Question.class)).thenReturn(cachedQuestion);

        // Act
        Question result = questionService.readQuestionById(1L);

        // Assert
        assertEquals(cachedQuestion, result);
        verify(questionRepository, never()).findQuestionById(anyLong());
    }

    @Test
    void testReadQuestionById_WithNotCachedQuestion() {
        // Arrange
        Question question = new Question();
        question.setId(1L);
        when(cacheUtil.get("question_1", Question.class)).thenReturn(null);
        when(questionRepository.findQuestionById(1L)).thenReturn(Optional.of(question));

        // Act
        Question result = questionService.readQuestionById(1L);

        // Assert
        assertEquals(question, result);
        verify(cacheUtil, times(1)).put("question_1", question);
    }

    @Test
    void testReadQuestionById_WithInvalidId_ShouldThrowDataNotFound() {
        // Arrange
        when(cacheUtil.get("question_1", Question.class)).thenReturn(null);
        when(questionRepository.findQuestionById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DataNotFound.class, () -> questionService.readQuestionById(1L));
    }

    @Test
    void testCreateQuestion_WithNullQuestion_ShouldThrowWrongRequest() {
        // Act & Assert
        assertThrows(WrongRequest.class, () -> questionService.createQuestion(1L, null));
    }

    @Test
    void testCreateQuestion_WithEmptyContent_ShouldThrowWrongRequest() {
        // Arrange
        Question question = new Question();
        question.setTitle("Some Title");

        // Act & Assert
        assertThrows(WrongRequest.class, () -> questionService.createQuestion(1L, question));
    }

    @Test
    void testCreateQuestion_WithEmptyTitle_ShouldThrowWrongRequest() {
        // Arrange
        Question question = new Question();
        question.setContent("Some content");

        // Act & Assert
        assertThrows(WrongRequest.class, () -> questionService.createQuestion(1L, question));
    }

    @Test
    void testCreateQuestion_WithInvalidUser_ShouldThrowDataNotFound() {
        // Arrange
        Question question = new Question();
        question.setContent("Some content");
        question.setTitle("Some Title");

        when(userRepository.findUserById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DataNotFound.class, () -> questionService.createQuestion(1L, question));
    }

    @Test
    void testCreateQuestion_Success() {
        // Arrange
        Question question = new Question();
        question.setContent("Some content");
        question.setTitle("Some Title");
        User user = new User();

        when(userRepository.findUserById(1L)).thenReturn(Optional.of(user));
        when(questionRepository.save(any(Question.class))).thenReturn(question);

        // Act
        Question createdQuestion = questionService.createQuestion(1L, question);

        // Assert
        assertNotNull(createdQuestion);
        assertEquals("Some content", createdQuestion.getContent());
        assertEquals("Some Title", createdQuestion.getTitle());
        verify(questionRepository, times(1)).save(question);
    }

    @Test
    void testDeleteQuestionById_WithInvalidId_ShouldThrowDataNotFound() {
        // Arrange
        when(questionRepository.findQuestionById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DataNotFound.class, () -> questionService.deleteQuestionById(1L));
    }

    @Test
    void testDeleteQuestionById_Success() {
        // Arrange
        Question question = new Question();
        question.setId(1L);

        when(questionRepository.findQuestionById(1L)).thenReturn(Optional.of(question));

        // Act
        questionService.deleteQuestionById(1L);

        // Assert
        verify(questionRepository, times(1)).delete(question);
        verify(cacheUtil, times(1)).evict("question_1");
    }

    @Test
    void testUpdateQuestion_WithNullQuestion_ShouldThrowWrongRequest() {
        // Act & Assert
        assertThrows(WrongRequest.class, () -> questionService.updateQuestion(null));
    }

    @Test
    void testUpdateQuestion_WithEmptyContent_ShouldThrowWrongRequest() {
        // Arrange
        Question question = new Question();
        question.setTitle("Some Title");

        // Act & Assert
        assertThrows(WrongRequest.class, () -> questionService.updateQuestion(question));
    }

    @Test
    void testUpdateQuestion_WithEmptyTitle_ShouldThrowWrongRequest() {
        // Arrange
        Question question = new Question();
        question.setContent("Some content");

        // Act & Assert
        assertThrows(WrongRequest.class, () -> questionService.updateQuestion(question));
    }

    @Test
    void testUpdateQuestion_WithInvalidId_ShouldThrowDataNotFound() {
        // Arrange
        Question question = new Question();
        question.setId(1L);
        question.setContent("Updated content");
        question.setTitle("Updated Title");

        when(questionRepository.findQuestionById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DataNotFound.class, () -> questionService.updateQuestion(question));
    }

    @Test
    void testUpdateQuestion_Success() {
        // Arrange
        Question existingQuestion = new Question();
        existingQuestion.setId(1L);
        existingQuestion.setContent("Old content");
        existingQuestion.setTitle("Old Title");

        Question updatedQuestion = new Question();
        updatedQuestion.setId(1L);
        updatedQuestion.setContent("Updated content");
        updatedQuestion.setTitle("Updated Title");

        when(questionRepository.findQuestionById(1L)).thenReturn(Optional.of(existingQuestion));
        when(questionRepository.save(any(Question.class))).thenReturn(updatedQuestion);

        // Act
        Question result = questionService.updateQuestion(updatedQuestion);

        // Assert
        assertEquals("Updated content", result.getContent());
        assertEquals("Updated Title", result.getTitle());
        verify(questionRepository, times(1)).save(existingQuestion);
        verify(cacheUtil, times(1)).evict("question_1");
    }
    @Test
    void testCreateQuestions_WithNullQuestions_ShouldThrowWrongRequest() {
        // Act & Assert
        assertThrows(WrongRequest.class, () -> questionService.createQuestionsBulk(1L, null));
    }

    @Test
    void testCreateQuestions_WithEmptyList_ShouldThrowWrongRequest() {
        // Arrange
        List<Question> questions = List.of();

        // Act & Assert
        WrongRequest exception = assertThrows(WrongRequest.class, () ->
                questionService.createQuestionsBulk(1L, questions)
        );
        assertEquals("Your request is empty.", exception.getMessage());
    }

    @Test
    void testCreateQuestions_WithInvalidUser_ShouldThrowDataNotFound() {
        // Arrange
        List<Question> questions = List.of(new Question());

        when(userRepository.findUserById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        DataNotFound exception = assertThrows(DataNotFound.class, () ->
                questionService.createQuestionsBulk(1L, questions)
        );
        assertEquals("User 1 not found", exception.getMessage());
    }

    @Test
    void testCreateQuestions_WithNullContent_ShouldThrowWrongRequest() {
        // Arrange
        Question question = new Question();
        question.setTitle("Some Title");

        List<Question> questions = List.of(question);

        when(userRepository.findUserById(1L)).thenReturn(Optional.of(new User()));

        // Act & Assert
        WrongRequest exception = assertThrows(WrongRequest.class, () ->
                questionService.createQuestionsBulk(1L, questions)
        );
        assertEquals("Content of question is empty.", exception.getMessage());
    }

    @Test
    void testCreateQuestions_WithNullTitle_ShouldThrowWrongRequest() {
        // Arrange
        Question question = new Question();
        question.setContent("Some content");

        List<Question> questions = List.of(question);

        when(userRepository.findUserById(1L)).thenReturn(Optional.of(new User()));

        // Act & Assert
        WrongRequest exception = assertThrows(WrongRequest.class, () ->
                questionService.createQuestionsBulk(1L, questions)
        );
        assertEquals("Title of question is empty.", exception.getMessage());
    }

    @Test
    void testCreateQuestions_Success() {
        // Arrange
        User user = new User();
        Question question1 = new Question();
        question1.setTitle("Title 1");
        question1.setContent("Content 1");

        Question question2 = new Question();
        question2.setTitle("Title 2");
        question2.setContent("Content 2");

        List<Question> questions = List.of(question1, question2);

        when(userRepository.findUserById(1L)).thenReturn(Optional.of(user));
        when(questionRepository.save(any(Question.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        List<Question> createdQuestions = questionService.createQuestionsBulk(1L, questions);

        // Assert
        assertEquals(2, createdQuestions.size());
        assertEquals(user, createdQuestions.get(0).getUser());
        assertEquals(user, createdQuestions.get(1).getUser());
        verify(questionRepository, times(2)).save(any(Question.class));
    }
}