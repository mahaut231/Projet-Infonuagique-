package com.cloudwebframeworks.mvcvirtualthreads.repository;

import com.cloudwebframeworks.mvcvirtualthreads.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
