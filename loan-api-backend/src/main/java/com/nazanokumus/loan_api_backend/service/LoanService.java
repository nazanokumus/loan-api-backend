package com.nazanokumus.loan_api_backend.service;

import com.nazanokumus.loan_api_backend.entity.Customer;
import com.nazanokumus.loan_api_backend.entity.Loan;
import com.nazanokumus.loan_api_backend.entity.LoanInstallment;
import com.nazanokumus.loan_api_backend.repository.CustomerRepository;
import com.nazanokumus.loan_api_backend.repository.LoanInstallmentRepository;
import com.nazanokumus.loan_api_backend.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;


@Service
public class LoanService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private LoanInstallmentRepository loanInstallmentRepository;

    // Create a new loan for a given customer
    public Loan createLoan(Long customerId, Double amount, Double interestRate, Integer numberOfInstallments) {
        // Validate number of installments
        List<Integer> validInstallments = Arrays.asList(6, 9, 12, 24);
        if (!validInstallments.contains(numberOfInstallments)) {
            throw new IllegalArgumentException("Invalid number of installments");
        }

        // Validate interest rate
        if (interestRate < 0.1 || interestRate > 0.5) {
            throw new IllegalArgumentException("Invalid interest rate");
        }

        // Find the customer
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid customer ID"));

        // Check if customer has enough credit limit
        double totalLoanAmount = amount * (1 + interestRate);
        if (customer.getCreditLimit() - customer.getUsedCreditLimit() < totalLoanAmount) {
            throw new IllegalArgumentException("Insufficient credit limit");
        }

        // Create loan
        Loan loan = new Loan();
        loan.setCustomerId(customerId);
        loan.setLoanAmount(totalLoanAmount);
        loan.setNumberOfInstallments(numberOfInstallments);
        loan.setCreateDate(new Date());
        loan.setIsPaid(false);
        loanRepository.save(loan);

        // Create loan installments
        LocalDate dueDate = LocalDate.now().withDayOfMonth(1).plusMonths(1);
        double installmentAmount = totalLoanAmount / numberOfInstallments;
        for (int i = 0; i < numberOfInstallments; i++) {
            LoanInstallment installment = new LoanInstallment();
            installment.setLoanId(loan.getId());
            installment.setAmount(installmentAmount);
            installment.setPaidAmount(0.0);
            installment.setDueDate(Date.from(dueDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            installment.setIsPaid(false);
            loanInstallmentRepository.save(installment);
            dueDate = dueDate.plusMonths(1);
        }

        // Update customer's used credit limit
        customer.setUsedCreditLimit(customer.getUsedCreditLimit() + totalLoanAmount);
        customerRepository.save(customer);

        return loan;
    }

    // List loans for a given customer
    public List<Loan> listLoans(Long customerId) {
        return loanRepository.findByCustomerId(customerId);
    }

    // List installments for a given loan
    public List<LoanInstallment> listInstallments(Long loanId) {
        return loanInstallmentRepository.findByLoanId(loanId);
    }

    // Pay installment for a given loan and amount
    public void payLoan(Long loanId, Double amount) {
        // Find the loan by loanId
        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new IllegalArgumentException("Invalid loan ID"));

        // Check if the loan is already fully paid
        if (loan.getIsPaid()) {
            throw new IllegalStateException("Loan is already fully paid");
        }

        // Get the installments for the loan and sort by due date
        List<LoanInstallment> installments = loanInstallmentRepository.findByLoanId(loanId);
        installments.sort(Comparator.comparing(LoanInstallment::getDueDate));

        double remainingAmount = amount;
        int installmentsPaid = 0;
        double totalAmountSpent = 0;

        for (LoanInstallment installment : installments) {
            // Convert Date to LocalDate
            LocalDate dueDate = installment.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            // Skip installments that are not due yet (more than 3 months in the future)
            if (dueDate.isAfter(LocalDate.now().plusMonths(3))) {
                break;
            }

            // Check if installment is already paid
            if (installment.getIsPaid()) {
                continue;
            }

            // Calculate the amount needed to pay this installment
            double amountNeeded = installment.getAmount() - installment.getPaidAmount();

            if (remainingAmount >= amountNeeded) {
                // Pay the installment fully
                installment.setPaidAmount(installment.getAmount());
                installment.setIsPaid(true);
                installment.setPaymentDate(new Date());
                remainingAmount -= amountNeeded;
                totalAmountSpent += amountNeeded;
                installmentsPaid++;
                loanInstallmentRepository.save(installment); // Save the installment
            } else {
                // Not enough amount to pay this installment fully, so stop here
                break;
            }
        }

        // Update the loan status if all installments are paid
        boolean allPaid = installments.stream().allMatch(LoanInstallment::getIsPaid);
        loan.setIsPaid(allPaid);
        loanRepository.save(loan);

        // Return result information (could be through a custom response object)
        System.out.println("Installments paid: " + installmentsPaid);
        System.out.println("Total amount spent: " + totalAmountSpent);
        System.out.println("Loan fully paid: " + loan.getIsPaid());
    }
}
