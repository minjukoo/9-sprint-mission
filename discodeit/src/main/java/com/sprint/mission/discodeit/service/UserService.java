package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse; // 추가
import com.sprint.mission.discodeit.dto.response.UserDto;
import org.springframework.data.domain.Pageable; // 추가
import java.util.UUID;

public interface UserService {

  UserDto create(UserCreateRequest request, BinaryContentCreateRequest profileRequest);

  UserDto update(UUID id, UserUpdateRequest request, BinaryContentCreateRequest profileRequest);

  void delete(UUID id);

  // List 대신 PageResponse와 Pageable 적용
  PageResponse<UserDto> findAll(Pageable pageable);

  UserDto findById(UUID id);
}