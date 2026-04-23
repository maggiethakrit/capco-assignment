package com.capco.transaction.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String transactionId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private String payee;

    @Column(nullable = false)
    private OffsetDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    private OffsetDateTime processedAt;
    
    private String failureReason;

    public Transaction() {}

    public Transaction(String transactionId, BigDecimal amount, String currency, String payee, OffsetDateTime timestamp) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.currency = currency;
        this.payee = payee;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getPayee() { return payee; }
    public void setPayee(String payee) { this.payee = payee; }

    public OffsetDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(OffsetDateTime timestamp) { this.timestamp = timestamp; }

    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }

    public OffsetDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(OffsetDateTime processedAt) { this.processedAt = processedAt; }

    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }

    public static TransactionBuilder builder() {
        return new TransactionBuilder();
    }

    public static class TransactionBuilder {
        private String transactionId;
        private BigDecimal amount;
        private String currency;
        private String payee;
        private OffsetDateTime timestamp;

        public TransactionBuilder transactionId(String transactionId) { this.transactionId = transactionId; return this; }
        public TransactionBuilder amount(BigDecimal amount) { this.amount = amount; return this; }
        public TransactionBuilder currency(String currency) { this.currency = currency; return this; }
        public TransactionBuilder payee(String payee) { this.payee = payee; return this; }
        public TransactionBuilder timestamp(OffsetDateTime timestamp) { this.timestamp = timestamp; return this; }

        public Transaction build() {
            return new Transaction(transactionId, amount, currency, payee, timestamp);
        }
    }
}
