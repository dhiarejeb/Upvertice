package com.dhia.Upvertise.models.sponsorship;

import com.dhia.Upvertise.models.common.BaseEntity;
import com.dhia.Upvertise.models.supplier.SupplierOffer;
import com.dhia.Upvertise.models.user.Sponsor;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "sponsor_ad")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor

public class SponsorAd extends BaseEntity {

    private String title;
    private String content; // Ad content
    //private String placement;// Where the ad is placed on the goblet
    private String design;
    @ElementCollection
    private List<String> design_colors;

    //@ManyToOne
    //@JoinColumn(name = "sponsor_id")
    //private Sponsor sponsor;
    @Column(name = "user_id")
    private String userId;

    @ManyToMany(mappedBy = "sponsorAds")
    private Set<Sponsorship> sponsorships = new HashSet<>();

    @ManyToMany(mappedBy = "sponsorAds")
    private List<SupplierOffer> supplierOffers;


}
