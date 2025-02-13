package com.dhia.Upvertise.mapper;

import com.dhia.Upvertise.dto.SponsorOfferResponse;
import com.dhia.Upvertise.models.common.PageResponse;
import com.dhia.Upvertise.models.sponsorship.SponsorOffer;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
public class SponsorOfferMapper {


    public SponsorOfferResponse toSponsorOfferResponse(SponsorOffer sponsorOffer) {
        return SponsorOfferResponse.builder()
                .title(sponsorOffer.getTitle())
                .description(sponsorOffer.getDescription())
                .price(sponsorOffer.getPrice())
                .gobletQuantity(sponsorOffer.getGobletQuantity())
                .explainImage(sponsorOffer.getExplainImage())
                .category(sponsorOffer.getCategory())
                .status(String.valueOf(sponsorOffer.getStatus()))
                .build();
    }
    public PageResponse<SponsorOfferResponse> toSponsorOfferPageResponse(Page<SponsorOffer> page) {
        SponsorOfferMapper mapper = new SponsorOfferMapper();
        List<SponsorOfferResponse> responses = page.getContent().stream()
                .map(mapper::toSponsorOfferResponse)
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
