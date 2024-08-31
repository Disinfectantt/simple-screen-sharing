package com.cringee.simplescreensharing.serviceTest;

import com.cringee.simplescreensharing.CustomUserDetails;
import com.cringee.simplescreensharing.models.Role;
import com.cringee.simplescreensharing.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsTest {
    @Mock
    private User user;

    private CustomUserDetails customUserDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customUserDetails = new CustomUserDetails(user);
    }

    @Test
    void getAuthorities_ShouldReturnUserAuthorities() {
        Role role1 = new Role();
        role1.setName("ROLE_USER");
        Role role2 = new Role();
        role2.setName("ROLE_ADMIN");

        Set<Role> roles = new HashSet<>();
        roles.add(role1);
        roles.add(role2);
        Mockito.when(user.getRoles()).thenReturn(roles);

        Collection<? extends GrantedAuthority> authorities = customUserDetails.getAuthorities();

        assertNotNull(authorities);
        assertEquals(2, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void getPassword_ShouldReturnUserPassword() {
        Mockito.when(user.getPassword()).thenReturn("password");

        String password = customUserDetails.getPassword();

        assertEquals("password", password);
    }

    @Test
    void getUsername_ShouldReturnUserUsername() {
        Mockito.when(user.getUsername()).thenReturn("username");

        String username = customUserDetails.getUsername();

        assertEquals("username", username);
    }

    @Test
    void isAccountNonExpired_ShouldReturnTrue() {
        assertTrue(customUserDetails.isAccountNonExpired());
    }

    @Test
    void isAccountNonLocked_ShouldReturnTrue() {
        assertTrue(customUserDetails.isAccountNonLocked());
    }

    @Test
    void isCredentialsNonExpired_ShouldReturnTrue() {
        assertTrue(customUserDetails.isCredentialsNonExpired());
    }

    @Test
    void isEnabled_ShouldReturnUserEnabledStatus() {
        Mockito.when(user.getEnabled()).thenReturn(true);

        assertTrue(customUserDetails.isEnabled());

        Mockito.when(user.getEnabled()).thenReturn(false);

        assertFalse(customUserDetails.isEnabled());
    }
}
