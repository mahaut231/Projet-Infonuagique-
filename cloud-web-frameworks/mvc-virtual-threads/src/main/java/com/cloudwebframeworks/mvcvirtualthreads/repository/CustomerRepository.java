package com.cloudwebframeworks.mvcvirtualthreads.repository;

import com.cloudwebframeworks.mvcvirtualthreads.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
