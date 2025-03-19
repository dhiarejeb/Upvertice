package com.dhia.Upvertise.models.supplier;

import com.dhia.Upvertise.models.common.BaseEntity;
import com.dhia.Upvertise.models.sponsorship.SponsorAd;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "supplier_offer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SupplierOffer extends BaseEntity {

    private String title ;
    private String description ;
    private Integer quantityAvailable;
    private Double price;
    @Enumerated(EnumType.STRING)
    private  SupplierOfferStatus status; //COMING_SOON,AVAILABLE,CLOSED
    private LocalDate startDate;
    private LocalDate endDate;
    private String imageUrl;

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
