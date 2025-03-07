package com.dhia.Upvertise.repositories;

import com.dhia.Upvertise.models.supplier.SupplierTransaction;
import com.dhia.Upvertise.models.supplier.SupplierTransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierTransactionRepository extends JpaRepository<SupplierTransaction, Integer> {
    Page<SupplierTransaction> findByUserId(String name, Pageable pageable);

    Integer countByUserIdAndSupplierTransactionStatus(String userId, SupplierTransactionStatus supplierTransactionStatus);
}
