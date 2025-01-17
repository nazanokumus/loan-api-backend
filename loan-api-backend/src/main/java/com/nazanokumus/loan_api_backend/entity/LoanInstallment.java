package com.nazanokumus.loan_api_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanInstallment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long loanId;
    private Double amount;
    private Double paidAmount;
    private Date dueDate;
    private Date paymentDate;
    private Boolean isPaid;
}