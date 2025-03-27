package com.dhia.Upvertise.dto;

public record SponsorOfferEvent(
        Integer offerId,
        String title,
        String createdBy,
        String adminEmail) {}

