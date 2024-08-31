package com.cringee.simplescreensharing.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class RoleDto {
    private Long id;
    @Size(min = 1, max = 255)
    private String name;
    private Boolean enabled = true;

    public RoleDto(String name) {
        this.name = name;
    }
}
