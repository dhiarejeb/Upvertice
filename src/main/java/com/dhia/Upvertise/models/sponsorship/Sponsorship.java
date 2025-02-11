package com.dhia.Upvertise.models.sponsorship;

import com.dhia.Upvertise.models.common.BaseEntity;
import com.dhia.Upvertise.models.user.Admin;
import com.dhia.Upvertise.models.user.Sponsor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "sponsorships")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Sponsorship extends BaseEntity {

    private SponsorshipStatus status;
    @Column(name = "user_id")
    private String userId;

    //@ManyToOne
    //@JoinColumn(name = "sponsor_id")
    //private Sponsor sponsor;

    //@ManyToOne
    //@JoinColumn(name = "admin_id")
    //private Admin admin;

    @ManyToOne
    @JoinColumn(name = "sponsor_offer_id")
    private SponsorOffer sponsorOffer;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "sponsorship_sponsor_ad",
            joinColumns = @JoinColumn(name = "sponsorship_id"),
            inverseJoinColumns = @JoinColumn(name = "sponsor_ad_id")
    )
    private Set<SponsorAd> sponsorAds = new HashSet<>();



}
