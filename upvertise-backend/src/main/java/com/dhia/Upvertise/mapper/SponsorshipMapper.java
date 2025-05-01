package com.dhia.Upvertise.mapper;

import com.dhia.Upvertise.dto.*;
import com.dhia.Upvertise.models.sponsorship.Sponsorship;
import com.dhia.Upvertise.services.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class SponsorshipMapper {





    public static SponsorshipResponse toSponsorshipResponse(Sponsorship sponsorship) {
        if (sponsorship == null) {
            return null;
        }

        // Map the sponsor offer details
        SponsorOfferResponse sponsorOfferResponse = SponsorOfferMapper.toSponsorOfferResponse(sponsorship.getSponsorOffer());

        // Map the sponsor ads associated with this sponsorship
        Set<SponsorAdResponse> sponsorAdResponses = (sponsorship.getSponsorAds() != null)
                ? sponsorship.getSponsorAds().stream()
                .map(SponsorAdMapper::toSponsorAdResponseWithImageUrl)
                .collect(Collectors.toSet())
                : new HashSet<>();

        // Map related providerships (reverse mapping)
        List<ProvidershipLightResponse> providershipLightResponses = (sponsorship.getProviderships() != null)
                ? sponsorship.getProviderships().stream()
                .map(ProvidershipMapper::toProvidershipLightResponse)
                .collect(Collectors.toList())
                : new ArrayList<>();

        // Map supplier transactions associated with this sponsorship
        List<SupplierTransactionLightResponse> supplierTransactionLightResponses = (sponsorship.getSupplierTransactions() != null)
                ? sponsorship.getSupplierTransactions().stream()
                .map(SupplierTransactionMapper::toSupplierTransactionLightResponse)
                .collect(Collectors.toList())
                : new ArrayList<>();

        return new SponsorshipResponse(
                sponsorship.getId(),
                sponsorship.getStatus(),
                sponsorship.getUserId(),
                sponsorOfferResponse,
                sponsorAdResponses,
                sponsorship.getCreatedDate(),
                providershipLightResponses,
                supplierTransactionLightResponses
        );
    }


    public static SponsorshipLightResponse toSponsorshipLightResponse(Sponsorship sponsorship) {
        if (sponsorship == null) {
            return null;
        }

        // Map the sponsor offer details
        SponsorOfferResponse sponsorOfferResponse = SponsorOfferMapper.toSponsorOfferResponse(sponsorship.getSponsorOffer());

        // Map the sponsor ads associated with this sponsorship
        Set<SponsorAdResponse> sponsorAdResponses = (sponsorship.getSponsorAds() != null)
                ? sponsorship.getSponsorAds().stream()
                .map(SponsorAdMapper::toSponsorAdResponseWithImageUrl)
                .collect(Collectors.toSet())
                : new HashSet<>();


        // Map supplier transactions associated with this sponsorship
        List<SupplierTransactionLightResponse> supplierTransactionLightResponses = (sponsorship.getSupplierTransactions() != null)
                ? sponsorship.getSupplierTransactions().stream()
                .map(SupplierTransactionMapper::toSupplierTransactionLightResponse)
                .collect(Collectors.toList())
                : new ArrayList<>();

        return new SponsorshipLightResponse(
                sponsorship.getId(),
                sponsorship.getStatus(),
                sponsorship.getUserId(),
                sponsorOfferResponse,
                sponsorAdResponses,
                sponsorship.getCreatedDate(),
                supplierTransactionLightResponses
        );

    }

    public static SponsorshipLightsResponse toSponsorshipLightsResponse(Sponsorship sponsorship) {
        if (sponsorship == null) {
            return null;
        }

        // Map the sponsor offer details
        SponsorOfferResponse sponsorOfferResponse = SponsorOfferMapper.toSponsorOfferResponse(sponsorship.getSponsorOffer());

        // Map the sponsor ads associated with this sponsorship
        Set<SponsorAdResponse> sponsorAdResponses = (sponsorship.getSponsorAds() != null)
                ? sponsorship.getSponsorAds().stream()
                .map(SponsorAdMapper::toSponsorAdResponseWithImageUrl)
                .collect(Collectors.toSet())
                : new HashSet<>();

        // Map related providerships (reverse mapping)
        //.map(ProvidershipMapper::toProvidershipResponse)
        List<ProvidershipLightResponse> providershipResponses = (sponsorship.getProviderships() != null)
                ? sponsorship.getProviderships().stream()
                .map(ProvidershipMapper::toProvidershipLightResponse)
                .collect(Collectors.toList())
                : new ArrayList<>();


        return new SponsorshipLightsResponse(
                sponsorship.getId(),
                sponsorship.getStatus(),
                sponsorship.getUserId(),
                sponsorOfferResponse,
                sponsorAdResponses,
                sponsorship.getCreatedDate(),
                providershipResponses

        );

    }
}
