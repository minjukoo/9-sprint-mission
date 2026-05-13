package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public interface UserMapper {

  // user.getStatus()가 삭제되었으므로 기존 expression 제거
  // online 상태는 나중에 Service 계층에서 SessionRegistry를 통해 별도로 주입하거나,
  // 우선 기본값(false)으로 매핑되도록 둡니다.
  @Mapping(target = "online", ignore = true)
  @Mapping(target = "role", source = "role")
  @Mapping(target = "profile", source = "profile")
  UserDto toDto(User user);

  List<UserDto> toDtoList(List<User> users);
}