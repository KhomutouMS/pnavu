package com.example.fakemaleru.repository;

import com.example.fakemaleru.model.Answer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository  extends JpaRepository<Answer, Long> {
    Optional<Answer> findAnswerById(Long id);
}
