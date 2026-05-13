package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.util.List;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public interface UserMapper {

  // role 필드 매핑 시 접두사를 붙이도록 수정
  @Mapping(target = "online", ignore = true)
  @Mapping(target = "role", source = "role", qualifiedByName = "addRolePrefix")
  @Mapping(target = "profile", source = "profile")
  UserDto toDto(User user);

  List<UserDto> toDtoList(List<User> users);

  @Named("addRolePrefix")
  default String addRolePrefix(Enum<?> role) {
    if (role == null) return null;
    String roleName = role.name();
    return roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName;
  }
}