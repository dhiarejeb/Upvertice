package com.dhia.Upvertise.dto;


import com.dhia.Upvertise.models.supplier.SupplierOfferStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

public record SupplierOfferRequest(
        @NotBlank(message = "Title cannot be blank") String title,
        @NotBlank(message = "Description cannot be blank") String description,

        @NotNull(message = "Quantity available is required")
        @PositiveOrZero(message = "Quantity must be zero or positive") Integer quantityAvailable,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive") Double price,

        @NotNull(message = "Start date is required")
        @FutureOrPresent(message = "Start date must be in the present or future") LocalDate startDate,

        @NotNull(message = "End date is required")
        @FutureOrPresent(message = "End date must be in the present or future") LocalDate endDate,

        @NotNull(message = "Status is required") SupplierOfferStatus status,
        List<@NotNull(message = "SponsorAd IDs cannot be null") Integer> sponsorAdIds





) {
}
