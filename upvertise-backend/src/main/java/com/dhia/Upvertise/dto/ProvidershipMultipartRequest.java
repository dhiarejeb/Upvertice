package com.dhia.Upvertise.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Schema(description = "Wrapper for multipart providership creation/update request")
public class ProvidershipMultipartRequest {

    @Schema(description = "Providership data as JSON string")
    public String request;

    @Schema(description = "Proof images (optional)")
    public List<MultipartFile> images;
}

