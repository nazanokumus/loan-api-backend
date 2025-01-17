package com.nazanokumus.loan_api_backend.controller;

import com.nazanokumus.loan_api_backend.entity.Loan;
import com.nazanokumus.loan_api_backend.entity.LoanInstallment;
import com.nazanokumus.loan_api_backend.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @PostMapping("/create")
    public Loan createLoan(@RequestParam Long customerId, @RequestParam Double amount, @RequestParam Double interestRate, @RequestParam Integer numberOfInstallments) {
        return loanService.createLoan(customerId, amount, interestRate, numberOfInstallments);
    }

    @GetMapping("/list")
    public List<Loan> listLoans(@RequestParam(required = false) Long customerId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"))) {
            // If the role is CUSTOMER, only list loans for the authenticated customer
            // Assuming the username is used as the customer identifier
            customerId = getCustomerIdFromUsername(userDetails.getUsername());
        }

        return loanService.listLoans(customerId);
    }

    @GetMapping("/installments")
    public List<LoanInstallment> listInstallments(@RequestParam Long loanId) {
        return loanService.listInstallments(loanId);
    }

    @PostMapping("/pay")
    public void payLoan(@RequestParam Long loanId, @RequestParam Double amount) {
        loanService.payLoan(loanId, amount);
    }

    private Long getCustomerIdFromUsername(String username) {
        // Logic to get customer ID from username
        // This could involve querying the database with the username
        return 1L; // Example: returning a dummy customer ID
    }
}
