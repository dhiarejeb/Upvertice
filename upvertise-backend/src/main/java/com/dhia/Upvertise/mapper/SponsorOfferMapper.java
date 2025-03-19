package com.dhia.Upvertise.mapper;

import com.dhia.Upvertise.dto.SponsorOfferResponse;
import com.dhia.Upvertise.models.common.PageResponse;
import com.dhia.Upvertise.models.sponsorship.SponsorOffer;
import com.dhia.Upvertise.services.CloudinaryService;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;


public class SponsorOfferMapper {

    private CloudinaryService cloudinaryService;


    public static SponsorOfferResponse toSponsorOfferResponse(SponsorOffer sponsorOffer) {
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

    public static SponsorOfferResponse toResponseWithImagesUrls(SponsorOffer sponsorOffer, CloudinaryService cloudinaryService) {
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
    }
}
