package com.dhia.Upvertise.mapper;

import com.dhia.Upvertise.dto.SponsorAdRequest;
import com.dhia.Upvertise.dto.SponsorAdResponse;
import com.dhia.Upvertise.models.sponsorship.SponsorAd;
import com.dhia.Upvertise.services.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Component
public class SponsorAdMapper {
    private final CloudinaryService cloudinaryService;

    // Apply changes
    public void updateSponsorAdFromRequest(SponsorAdRequest request, SponsorAd sponsorAd, MultipartFile image) {
        if (request.content() != null) sponsorAd.setContent(request.content());
        if (request.designColors() != null) sponsorAd.setDesign_colors(request.designColors());
        if (request.title() != null) sponsorAd.setTitle(request.title());


        // ✅ Upload the image only if a new one is provided
        if (image != null && !image.isEmpty()) {
            String imageUrl = cloudinaryService.uploadImage(image); // Upload new image to Cloudinary
            sponsorAd.setDesign(imageUrl); // Store the Cloudinary URL
        }
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
    public SponsorAdResponse toSponsorAdResponseWithImageUrl(SponsorAd sponsorAd) {
        return SponsorAdResponse.builder()
                .title(sponsorAd.getTitle())
                .content(sponsorAd.getContent())
                .placement(sponsorAd.getPlacement())
                .design(sponsorAd.getDesign()) // ✅ This is now the Cloudinary URL
                .designColors(sponsorAd.getDesign_colors())
                .build();
    }
}
