package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;

public interface AuthService {
  // 로그인은 Spring Security가 처리하므로 삭제하고, 권한 수정 기능을 추가합니다.
  UserDto updateRole(UserRoleUpdateRequest request);
}