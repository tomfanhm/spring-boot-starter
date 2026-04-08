package com.starter.app.modules.auth.mapper;

import com.starter.app.modules.auth.dto.RegisterRequest;
import com.starter.app.modules.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "passwordHash", ignore = true)
  @Mapping(target = "role", ignore = true)
  @Mapping(target = "enabled", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  User toUser(RegisterRequest request);
}
