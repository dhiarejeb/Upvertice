package com.dhia.Upvertise.dto;

import java.util.List;

public record SponsorAdRequest(
        String content,
        String design,
        List<String> designColors
) {
}
