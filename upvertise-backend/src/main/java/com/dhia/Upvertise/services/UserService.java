package com.dhia.Upvertise.services;

import com.dhia.Upvertise.handler.EntityNotFoundException;
import com.dhia.Upvertise.models.user.User;
import com.dhia.Upvertise.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    public User createUser(User userRequest) {
        User user = new User();
        user.setKeycloakId(userRequest.getKeycloakId());
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setRole(userRequest.getRole());
        user.setProfilePhotoUrl(userRequest.getProfilePhotoUrl());
        return userRepository.save(user);
    }

    public String updateProfilePhoto(Integer userId, MultipartFile file) throws IOException {
        String imageUrl = cloudinaryService.uploadProfilePhoto(file);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User",userId));
        user.setProfilePhotoUrl(imageUrl);
        userRepository.save(user);
        return imageUrl;
    }

}
