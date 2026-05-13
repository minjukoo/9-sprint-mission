package com.sprint.mission.discodeit.dto.response;

import java.util.UUID;

public record UserDto(
    UUID id,
    String username,
    String email,
    BinaryContentDto profile,
    String role,    // 추가: ADMIN, CHANNEL_MANAGER, USER 등의 권한 정보
    boolean online  // 유지: 다만, 이제 소스는 UserStatus가 아닌 SessionRegistry가 됩니다.
) {
}