package com.dhia.Upvertise.mapper;

import com.dhia.Upvertise.dto.SponsorAdRequest;
import com.dhia.Upvertise.dto.SponsorAdResponse;
import com.dhia.Upvertise.models.sponsorship.SponsorAd;


public class SponsorAdMapper {
    // Apply changes
    public static void updateSponsorAdFromRequest(SponsorAdRequest request, SponsorAd sponsorAd) {
        if (request.content() != null) sponsorAd.setContent(request.content());
        if (request.design() != null) sponsorAd.setDesign(request.design());
        if (request.designColors() != null) sponsorAd.setDesign_colors(request.designColors());
        if (request.title() != null) sponsorAd.setTitle(request.title());
    }
    public static SponsorAdResponse toSponsorAdResponse(SponsorAd sponsorAd) {
        return new SponsorAdResponse(
                sponsorAd.getTitle(),
                sponsorAd.getContent(),
                sponsorAd.getPlacement(),
                sponsorAd.getDesign(),
                sponsorAd.getDesign_colors()
        );
    }
}
