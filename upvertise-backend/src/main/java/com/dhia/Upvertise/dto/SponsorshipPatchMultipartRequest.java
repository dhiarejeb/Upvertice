package com.dhia.Upvertise.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

    @Schema(description = "Wrapper for multipart Sponsorship patch request")
    public class SponsorshipPatchMultipartRequest {

        @Schema(description = "Sponsorship patch request data as JSON string")
        public String request;

        @Schema(description = "Image file (optional)")
        public MultipartFile image;
    }

