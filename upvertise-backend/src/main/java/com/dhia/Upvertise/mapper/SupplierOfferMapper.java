package com.dhia.Upvertise.mapper;

import com.dhia.Upvertise.dto.SponsorAdResponse;
import com.dhia.Upvertise.dto.SupplierOfferRequest;
import com.dhia.Upvertise.dto.SupplierOfferResponse;
import com.dhia.Upvertise.models.sponsorship.SponsorAd;
import com.dhia.Upvertise.models.supplier.SupplierOffer;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

// SupplierOfferMapper

public class SupplierOfferMapper {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
    public static SupplierOfferResponse toResponse(SupplierOffer supplierOffer) {
        return new SupplierOfferResponse(
                supplierOffer.getId(),
                supplierOffer.getTitle(),
                supplierOffer.getDescription(),
                supplierOffer.getQuantityAvailable(),
                supplierOffer.getPrice(),
                supplierOffer.getStartDate().format(formatter),  // Format LocalDate to String
                supplierOffer.getEndDate().format(formatter),
                supplierOffer.getStatus(),
                supplierOffer.getImageUrl(),
                supplierOffer.getSponsorAds().stream()
                        .map(ad -> new SponsorAdResponse(
                                ad.getId(),
                                ad.getTitle(),
                                ad.getContent(),
                                ad.getDesign(),
                                new ArrayList<>(ad.getDesignColors())
                        ))
                        .toList()
        );
    }


    public static SupplierOffer toSupplierOffer(SupplierOfferRequest request, List<SponsorAd> sponsorAds) {
        return SupplierOffer.builder()
                .title(request.title())
                .description(request.description())
                .quantityAvailable(request.quantityAvailable())
                .price(request.price())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .status(request.status())
                .sponsorAds(new HashSet<>(sponsorAds)) // Convert List to Set
                //.sponsorAds(sponsorAds) // Set fetched SponsorAds
                .build();
        // Image URL will be set after Cloudinary upload
    }
    public static void updateSupplierOffer(SupplierOffer entity, SupplierOfferRequest request) {
        entity.setTitle(request.title());
        entity.setDescription(request.description());
        entity.setQuantityAvailable(request.quantityAvailable());
        entity.setPrice(request.price());
        entity.setStartDate(request.startDate());
        entity.setEndDate(request.endDate());
        entity.setStatus(request.status());


    }

    public static SupplierOfferResponse toResponseWithImageUrl(SupplierOffer supplierOffer) {
        return new SupplierOfferResponse(
                supplierOffer.getId(),
                supplierOffer.getTitle(),
                supplierOffer.getDescription(),
                supplierOffer.getQuantityAvailable(),
                supplierOffer.getPrice(),
                supplierOffer.getStartDate().format(formatter), // Convert to String
                supplierOffer.getEndDate().format(formatter),   // Convert to String
                supplierOffer.getStatus(),
                supplierOffer.getImageUrl(),
                supplierOffer.getSponsorAds().stream()
                        .map(ad -> new SponsorAdResponse(
                                ad.getId(),
                                ad.getTitle(),
                                ad.getContent(),
                                ad.getDesign(),
                                new ArrayList<>(ad.getDesignColors()) // <-- convert Set to List for the DTO
                                //ad.getDesignColors() // make sure this method name is correct
                        ))
                        .toList()
        );
    }
    }


