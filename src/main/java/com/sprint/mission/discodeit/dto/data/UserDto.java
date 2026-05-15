package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.UserRole;
import java.util.UUID;

public record UserDto(
    UUID id,
    String username,
    String email,
    BinaryContentDto profile,
    UserRole role,      // role 필드 추가
    Boolean online     // 기존의 online 여부는 SessionRegistry를 통해 채워질 예정입니다 [cite: 7599]
) {
}