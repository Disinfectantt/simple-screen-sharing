package com.cringee.simplescreensharing.serviceTest;

import com.cringee.simplescreensharing.dto.RoleDto;
import com.cringee.simplescreensharing.models.Role;
import com.cringee.simplescreensharing.repos.RoleRepo;
import com.cringee.simplescreensharing.services.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {
    @Mock
    private RoleRepo roleRepo;

    @InjectMocks
    private RoleService roleService;

    private Role role;
    private RoleDto roleDto;

    @BeforeEach
    public void setUp() {
        role = new Role();
        role.setId(1L);
        role.setName("ADMIN");

        roleDto = new RoleDto();
        roleDto.setName("USER");
    }

    @Test
    public void testFindByName() {
        when(roleRepo.findByName("ADMIN")).thenReturn(role);
        Role found = roleService.findByName("ADMIN");
        assertThat(found.getName()).isEqualTo("ADMIN");
    }

    @Test
    public void testFindById() {
        when(roleRepo.findById(1L)).thenReturn(Optional.of(role));
        Role found = roleService.findById(1L);
        assertThat(found.getId()).isEqualTo(1L);
    }

    @Test
    public void testFindAll() {
        when(roleRepo.findAll()).thenReturn(Collections.singletonList(role));
        List<Role> roles = roleService.findAll();
        assertThat(roles).hasSize(1);
        assertThat(roles.getFirst().getName()).isEqualTo("ADMIN");
    }

    @Test
    public void testSaveByName() {
        roleService.save("USER");
        verify(roleRepo).save(any(Role.class));
    }

    @Test
    public void testSaveRole() {
        roleService.save(role);
        verify(roleRepo).save(role);
    }

    @Test
    public void testSaveRoleDto() {
        roleService.save(roleDto);
        verify(roleRepo).save(any(Role.class));
    }

    @Test
    public void testCount() {
        when(roleRepo.count()).thenReturn(1L);
        long count = roleService.count();
        assertThat(count).isEqualTo(1L);
    }
}
