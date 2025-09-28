package com.jpmc.midascore.repository;

import com.jpmc.midascore.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    // You can add custom queries later if needed
}
