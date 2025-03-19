package com.dhia.Upvertise.mapper;

import com.dhia.Upvertise.dto.*;
import com.dhia.Upvertise.models.provider.Providership;
import com.dhia.Upvertise.models.sponsorship.Sponsorship;



public class ProvidershipMapper {


    /**
     * Maps a Providership entity to a ProvidershipResponse record.
     *
     * @param providership the entity to map
     * @return the mapped response record
     */
    public static ProvidershipResponse toProvidershipResponse(Providership providership) {
        if (providership == null) {
            return null;
        }

        // Map the full sponsorship, which includes its supplier transactions, sponsor ads, sponsor offer, etc.
        SponsorshipLightResponse sponsorshipLightResponse = SponsorshipMapper.toSponsorshipLightResponse(providership.getSponsorship());

        return new ProvidershipResponse(
                providership.getId(),
                sponsorshipLightResponse,
                providership.getUserId(),
                providership.getStatus(),
                providership.getProducedProduct(),
                providership.getTotalProduct(),
                providership.getBonusEarned(),
                providership.getProvidershipApprovalStatus(),
                providership.getProofDocs(),
                providership.getLocation(),
                providership.getHasPrintMachine(),
                providership.getCreatedDate(),
                providership.getLastModifiedDate()
                // No direct supplier transactions list here,
                // as they are included within sponsorshipResponse.
        );
    }

    /**
     * Maps a ProvidershipRequest record to a Providership entity.
     *
     * @param request the request record to map
     * @return the mapped entity
     */
    public Providership toEntity(ProvidershipRequest request) {
        if (request == null) {
            return null;
        }
        Providership providership = new Providership();

        // Create a minimal Sponsorship entity from the provided sponsorshipId
        Sponsorship sponsorship = new Sponsorship();
        sponsorship.setId(request.sponsorshipId());
        providership.setSponsorship(sponsorship);

        providership.setUserId(request.userId());
        providership.setStatus(request.status());
        providership.setProducedProduct(request.producedProduct());
        providership.setTotalProduct(request.totalProduct());
        providership.setBonusEarned(request.bonusEarned());
        providership.setProvidershipApprovalStatus(request.providershipApprovalStatus());
        //providership.setProofDocs(request.proofDocs());

        return providership;
    }

    public static ProvidershipLightResponse toProvidershipLightResponse(Providership providership) {
        if (providership == null) {
            return null;
        }
        return new ProvidershipLightResponse(
                providership.getId(),
                providership.getUserId(),
                providership.getStatus(),
                providership.getProducedProduct(),
                providership.getTotalProduct(),
                providership.getBonusEarned(),
                providership.getProvidershipApprovalStatus(),
                providership.getProofDocs(),
                providership.getLocation(),
                providership.getHasPrintMachine(),
                providership.getCreatedDate(),
                providership.getLastModifiedDate()
                // No direct supplier transactions list here,
                // as they are included within sponsorshipResponse.
        );
    }
}
