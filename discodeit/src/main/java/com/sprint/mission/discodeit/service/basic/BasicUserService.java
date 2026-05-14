package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final ChannelRepository channelRepository;
  private final BinaryContentService binaryContentService;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final org.springframework.security.core.session.SessionRegistry sessionRegistry;

  // 추후 SecurityConfig 설정 후 주입받아 사용 예정
  // private final SessionRegistry sessionRegistry;

  @Override
  @Transactional
  public UserDto create(UserCreateRequest request, BinaryContentCreateRequest profileRequest) {
    if (userRepository.existsByEmail(request.email())) {
      throw new UserAlreadyExistsException(request.email());
    }

    BinaryContent profile = null;
    if (profileRequest != null) {
      var profileDto = binaryContentService.create(profileRequest);
      profile = binaryContentRepository.findById(profileDto.id()).orElseThrow();
    }

    String encodedPassword = passwordEncoder.encode(request.password());

    // 기본 권한 'USER'로 생성
    User user = new User(request.username(), request.email(), encodedPassword, profile, "USER");
    User savedUser = userRepository.save(user);

    return toDtoWithOnlineStatus(savedUser);
  }

  @Override
  @Transactional
  public UserDto update(UUID id, UserUpdateRequest request,
      BinaryContentCreateRequest profileRequest) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id.toString()));

    String encodedPassword = (request.newPassword() != null && !request.newPassword().isBlank())
        ? passwordEncoder.encode(request.newPassword())
        : user.getPassword();

    String updatedUsername = (request.newUsername() != null && !request.newUsername().isBlank())
        ? request.newUsername()
        : user.getUsername();

    String updatedEmail = (request.newEmail() != null && !request.newEmail().isBlank())
        ? request.newEmail()
        : user.getEmail();

    BinaryContent newProfile = user.getProfile();
    if (profileRequest != null) {
      if (user.getProfile() != null) {
        binaryContentService.delete(user.getProfile().getId());
      }
      var profileDto = binaryContentService.create(profileRequest);
      newProfile = binaryContentRepository.findById(profileDto.id()).orElseThrow();
    }

    user.update(updatedUsername, updatedEmail, encodedPassword, newProfile);
    return toDtoWithOnlineStatus(user);
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id.toString()));

    if (user.getProfile() != null) {
      binaryContentService.delete(user.getProfile().getId());
    }

    channelRepository.findAllByUserId(id).forEach(channel -> channel.removeParticipant(user));
    userRepository.delete(user);
  }

  @Override
  public PageResponse<UserDto> findAll(Pageable pageable) {
    Page<User> userPage = userRepository.findAll(pageable);

    List<UserDto> content = userPage.getContent().stream()
        .map(this::toDtoWithOnlineStatus)
        .toList();

    return new PageResponse<>(
        content,
        null, // Offset 기반 페이징이므로 커서는 null
        userPage.getSize(),
        userPage.hasNext(),
        userPage.getTotalElements()
    );
  }

  @Override
  public UserDto findById(UUID id) {
    return userRepository.findById(id)
        .map(this::toDtoWithOnlineStatus)
        .orElseThrow(() -> new UserNotFoundException(id.toString()));
  }

  private UserDto toDtoWithOnlineStatus(User user) {
    List<Object> principals = sessionRegistry.getAllPrincipals();
    log.info("현재 세션 레지스터에 등록된 인원 수: {}", principals.size()); // 이게 0이면 등록 자체가 안 된 것

    boolean isOnline = principals.stream()
        .filter(p -> p instanceof DiscodeitUserDetails)
        .map(p -> (DiscodeitUserDetails) p)
        .anyMatch(u -> u.getUserDto().id().equals(user.getId()));
    UserDto dto = userMapper.toDto(user);

    return new UserDto(
        dto.id(),
        dto.username(),
        dto.email(),
        dto.profile(),
        dto.role(),
        isOnline // [수정] 계산된 온라인 상태값 전달
    );
  }
}