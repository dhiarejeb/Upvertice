package com.dhia.Upvertise.models.sponsorship;

import com.dhia.Upvertise.models.common.BaseEntity;
import com.dhia.Upvertise.models.user.Admin;
import com.dhia.Upvertise.models.user.Provider;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "sponsor_offers")
@Getter
@Setter
@SuperBuilder
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SponsorOffer extends BaseEntity {

    @Enumerated(EnumType.STRING)  // Store as a string in the database
    private SponsorOfferStatus status;
    private String title;
    private String description;
    private Double price;
    private String category;
    private String productType;
    private Integer productQuantity;
    private String salesArea;
    @ElementCollection
    private List<String> explainImages;
    private Integer numberAds;
    @Column(name = "user_id")
    private String userId;



    //@ManyToOne
    //@JoinColumn(name = "provider_id")
    //private Provider provider;

    //@ManyToOne
    //@JoinColumn(name = "admin_id")
    //private Admin admin;

    @OneToMany(mappedBy = "sponsorOffer")
    private List<Sponsorship> sponsorships;




}
