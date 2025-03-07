package com.dhia.Upvertise.models.user;

import com.dhia.Upvertise.models.common.BaseEntity;
import com.dhia.Upvertise.models.sponsorship.SponsorAd;
import com.dhia.Upvertise.models.sponsorship.Sponsorship;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

//@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@DiscriminatorValue("SPONSOR")
public class Sponsor extends BaseEntity {
    @Column(name = "user_id")
    private String userId;
    @OneToMany(mappedBy = "sponsor")
    private List<Sponsorship> sponsorships;

    @OneToMany(mappedBy = "sponsor")
    private List<SponsorAd> sponsorAds;


}
