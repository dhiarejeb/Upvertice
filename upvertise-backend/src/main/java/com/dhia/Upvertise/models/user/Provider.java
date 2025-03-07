package com.dhia.Upvertise.models.user;

import com.dhia.Upvertise.models.common.BaseEntity;
import com.dhia.Upvertise.models.sponsorship.SponsorOffer;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

//@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@DiscriminatorValue("PROVIDER")
public class Provider extends BaseEntity {
    @Column(name = "user_id")
    private String userId;
    private Integer provider_discount ;
    @OneToMany(mappedBy = "provider")
    private List<SponsorOffer> sponsorOffers;


}
