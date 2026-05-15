package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// 1. UserStatusMapper.class 제거 (삭제된 파일)
@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public interface UserMapper {

  // 2. user.getStatus()가 삭제되었으므로 online 필드는 매핑에서 제외(ignore)하거나 기본값 설정
  // 실제 온라인 여부는 서비스 레이어에서 SessionRegistry를 통해 나중에 채워줍니다.
  @Mapping(target = "online", ignore = true)
  UserDto toDto(User user);
}