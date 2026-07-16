package com.digibank.account.repository;

import com.digibank.account.model.Account;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Stateless
public class AccountRepository {

    @PersistenceContext(unitName = "digibank-pu")
    private EntityManager em;

    public Account save(Account account) {
        if (account.getId() == null) {
            em.persist(account);
            return account;
        } else {
            return em.merge(account);
        }
    }

    public Optional<Account> findById(Long id) {
        return Optional.ofNullable(em.find(Account.class, id));
    }

    public List<Account> findAll() {
        return em.createQuery("SELECT a FROM Account a ORDER BY a.accountNumber", Account.class)
                .getResultList();
    }

    public List<Account> findByCustomerId(Long customerId) {
        return em.createQuery("SELECT a FROM Account a WHERE a.customerId = :customerId ORDER BY a.accountNumber", Account.class)
                .setParameter("customerId", customerId)
                .getResultList();
    }

    public void delete(Account account) {
        em.remove(em.contains(account) ? account : em.merge(account));
    }
}
