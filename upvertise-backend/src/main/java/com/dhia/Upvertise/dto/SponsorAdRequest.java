package com.dhia.Upvertise.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record SponsorAdRequest(
        String title,
        String content,
        List<String> designColors
) {
}
