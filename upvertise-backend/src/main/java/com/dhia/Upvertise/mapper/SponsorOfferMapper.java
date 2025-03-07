package com.dhia.Upvertise.mapper;

import com.dhia.Upvertise.dto.SponsorOfferResponse;
import com.dhia.Upvertise.models.common.PageResponse;
import com.dhia.Upvertise.models.sponsorship.SponsorOffer;
import org.springframework.data.domain.Page;

import java.util.List;



public class SponsorOfferMapper {


    public static SponsorOfferResponse toSponsorOfferResponse(SponsorOffer sponsorOffer) {
        return SponsorOfferResponse.builder()
                .title(sponsorOffer.getTitle())
                .description(sponsorOffer.getDescription())
                .price(sponsorOffer.getPrice())
                .gobletQuantity(sponsorOffer.getGobletQuantity())
                .explainImage(sponsorOffer.getExplainImage())
                .category(sponsorOffer.getCategory())
                .status(String.valueOf(sponsorOffer.getStatus()))
                .numberAds(sponsorOffer.getNumberAds())
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
}
