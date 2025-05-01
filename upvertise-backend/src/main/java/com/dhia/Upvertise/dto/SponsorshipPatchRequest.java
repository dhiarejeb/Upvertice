package com.dhia.Upvertise.dto;

import com.dhia.Upvertise.models.sponsorship.SponsorshipStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Set;
@Data
@Schema(description = "Sponsorship patch request")
public class SponsorshipPatchRequest {

    @Schema(description = "New sponsorship status")
    private SponsorshipStatus newStatus;

    @Schema(description = "Sponsor ad data")
    private SponsorAdRequest sponsorAdData;
}
