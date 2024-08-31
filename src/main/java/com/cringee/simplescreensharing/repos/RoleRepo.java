package com.cringee.simplescreensharing.repos;

import com.cringee.simplescreensharing.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepo extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
