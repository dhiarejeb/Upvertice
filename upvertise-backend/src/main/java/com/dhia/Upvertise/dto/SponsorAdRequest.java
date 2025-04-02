package com.dhia.Upvertise.dto;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record SponsorAdRequest(
        @NotBlank(message = "Title cannot be blank") String title,
        @NotBlank(message = "Content cannot be blank") String content,
        List<@NotBlank(message = "Design colors cannot be blank") String> designColors
) {
}
