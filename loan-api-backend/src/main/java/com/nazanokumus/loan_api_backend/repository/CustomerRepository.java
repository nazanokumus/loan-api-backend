package com.nazanokumus.loan_api_backend.repository;

import com.nazanokumus.loan_api_backend.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
