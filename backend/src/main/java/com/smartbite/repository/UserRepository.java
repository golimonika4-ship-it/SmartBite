package com.smartbite.repository;

import com.smartbite.model.Role;
import com.smartbite.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findByRollNumberIgnoreCase(String rollNumber);

    Optional<User> findByEmailIgnoreCaseOrRollNumberIgnoreCase(String email, String rollNumber);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByRollNumberIgnoreCase(String rollNumber);

    boolean existsByRole(Role role);
}
