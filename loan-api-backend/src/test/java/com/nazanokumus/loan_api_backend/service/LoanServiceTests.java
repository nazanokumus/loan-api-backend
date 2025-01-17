package com.nazanokumus.loan_api_backend.service;

import com.nazanokumus.loan_api_backend.entity.Customer;
import com.nazanokumus.loan_api_backend.entity.Loan;
import com.nazanokumus.loan_api_backend.entity.LoanInstallment;
import com.nazanokumus.loan_api_backend.repository.CustomerRepository;
import com.nazanokumus.loan_api_backend.repository.LoanInstallmentRepository;
import com.nazanokumus.loan_api_backend.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class LoanServiceTests {

    @InjectMocks
    private LoanService loanService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private LoanInstallmentRepository loanInstallmentRepository;

    private Customer customer;
    private Loan loan;
    private List<LoanInstallment> installments;

    @BeforeEach
    public void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setCreditLimit(10000.0);
        customer.setUsedCreditLimit(0.0);

        loan = new Loan();
        loan.setId(1L);
        loan.setCustomerId(1L);
        loan.setLoanAmount(6000.0);
        loan.setNumberOfInstallments(12);
        loan.setCreateDate(new Date());
        loan.setIsPaid(false);

        LoanInstallment installment1 = new LoanInstallment();
        installment1.setLoanId(1L);
        installment1.setAmount(500.0);
        installment1.setPaidAmount(0.0);
        installment1.setDueDate(Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        installment1.setIsPaid(false);

        LoanInstallment installment2 = new LoanInstallment();
        installment2.setLoanId(1L);
        installment2.setAmount(500.0);
        installment2.setPaidAmount(0.0);
        installment2.setDueDate(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        installment2.setIsPaid(false);

        installments = Arrays.asList(installment1, installment2);
    }

    @Test
    public void testCreateLoan() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Loan loan = loanService.createLoan(1L, 5000.0, 0.2, 12);

        assertNotNull(loan);
        assertEquals(6000.0, loan.getLoanAmount());
        verify(customerRepository, times(1)).save(any(Customer.class));
        verify(loanRepository, times(1)).save(any(Loan.class));
        verify(loanInstallmentRepository, times(12)).save(any(LoanInstallment.class));
    }

    @Test
    public void testListLoans() {
        when(loanRepository.findByCustomerId(1L)).thenReturn(Collections.singletonList(loan));

        List<Loan> result = loanService.listLoans(1L);

        assertEquals(1, result.size());
        assertEquals(loan, result.get(0));
    }

    @Test
    public void testListInstallments() {
        when(loanInstallmentRepository.findByLoanId(1L)).thenReturn(installments);

        List<LoanInstallment> result = loanService.listInstallments(1L);

        assertEquals(2, result.size());
        assertEquals(installments.get(0), result.get(0));
        assertEquals(installments.get(1), result.get(1));
    }

    @Test
    public void testPayLoan() {
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanInstallmentRepository.findByLoanId(1L)).thenReturn(installments);

        loanService.payLoan(1L, 1000.0);

        verify(loanInstallmentRepository, times(2)).save(any(LoanInstallment.class));
        verify(loanRepository, times(1)).save(any(Loan.class));
    }
}
