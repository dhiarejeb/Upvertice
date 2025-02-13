package com.dhia.Upvertise.mapper;

import com.dhia.Upvertise.dto.SponsorshipResponse;
import com.dhia.Upvertise.models.sponsorship.SponsorAd;
import com.dhia.Upvertise.models.sponsorship.Sponsorship;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
@Component
public class SponsorshipMapper {

    public SponsorshipResponse toSponsorshipResponse(Sponsorship sponsorship) {
        if (sponsorship == null) {
            return null;
        }
        return new SponsorshipResponse(
                sponsorship.getId(),
                sponsorship.getStatus(),
                sponsorship.getUserId(),
                sponsorship.getSponsorOffer().getTitle(),
                sponsorship.getSponsorAds().stream()
                        .map(SponsorAd::getTitle)
                        .collect(Collectors.toSet()),
                sponsorship.getCreatedDate()
        );
    }



}
