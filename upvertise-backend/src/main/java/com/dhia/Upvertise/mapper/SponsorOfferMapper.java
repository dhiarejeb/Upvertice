package com.dhia.Upvertise.mapper;

import com.dhia.Upvertise.dto.SponsorOfferResponse;
import com.dhia.Upvertise.models.common.PageResponse;
import com.dhia.Upvertise.models.sponsorship.SponsorOffer;
import com.dhia.Upvertise.services.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SponsorOfferMapper {

    private final CloudinaryService cloudinaryService;


    public static SponsorOfferResponse toSponsorOfferResponse(SponsorOffer sponsorOffer) {
        return SponsorOfferResponse.builder()
                .id(sponsorOffer.getId())
                .title(sponsorOffer.getTitle())
                .description(sponsorOffer.getDescription())
                .price(sponsorOffer.getPrice())
                .productQuantity(sponsorOffer.getProductQuantity())
                .explainImages(sponsorOffer.getExplainImages()) // Updated to handle List<String>
                .category(sponsorOffer.getCategory())
                .status(String.valueOf(sponsorOffer.getStatus()))
                .numberAds(sponsorOffer.getNumberAds())
                .productType(sponsorOffer.getProductType())
                .build();
    }
    public static PageResponse<SponsorOfferResponse> toSponsorOfferPageResponse(Page<SponsorOffer> page) {

        List<SponsorOfferResponse> responses = page.getContent().stream()
                .map(SponsorOfferMapper::toSponsorOfferResponse)
                .toList();

        return PageResponse.<SponsorOfferResponse>builder()
                .content(responses)
                .number(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
    public SponsorOfferResponse toResponseWithImagesUrls(SponsorOffer sponsorOffer) {
        List<String> imageUrls = sponsorOffer.getExplainImages()
                .stream()
                .map(image -> {
                    try {
                        return cloudinaryService.getImageUrl(image);
                    } catch (Exception e) {
                        System.err.println("Failed to generate URL for image: " + image);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        System.out.println("Mapped SponsorOffer: " + sponsorOffer.getTitle() + ", Images: " + imageUrls);

        return SponsorOfferResponse.builder()
                .id(sponsorOffer.getId())
                .title(sponsorOffer.getTitle())
                .description(sponsorOffer.getDescription())
                .price(sponsorOffer.getPrice())
                .productQuantity(sponsorOffer.getProductQuantity())
                .explainImages(imageUrls)
                .category(sponsorOffer.getCategory())
                .status(String.valueOf(sponsorOffer.getStatus()))
                .numberAds(sponsorOffer.getNumberAds())
                .productType(sponsorOffer.getProductType())
                .salesArea(sponsorOffer.getSalesArea())
                .build();
    }

    /*public static SponsorOfferResponse toResponseWithImagesUrls(SponsorOffer sponsorOffer, CloudinaryService cloudinaryService) {
        List<String> imageUrls = sponsorOffer.getExplainImages()
                .stream()
                .map(image -> cloudinaryService.getImageUrl(image)) // Use the service to get image URL
                .collect(Collectors.toList());

        return SponsorOfferResponse.builder()
                .title(sponsorOffer.getTitle())
                .description(sponsorOffer.getDescription())
                .price(sponsorOffer.getPrice())
                .productQuantity(sponsorOffer.getProductQuantity())
                .explainImages(sponsorOffer.getExplainImages()) // Updated to handle List<String>
                .category(sponsorOffer.getCategory())
                .status(String.valueOf(sponsorOffer.getStatus()))
                .numberAds(sponsorOffer.getNumberAds())
                .productType(sponsorOffer.getProductType())
                .explainImages(imageUrls)
                .build();
    }*/
}
