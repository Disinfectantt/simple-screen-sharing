package com.cringee.simplescreensharing.dto;

import com.cringee.simplescreensharing.annotations.UsernameUnique;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    private Long id;
    @NotNull
    @Size(min = 3, max = 50)
    @UsernameUnique
    private String username;
    @NotNull
    @Size(min = 1, max = 255)
    private String password;
    private Set<Long> roles;
    private Boolean enabled;
}
