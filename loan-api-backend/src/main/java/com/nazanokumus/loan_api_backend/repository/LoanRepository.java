package com.nazanokumus.loan_api_backend.repository;

import com.nazanokumus.loan_api_backend.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByCustomerId(Long customerId);
}
