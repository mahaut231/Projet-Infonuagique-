package com.cloudwebframeworks.mvcjpa.repository;

import com.cloudwebframeworks.mvcjpa.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
