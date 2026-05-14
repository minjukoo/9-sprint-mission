package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails; // 추가
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // 추가
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Channel", description = "Channel API")
public interface ChannelApi {

  @Operation(summary = "Public Channel 생성")
  @PostMapping("/public")
  ResponseEntity<ChannelDto> createPublic(
      @Valid @RequestBody PublicChannelCreateRequest request,
      @AuthenticationPrincipal DiscodeitUserDetails userDetails); // 수정

  @Operation(summary = "Private Channel 생성")
  @PostMapping("/private")
  ResponseEntity<ChannelDto> createPrivate(
      @Valid @RequestBody PrivateChannelCreateRequest request,
      @AuthenticationPrincipal DiscodeitUserDetails userDetails); // 수정


  @Operation(summary = "User가 참여 중인 Channel 목록 조회")
  @GetMapping
  ResponseEntity<List<ChannelDto>> findAll(
      @RequestParam(name = "userId") UUID userId,
      @AuthenticationPrincipal DiscodeitUserDetails userDetails); // 수정

  @Operation(summary = "Channel 정보 수정")
  @PatchMapping("/{channelId}")
  ResponseEntity<ChannelDto> update(
      @PathVariable(name = "channelId") UUID channelId,
      @Valid @RequestBody PublicChannelUpdateRequest request,
      @AuthenticationPrincipal DiscodeitUserDetails userDetails); // 수정

  @Operation(summary = "Channel 삭제")
  @DeleteMapping("/{channelId}")
  ResponseEntity<Void> delete(
      @PathVariable(name = "channelId") UUID channelId,
      @AuthenticationPrincipal DiscodeitUserDetails userDetails); // 수정
}