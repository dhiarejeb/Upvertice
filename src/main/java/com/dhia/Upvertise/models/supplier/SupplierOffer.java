package com.dhia.Upvertise.models.supplier;

import com.dhia.Upvertise.models.common.BaseEntity;
import com.dhia.Upvertise.models.sponsorship.SponsorAd;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "supplier_offer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupplierOffer extends BaseEntity {

    private Integer quantityAvailable;
    private Double price;
    @Enumerated(EnumType.STRING)
    private  SupplierOfferStatus status;

    @OneToMany(mappedBy = "supplierOffer")
    private List<SupplierTransaction> transactions;

    @ManyToMany
    @JoinTable(
            name = "supplier_offer_sponsor_ad",
            joinColumns = @JoinColumn(name = "supplier_offer_id"),
            inverseJoinColumns = @JoinColumn(name = "sponsor_ad_id")
    )
    private List<SponsorAd> sponsorAds;


}
