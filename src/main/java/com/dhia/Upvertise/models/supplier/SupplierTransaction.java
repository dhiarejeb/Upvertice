package com.dhia.Upvertise.models.supplier;

import com.dhia.Upvertise.models.common.BaseEntity;
import com.dhia.Upvertise.models.user.Supplier;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "supplier_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupplierTransaction extends BaseEntity {

    private Integer quantityPurchased;
    private Double totalPrice;
    @Column(name = "user_id")
    private String userId;
    //@ManyToOne
    //@JoinColumn(name = "supplier_id")
    //private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "supplier_offer_id")
    private SupplierOffer supplierOffer;


}
