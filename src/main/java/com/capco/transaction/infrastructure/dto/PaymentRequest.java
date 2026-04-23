package com.capco.transaction.infrastructure.dto;

import java.math.BigDecimal;

public class PaymentRequest {
    private String transactionId;
    private BigDecimal amount;
    private String currency;
    private String payee;

    public PaymentRequest() {}

    public PaymentRequest(String transactionId, BigDecimal amount, String currency, String payee) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.currency = currency;
        this.payee = payee;
    }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getPayee() { return payee; }
    public void setPayee(String payee) { this.payee = payee; }

    public static PaymentRequestBuilder builder() {
        return new PaymentRequestBuilder();
    }

    public static class PaymentRequestBuilder {
        private String transactionId;
        private BigDecimal amount;
        private String currency;
        private String payee;

        public PaymentRequestBuilder transactionId(String transactionId) { this.transactionId = transactionId; return this; }
        public PaymentRequestBuilder amount(BigDecimal amount) { this.amount = amount; return this; }
        public PaymentRequestBuilder currency(String currency) { this.currency = currency; return this; }
        public PaymentRequestBuilder payee(String payee) { this.payee = payee; return this; }

        public PaymentRequest build() {
            return new PaymentRequest(transactionId, amount, currency, payee);
        }
    }
}
