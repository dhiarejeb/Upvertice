package com.dhia.Upvertise.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Getter
@Setter
@Schema(name = "SponsorOfferMultipartRequest", description = "Multipart form request for sponsor offer")
public class SponsorOfferMultipartRequest {

    @Schema(description = "Sponsor Offer JSON string", type = "string", format = "json")
    private String request;

    @Schema(description = "List of images", type = "array", format = "binary")
    private List<MultipartFile> explainImages;

}
