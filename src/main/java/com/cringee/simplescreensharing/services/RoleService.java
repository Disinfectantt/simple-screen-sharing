package com.cringee.simplescreensharing.services;

import com.cringee.simplescreensharing.dto.RoleDto;
import com.cringee.simplescreensharing.mapper.RoleMapper;
import com.cringee.simplescreensharing.models.Role;
import com.cringee.simplescreensharing.repos.RoleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepo roleRepo;

    public Role findByName(String name) {
        return roleRepo.findByName(name);
    }

    public Role findById(Long id) {
        return roleRepo.findById(id).orElse(null);
    }

    public List<Role> findAll() {
        return roleRepo.findAll();
    }

    public void save(String name) {
        Role role = new Role();
        role.setName(name);
        roleRepo.save(role);
    }

    public void save(Role role) {
        roleRepo.save(role);
    }

    public void save(RoleDto roleDto) {
        roleRepo.save(RoleMapper.INSTANCE.toRole(roleDto));
    }

    public long count() {
        return roleRepo.count();
    }
}
