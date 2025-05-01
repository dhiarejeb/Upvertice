package com.dhia.Upvertise.services;

import com.dhia.Upvertise.dto.UserResponse;
import com.dhia.Upvertise.dto.UserUpdateRequest;
import com.dhia.Upvertise.handler.EntityNotFoundException;
import com.dhia.Upvertise.handler.UserNotFoundException;
import com.dhia.Upvertise.mapper.UserMapper;
import com.dhia.Upvertise.models.common.PageResponse;
import com.dhia.Upvertise.models.user.User;
import com.dhia.Upvertise.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final KeycloakService keycloakService;
    private final UserMapper userMapper;


    public PageResponse<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);

        List<UserResponse> content = userPage.getContent()
                .stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());

        return PageResponse.<UserResponse>builder()
                .content(content)
                .number(userPage.getNumber())
                .size(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .first(userPage.isFirst())
                .last(userPage.isLast())
                .build();
    }

    public PageResponse<UserResponse> getUserById(String userId) {
        User user = userRepository.findByKeycloakId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserResponse> content = List.of(userMapper.toUserResponse(user));

        return PageResponse.<UserResponse>builder()
                .content(content)
                .number(0)
                .size(1)
                .totalElements(1)
                .totalPages(1)
                .first(true)
                .last(true)
                .build();
    }

    /**
     * Creates a user in Keycloak first, then stores them in Upvertise.
     */
    public User createUser(User userRequest) {
        // Only create in Keycloak if NOT coming from Keycloak sync
        if (userRequest.getKeycloakId() == null) {
            String keycloakId = keycloakService.createUserInKeycloak(userRequest);
            userRequest.setKeycloakId(keycloakId);
        }

        return userRepository.save(userRequest);
    }

    public String updateProfilePhoto(Integer userId, MultipartFile file) throws IOException {
        String imageUrl = cloudinaryService.uploadProfilePhoto(file);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User",userId));
        user.setProfilePhotoUrl(imageUrl);
        userRepository.save(user);
        return imageUrl;
    }


    public User updateUserFromKeycloak(User userUpdate) {
        // Update database ONLY (no Keycloak sync)
        User existingUser = userRepository.findByKeycloakId(userUpdate.getKeycloakId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        existingUser.setFirstName(userUpdate.getFirstName());
        existingUser.setLastName(userUpdate.getLastName());
        existingUser.setEmail(userUpdate.getEmail());
        existingUser.setRole(userUpdate.getRole());

        return userRepository.save(existingUser);
    }


    @Transactional
    public void deleteUserEverywhere(String keycloakId) {


        // Then delete from DB
        userRepository.deleteByKeycloakId(keycloakId)
                .orElseThrow(() -> new UserNotFoundException(keycloakId));
    }





    

}
