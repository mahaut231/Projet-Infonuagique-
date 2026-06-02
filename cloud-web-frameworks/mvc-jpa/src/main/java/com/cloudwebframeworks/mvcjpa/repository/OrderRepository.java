package com.cloudwebframeworks.mvcjpa.repository;

import com.cloudwebframeworks.mvcjpa.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Charge la commande avec ses items et produits en une seule requête
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.product WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);

    // Commandes d'un client avec items (évite le N+1)
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.product WHERE o.customer.id = :customerId")
    List<Order> findByCustomerIdWithItems(@Param("customerId") Long customerId);
}
