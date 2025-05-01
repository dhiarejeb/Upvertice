package com.dhia.Upvertise.dto;

import com.dhia.Upvertise.models.supplier.SupplierTransactionStatus;

import java.util.List;

public record SupplierTransactionUpdateRequest(
        Integer quantitySold,

        SupplierTransactionStatus status,

        List<String> locations,

        Double discount
) {

}
