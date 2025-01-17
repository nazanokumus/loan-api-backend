# Loan API Backend
This project is a Spring Boot-based backend API for managing loans, customers, and loan installments. It allows creating loans, listing loans, managing loan installments, and making payments.

## Technologies Used
- **Spring Boot** for the main framework.
- **Spring Data JPA** for database interaction.
- **H2 Database** for an in-memory database during development (optional).
- **JUnit 5** and **Mockito** for testing.

## Features
- **Loan Creation**: Create a loan with specified interest rate and installment duration.
- **Loan Listing**: List all loans for a specific customer.
- **Installment Listing**: List all installments for a specific loan.
- **Loan Payment**: Pay a loan installment and update the loan status.

## Setup and Installation

### 1. Clone the repository
git clone https://github.com/yourusername/loan-api-backend.git
cd loan-api-backend
2. Configure your database (if you're using one)
You can configure the application to use an in-memory database (H2) for testing purposes. For production, you may want to configure MySQL or PostgreSQL.

3. Run the application

./mvnw spring-boot:run
The API will be available at http://localhost:8080.

4. Access the API endpoints
POST /api/loans/create: Create a new loan (Admin access).
GET /api/loans/list: List all loans for a customer (Customer access).
GET /api/loans/installments: List installments for a loan (Customer access).
POST /api/loans/pay: Pay an installment (Customer access).
5. Test the application
The tests are located in the src/test/java folder. To run them, simply use the following command:


./mvnw test
Example Request (POST /api/loans/create)
Request Body:

{
  "customerId": 1,
  "amount": 5000.0,
  "interestRate": 0.2,
  "numberOfInstallments": 12
}
Example Response:
{
  "id": 1,
  "customerId": 1,
  "loanAmount": 6000.0,
  "numberOfInstallments": 12,
  "createDate": "2025-01-17T00:00:00Z",
  "isPaid": false
}

