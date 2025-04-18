package com.dhia.Upvertise.models.provider;

import com.dhia.Upvertise.models.common.BaseEntity;
import com.dhia.Upvertise.models.sponsorship.Sponsorship;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Table(name = "providerships")
public class Providership extends BaseEntity {

    @ManyToOne()
    @JoinColumn(name = "sponsorship_id", nullable = true)
    private Sponsorship sponsorship;

    @Column(name = "user_id")
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProvidershipStatus status;

    @Column(name = "produced_cups")
    private Integer producedProduct ;

    @Column(name = "total_cups", nullable = false)
    private Integer totalProduct; // Required: Total cups provider can produce

    @Column(name = "bonus_earned")
    private Double bonusEarned ;
    //private BigDecimal bonusEarned = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "admin_approval_status", nullable = false)
    private ProvidershipApprovalStatus providershipApprovalStatus = ProvidershipApprovalStatus.PENDING;

    @Column(name = "proof_docs")
    @ElementCollection
    private List<String> proofDocs;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "has_print_machine", nullable = false)
    private Boolean hasPrintMachine;

    @ElementCollection(targetClass = ProvidedProductType.class)
    @CollectionTable(name = "providership_product_types", joinColumns = @JoinColumn(name = "providership_id"))
    @Column(name = "product_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<ProvidedProductType> providedProductTypes;
}
