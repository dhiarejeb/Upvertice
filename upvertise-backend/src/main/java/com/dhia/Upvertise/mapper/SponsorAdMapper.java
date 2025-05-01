package com.dhia.Upvertise.mapper;

import com.dhia.Upvertise.dto.SponsorAdRequest;
import com.dhia.Upvertise.dto.SponsorAdResponse;
import com.dhia.Upvertise.models.sponsorship.SponsorAd;
import com.dhia.Upvertise.services.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;

@RequiredArgsConstructor
@Component
public class SponsorAdMapper {
    private final CloudinaryService cloudinaryService;

    // Apply changes
    public void updateSponsorAdFromRequest(SponsorAdRequest request, SponsorAd sponsorAd, MultipartFile image) {
        // Only set values if the request is not null
        if (request != null) {
            if (request.content() != null) sponsorAd.setContent(request.content());
            if (request.designColors() != null) sponsorAd.setDesignColors(new HashSet<>(request.designColors()));//request.designColors()
            if (request.title() != null) sponsorAd.setTitle(request.title());
        }

        // ✅ Upload the image only if a new one is provided
        if (image != null && !image.isEmpty()) {
            String imageUrl = cloudinaryService.uploadImage(image); // Upload new image to Cloudinary
            sponsorAd.setDesign(imageUrl); // Store the Cloudinary URL
        }
    }
  /*  public static SponsorAdResponse toSponsorAdResponse(SponsorAd sponsorAd) {
        return new SponsorAdResponse(
                sponsorAd.getId(),
                sponsorAd.getTitle(),
                sponsorAd.getContent(),
                sponsorAd.getDesign(),
                sponsorAd.getDesignColors()
        );
    }*/
    public static SponsorAdResponse toSponsorAdResponseWithImageUrl(SponsorAd sponsorAd) {
        return SponsorAdResponse.builder()
                .id(sponsorAd.getId())
                .title(sponsorAd.getTitle())
                .content(sponsorAd.getContent())
                .design(sponsorAd.getDesign()) // ✅ This is now the Cloudinary URL
                .designColors(new ArrayList<>(sponsorAd.getDesignColors()))
                //.designColors(sponsorAd.getDesignColors())
                .build();
    }
}
