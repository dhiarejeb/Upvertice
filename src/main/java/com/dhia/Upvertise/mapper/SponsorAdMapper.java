package com.dhia.Upvertise.mapper;

import com.dhia.Upvertise.dto.SponsorAdResponse;
import com.dhia.Upvertise.models.sponsorship.SponsorAd;
import org.springframework.stereotype.Component;

@Component
public class SponsorAdMapper {
    public static SponsorAdResponse toSponsorAdResponse(SponsorAd sponsorAd) {
        return new SponsorAdResponse(
                sponsorAd.getContent(),
                sponsorAd.getPlacement(),
                sponsorAd.getDesign(),
                sponsorAd.getDesign_colors()
        );
    }
}
