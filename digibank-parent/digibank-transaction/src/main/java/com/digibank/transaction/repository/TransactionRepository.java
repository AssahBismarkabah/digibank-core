package com.digibank.transaction.repository;

import com.digibank.transaction.model.Transaction;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Stateless
public class TransactionRepository {

    @PersistenceContext(unitName = "digibank-pu")
    private EntityManager em;

    public Transaction save(Transaction transaction) {
        if (transaction.getId() == null) {
            em.persist(transaction);
            return transaction;
        } else {
            return em.merge(transaction);
        }
    }

    public Optional<Transaction> findById(Long id) {
        return Optional.ofNullable(em.find(Transaction.class, id));
    }

    public List<Transaction> findAll() {
        return em.createQuery(
                        "SELECT t FROM Transaction t ORDER BY t.transactionDate DESC", Transaction.class)
                .getResultList();
    }

    public List<Transaction> findByAccountId(Long accountId) {
        return em.createQuery(
                        "SELECT t FROM Transaction t WHERE t.accountId = :accountId ORDER BY t.transactionDate DESC",
                        Transaction.class)
                .setParameter("accountId", accountId)
                .getResultList();
    }

    public void delete(Transaction transaction) {
        em.remove(em.contains(transaction) ? transaction : em.merge(transaction));
    }
}
