package com.cringee.simplescreensharing.mapper;

import com.cringee.simplescreensharing.dto.RoleDto;
import com.cringee.simplescreensharing.models.Role;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RoleMapper {
    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    RoleDto toDto(Role role);

    Role toRole(RoleDto roleDto);

}
