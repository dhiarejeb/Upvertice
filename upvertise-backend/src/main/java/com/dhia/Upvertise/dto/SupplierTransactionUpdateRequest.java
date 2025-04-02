package com.dhia.Upvertise.dto;

import com.dhia.Upvertise.models.supplier.SupplierTransactionStatus;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

public record SupplierTransactionUpdateRequest(
        @JsonProperty("quantitySold")
        @NotNull(message = "Quantity sold is required")
        @PositiveOrZero(message = "Quantity sold must be zero or positive")
        Integer quantitySold,

        @JsonProperty("status")
        @NotNull(message = "Status is required")
        SupplierTransactionStatus status,

        // Uncomment if proofs are required
        // @JsonProperty("proofs")
        // @NotEmpty(message = "Proofs list cannot be empty")
        // List<@NotBlank(message = "Proof document cannot be blank") String> proofs,

        @JsonProperty("locations")
        @NotEmpty(message = "Locations list cannot be empty")
        List<@NotBlank(message = "Location cannot be blank") String> locations
) {
    @JsonCreator
    public SupplierTransactionUpdateRequest {
    }
}
