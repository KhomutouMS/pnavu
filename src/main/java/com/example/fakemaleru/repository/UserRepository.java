package com.example.fakemaleru.repository;

import com.example.fakemaleru.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserById(Long id);

    @Query("SELECT u FROM User u WHERE SIZE(u.questions) >= :minQuestions")
    List<User> findCuriousUsers(@Param("minQuestions") int minQuestions);

    @Query(value = "SELECT DISTINCT u.* FROM users u "
            + "JOIN answers a ON u.id = a.user_id "
            + "JOIN questions q ON a.question_id = q.id "
            + "WHERE q.title LIKE CONCAT('%', :titleFragment, '%')",
            nativeQuery = true)
    List<User> findDiscussingUsers(@Param("titleFragment") String titleFragment);
}
