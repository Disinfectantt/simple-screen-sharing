package com.cringee.simplescreensharing.repos;

import com.cringee.simplescreensharing.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
