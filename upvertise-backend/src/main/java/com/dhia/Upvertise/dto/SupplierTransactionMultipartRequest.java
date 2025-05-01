package com.dhia.Upvertise.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Schema(name = "SupplierTransactionMultipartRequest", description = "Multipart form request for updating supplier transaction with request and images")
public class SupplierTransactionMultipartRequest {

    @Schema(description = "Supplier Transaction update request as JSON string", type = "string", format = "json")
    private String request;

    @Schema(description = "List of images or proofs related to the supplier transaction", type = "array", format = "binary")
    private List<MultipartFile> images;
}
