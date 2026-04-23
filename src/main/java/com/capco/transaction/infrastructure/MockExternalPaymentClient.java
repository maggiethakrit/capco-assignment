package com.capco.transaction.infrastructure;

import com.capco.transaction.infrastructure.dto.PaymentRequest;
import com.capco.transaction.infrastructure.dto.PaymentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Random;

@Component
public class MockExternalPaymentClient implements ExternalPaymentClient {

    private static final Logger log = LoggerFactory.getLogger(MockExternalPaymentClient.class);
    private final Random random = new Random();

    @Override
    public PaymentResponse submitPayment(PaymentRequest request) {
        log.info("Dispatching payment to external gateway: {}", request.getTransactionId());
        
        // Simulate network delay
        try {
            Thread.sleep(500 + random.nextInt(1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Simulate random failures (10% chance)
        if (random.nextInt(10) == 0) {
            log.error("External gateway failure for transaction: {}", request.getTransactionId());
            throw new RuntimeException("External Gateway Timeout/Error");
        }

        return PaymentResponse.builder()
                .status("success")
                .processedAt(OffsetDateTime.now())
                .build();
    }
}
