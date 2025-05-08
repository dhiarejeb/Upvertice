package com.dhia.Upvertise.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Data
public class SponsorOfferMultipartChooseRequest {

    @Schema(
            description = "SponsorAdRequest serialized as JSON string",
            type = "string",
            format = "json"
    )
    private String request;

    @Schema(
            description = "Optional image files for the ad design"
    )
    private List<MultipartFile> images;
}
