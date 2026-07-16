package com.digibank.compliance.repository;

import com.digibank.compliance.model.ComplianceCheck;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Stateless
public class ComplianceRepository {

    @PersistenceContext(unitName = "digibank-pu")
    private EntityManager em;

    public ComplianceCheck save(ComplianceCheck complianceCheck) {
        if (complianceCheck.getId() == null) {
            em.persist(complianceCheck);
            return complianceCheck;
        } else {
            return em.merge(complianceCheck);
        }
    }

    public Optional<ComplianceCheck> findById(Long id) {
        return Optional.ofNullable(em.find(ComplianceCheck.class, id));
    }

    public List<ComplianceCheck> findAll() {
        return em.createQuery(
                        "SELECT c FROM ComplianceCheck c ORDER BY c.checkDate DESC", ComplianceCheck.class)
                .getResultList();
    }

    public List<ComplianceCheck> findByCustomerId(Long customerId) {
        return em.createQuery(
                        "SELECT c FROM ComplianceCheck c WHERE c.customerId = :customerId ORDER BY c.checkDate DESC",
                        ComplianceCheck.class)
                .setParameter("customerId", customerId)
                .getResultList();
    }

    public List<ComplianceCheck> findByStatus(ComplianceCheck.CheckStatus status) {
        return em.createQuery(
                        "SELECT c FROM ComplianceCheck c WHERE c.status = :status ORDER BY c.checkDate DESC",
                        ComplianceCheck.class)
                .setParameter("status", status)
                .getResultList();
    }

    public void delete(ComplianceCheck complianceCheck) {
        em.remove(em.contains(complianceCheck) ? complianceCheck : em.merge(complianceCheck));
    }
}
