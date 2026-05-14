package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public interface UserMapper {

  @Mapping(target = "online", ignore = true)
  @Mapping(target = "profile", source = "profile")
  // 핵심 수정: Java 코드를 직접 주입하여 컴파일 에러를 방지하고 ROLE_ 접두사를 보장합니다.
  @Mapping(target = "role", source = "role")
    // 엔티티의 "USER"가 그대로 DTO의 "USER"로 매핑되도록 합니다.
  UserDto toDto(User user);

  List<UserDto> toDtoList(List<User> users);
}