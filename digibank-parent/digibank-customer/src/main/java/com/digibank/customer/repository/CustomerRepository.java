package com.digibank.customer.repository;

import com.digibank.customer.model.Customer;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Stateless
public class CustomerRepository {

    @PersistenceContext(unitName = "digibank-pu")
    private EntityManager em;

    public Customer save(Customer customer) {
        if (customer.getId() == null) {
            em.persist(customer);
            return customer;
        } else {
            return em.merge(customer);
        }
    }

    public Optional<Customer> findById(Long id) {
        return Optional.ofNullable(em.find(Customer.class, id));
    }

    public List<Customer> findAll() {
        return em.createQuery("SELECT c FROM Customer c ORDER BY c.lastName, c.firstName", Customer.class)
                .getResultList();
    }

    public void delete(Customer customer) {
        em.remove(em.contains(customer) ? customer : em.merge(customer));
    }

    public Optional<Customer> findByEmail(String email) {
        List<Customer> results = em.createQuery(
                        "SELECT c FROM Customer c WHERE c.email = :email", Customer.class)
                .setParameter("email", email)
                .getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
}
