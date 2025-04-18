package com.dhia.Upvertise.dto;

import lombok.Builder;

import java.util.List;
@Builder
public record SponsorAdResponse(
        Integer id,
        String title,
        String content,
        //String placement,
        String design,
        List<String> designColors
) {
}
