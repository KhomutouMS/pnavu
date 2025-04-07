package com.example.fakemaleru.repository;

import com.example.fakemaleru.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    @SuppressWarnings("checkstyle:EmptyLineSeparator")
    Optional<User> findUserByEmail(String email);
}
