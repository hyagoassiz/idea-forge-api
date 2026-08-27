package com.idea_forge.modules.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.idea_forge.modules.user.dto.CreateUserRequestDTO;
import com.idea_forge.modules.user.dto.CreateUserResponseDTO;
import com.idea_forge.modules.user.dto.UserResponseDTO;
import com.idea_forge.modules.user.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    User toEntity(CreateUserRequestDTO createUserRequestDTO);

    CreateUserResponseDTO toCreateResponse(User user);

    UserResponseDTO toUserResponse(User user);
}
