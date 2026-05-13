package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record UserRoleUpdateRequest(
    @NotNull(message = "사용자 ID는 필수입니다.")
    UUID userId,

    @NotBlank(message = "새로운 권한은 필수입니다.")
    String newRole
) {
}