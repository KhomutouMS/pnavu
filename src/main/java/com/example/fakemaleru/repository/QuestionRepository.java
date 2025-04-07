package com.example.fakemaleru.repository;

import com.example.fakemaleru.model.Question;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository  extends JpaRepository<Question, Long> {
    Optional<Question> findQuestionById(Long id);
}
