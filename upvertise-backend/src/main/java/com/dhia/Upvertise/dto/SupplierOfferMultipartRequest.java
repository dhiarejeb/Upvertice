package com.dhia.Upvertise.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "Wrapper for multipart supplier offer creation/update request")
public class SupplierOfferMultipartRequest {

    @Schema(description = "Supplier offer data as JSON string")
    public String request;

    @Schema(description = "Image for the supplier offer (optional)")
    public MultipartFile image;
}
