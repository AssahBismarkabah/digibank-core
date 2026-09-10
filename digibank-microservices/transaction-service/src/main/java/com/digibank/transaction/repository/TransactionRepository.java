package com.digibank.transaction.repository;
import com.digibank.transaction.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface TransactionRepository extends JpaRepository<Transaction, Long> { List<Transaction> findByAccountIdOrderByTransactionDateDesc(Long accountId); }
