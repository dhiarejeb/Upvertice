package com.dhia.Upvertise.dto;

import java.util.List;

public record SponsorAdRequest(
        String title,
        String content,
        String design,
        List<String> designColors
) {
}
