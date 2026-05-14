package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails; // 추가
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // 추가
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController implements ChannelApi {

  private final ChannelService channelService;

  @Override
  public ResponseEntity<ChannelDto> createPublic(
      @Valid @RequestBody PublicChannelCreateRequest request,
      @AuthenticationPrincipal DiscodeitUserDetails userDetails) { // HttpSession 대신 사용

    // userDetails.getUserDto().id()를 통해 안전하게 인증된 사용자 ID를 가져옵니다.
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(channelService.createPublicChannel(request, userDetails.getUserDto().id()));
  }

  @Override
  public ResponseEntity<ChannelDto> createPrivate(
      @Valid @RequestBody PrivateChannelCreateRequest request,
      @AuthenticationPrincipal DiscodeitUserDetails userDetails) {

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(channelService.createPrivateChannel(request, userDetails.getUserDto().id()));
  }


  @Override
  public ResponseEntity<List<ChannelDto>> findAll(
      @RequestParam UUID userId,
      @AuthenticationPrincipal DiscodeitUserDetails userDetails) {

    // 1번 문제 해결: 본인의 채널 목록만 조회하도록 검증 로직 유지
    if (!userDetails.getUserDto().id().equals(userId)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "타인의 채널 목록을 조회할 권한이 없습니다.");
    }

    return ResponseEntity.ok(channelService.findAllByUserId(userDetails.getUserDto().id()));
  }

  @Override
  public ResponseEntity<ChannelDto> update(
      @PathVariable UUID channelId,
      @Valid @RequestBody PublicChannelUpdateRequest request,
      @AuthenticationPrincipal DiscodeitUserDetails userDetails) {

    return ResponseEntity.ok(
        channelService.update(channelId, request, userDetails.getUserDto().id()));
  }

  @Override
  public ResponseEntity<Void> delete(
      @PathVariable UUID channelId,
      @AuthenticationPrincipal DiscodeitUserDetails userDetails) {

    log.info("채널 삭제 요청: channelId={}, requesterId={}", channelId, userDetails.getUserDto().id());
    channelService.delete(channelId, userDetails.getUserDto().id());

    return ResponseEntity.noContent().build();
  }

  // getSessionUserId 헬퍼 메서드는 더 이상 필요 없으므로 삭제합니다.
}