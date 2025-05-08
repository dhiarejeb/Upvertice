package com.dhia.Upvertise.repositories;

import com.dhia.Upvertise.models.supplier.SupplierOffer;
import com.dhia.Upvertise.models.supplier.SupplierOfferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierOfferRepository extends JpaRepository<SupplierOffer, Integer> {
    @EntityGraph(attributePaths = { "sponsorAds.designColors" })
    Page<SupplierOffer> findAll(Pageable pageable);

    Page<SupplierOffer> findByStatus(SupplierOfferStatus status, Pageable pageable);
    Page<SupplierOffer> findByCreatedBy(String createdBy, Pageable pageable);

}
