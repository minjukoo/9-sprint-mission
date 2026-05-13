package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public interface UserMapper {

  // User 엔티티에는 없는 online 필드는 기본적으로 false로 매핑됩니다.
  // 이 값은 Service 레이어의 SessionRegistry를 통해 동적으로 결정되거나,
  // 인증 성공 시점에 명시적으로 주입되어야 합니다.
  @Mapping(target = "online", ignore = true)
  @Mapping(target = "role", source = "role")
  @Mapping(target = "profile", source = "profile")
  UserDto toDto(User user);

  List<UserDto> toDtoList(List<User> users);
}