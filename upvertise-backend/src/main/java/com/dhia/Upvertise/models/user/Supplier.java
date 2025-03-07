package com.dhia.Upvertise.models.user;

import com.dhia.Upvertise.models.common.BaseEntity;
import com.dhia.Upvertise.models.supplier.SupplierTransaction;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

//@Entity
//@DiscriminatorValue("SUPPLIER")
public class Supplier extends BaseEntity {
    @Column(name = "user_id")
    private String userId;
    private Integer supplier_discount ;
    @OneToMany(mappedBy = "supplier")
    private List<SupplierTransaction> transactions;


}
