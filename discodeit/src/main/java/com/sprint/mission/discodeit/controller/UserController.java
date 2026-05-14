package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse; // 추가
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping
  public ResponseEntity<PageResponse<UserDto>> findAll(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(userService.findAll(pageable));
  }

  @PostMapping
  public ResponseEntity<UserDto> register(
      @Valid @RequestPart("userCreateRequest") UserCreateRequest request,
      @RequestPart(value = "profile", required = false) MultipartFile profile) {
    log.info("Registering new user with email: {}", request.email());
    BinaryContentCreateRequest profileRequest = resolveProfileRequest(profile);
    UserDto userDto = userService.create(request, profileRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
  }

  @PatchMapping("/{userId}")
  public ResponseEntity<UserDto> update(
      @PathVariable UUID userId,
      @Valid @RequestPart("userUpdateRequest") UserUpdateRequest request,
      @RequestPart(value = "profile", required = false) MultipartFile profile) {
    log.info("Updating user information for ID: {}", userId);
    BinaryContentCreateRequest profileRequest = resolveProfileRequest(profile);
    return ResponseEntity.ok(userService.update(userId, request, profileRequest));
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> delete(@PathVariable UUID userId) {
    log.info("Deleting user ID: {}", userId);
    userService.delete(userId);
    return ResponseEntity.noContent().build();
  }

  private BinaryContentCreateRequest resolveProfileRequest(MultipartFile profileFile) {
    if (profileFile == null || profileFile.isEmpty()) {
      return null;
    }
    try {
      return new BinaryContentCreateRequest(
          profileFile.getBytes(), profileFile.getOriginalFilename(), profileFile.getContentType(),
          profileFile.getSize()
      );
    } catch (IOException e) {
      log.error("Failed to process profile image", e);
      throw new RuntimeException("이미지 처리 중 오류 발생", e);
    }
  }
}