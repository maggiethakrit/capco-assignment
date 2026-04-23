package com.capco.transaction.infrastructure.dto;

import java.time.OffsetDateTime;

public class PaymentResponse {
    private String status;
    private OffsetDateTime processedAt;

    public PaymentResponse() {}

    public PaymentResponse(String status, OffsetDateTime processedAt) {
        this.status = status;
        this.processedAt = processedAt;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public OffsetDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(OffsetDateTime processedAt) { this.processedAt = processedAt; }

    public static PaymentResponseBuilder builder() {
        return new PaymentResponseBuilder();
    }

    public static class PaymentResponseBuilder {
        private String status;
        private OffsetDateTime processedAt;

        public PaymentResponseBuilder status(String status) { this.status = status; return this; }
        public PaymentResponseBuilder processedAt(OffsetDateTime processedAt) { this.processedAt = processedAt; return this; }

        public PaymentResponse build() {
            return new PaymentResponse(status, processedAt);
        }
    }
}
