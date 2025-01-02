package com.cringee.simplescreensharing.mapper;

import com.cringee.simplescreensharing.dto.UserDto;
import com.cringee.simplescreensharing.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {RoleMapper.class})
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User toUser(UserDto userDto);

    UserDto toUserDto(User user);

}
