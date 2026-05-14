package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse; // 추가
import com.sprint.mission.discodeit.dto.response.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Tag(name = "User", description = "User API")
public interface UserApi {

  @Operation(summary = "전체 User 목록 조회")
  @GetMapping
  ResponseEntity<PageResponse<UserDto>> findAll( // 반환 타입 수정
      @RequestParam(name = "page", defaultValue = "0") int page, // 파라미터 추가
      @RequestParam(name = "size", defaultValue = "10") int size
  );

  @Operation(summary = "User 등록")
  @PostMapping(consumes = "multipart/form-data")
  ResponseEntity<UserDto> register(
      @Valid @RequestPart("userCreateRequest") UserCreateRequest request,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  );

  @Operation(summary = "User 정보 수정")
  @PatchMapping(value = "/{userId}", consumes = "multipart/form-data")
  ResponseEntity<UserDto> update(
      @PathVariable(name = "userId") UUID userId,
      @Valid @RequestPart("userUpdateRequest") UserUpdateRequest request,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  );

  @Operation(summary = "User 삭제")
  @DeleteMapping("/{userId}")
  ResponseEntity<Void> delete(@PathVariable(name = "userId") UUID userId);
}