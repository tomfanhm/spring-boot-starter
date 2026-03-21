package com.starter.app.modules.user.mapper;

import com.starter.app.modules.user.User;
import com.starter.app.modules.user.dto.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(target = "role", expression = "java(user.getRole().name())")
  UserResponse toResponse(User user);
}
