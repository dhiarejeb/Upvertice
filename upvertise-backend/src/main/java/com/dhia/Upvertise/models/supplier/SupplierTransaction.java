package com.dhia.Upvertise.models.supplier;

import com.dhia.Upvertise.models.common.BaseEntity;
import com.dhia.Upvertise.models.sponsorship.Sponsorship;
import com.dhia.Upvertise.models.sponsorship.SponsorshipStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "supplier_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupplierTransaction extends BaseEntity {
    @Enumerated(EnumType.STRING)
    private SupplierTransactionStatus supplierTransactionStatus;

    @Column(name = "user_id")
    private String userId;

    private Integer quantitySold;
    private Double relativePrice;
    private Double percentage;
    private Double discount = 0.0; // Default no discount
    @Column(name = "proofs")
    @ElementCollection
    private List<String> proofs;
    @Column(name = "locations")
    @ElementCollection
    private List<String> locations;



    @ManyToOne
    @JoinColumn(name = "supplier_offer_id")
    private SupplierOffer supplierOffer;

    @ManyToOne
    @JoinColumn(name = "sponsorship_id")
    private Sponsorship  sponsorship;




}
