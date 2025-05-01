package com.dhia.Upvertise.mapper;


import com.dhia.Upvertise.dto.UserResponse;
import com.dhia.Upvertise.models.common.PageResponse;
import com.dhia.Upvertise.models.user.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        }

        return UserResponse.builder()
                .id(user.getId().toString())
                .keycloakId(user.getKeycloakId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .profilePhotoUrl(user.getProfilePhotoUrl())
                .build();
    }
    public <T> PageResponse<T> mapToPageResponse(Page<T> page) {
        PageResponse<T> response = new PageResponse<>();
        response.setContent(page.getContent());
        response.setNumber(page.getNumber());
        response.setSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setFirst(page.isFirst());
        response.setLast(page.isLast());
        return response;
    }
}
