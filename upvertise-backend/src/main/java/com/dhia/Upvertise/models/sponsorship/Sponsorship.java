package com.dhia.Upvertise.models.sponsorship;

import com.dhia.Upvertise.models.common.BaseEntity;
import com.dhia.Upvertise.models.provider.Providership;
import com.dhia.Upvertise.models.supplier.SupplierTransaction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "sponsorships")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Sponsorship extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private SponsorshipStatus status;
    @Column(name = "user_id")
    private String userId;

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

    @OneToMany(mappedBy = "sponsorship", cascade = CascadeType.ALL)
    private List<Providership> providerships = new ArrayList<>();

    @OneToMany(mappedBy = "sponsorship", cascade = CascadeType.ALL)
    private List<SupplierTransaction> supplierTransactions = new ArrayList<>();




}
