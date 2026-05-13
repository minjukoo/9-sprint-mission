package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
  public UserDto update(UUID id, UserUpdateRequest request, BinaryContentCreateRequest profileRequest) {
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
  public List<UserDto> findAll() {
    return userRepository.findAll().stream()
        .map(this::toDtoWithOnlineStatus)
        .toList();
  }

  @Override
  public UserDto findById(UUID id) {
    return userRepository.findById(id)
        .map(this::toDtoWithOnlineStatus)
        .orElseThrow(() -> new UserNotFoundException(id.toString()));
  }

  private UserDto toDtoWithOnlineStatus(User user) {
    UserDto dto = userMapper.toDto(user);
    // UserDto 필드 순서: id, username, email, profile, role, online
    return new UserDto(
        dto.id(),
        dto.username(),
        dto.email(),
        dto.profile(),
        dto.role(), // 5번째: role (String) 추가
        false       // 6번째: online (boolean) 유지
    );
  }
}