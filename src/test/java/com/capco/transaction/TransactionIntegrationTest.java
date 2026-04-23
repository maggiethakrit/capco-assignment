package com.capco.transaction;

import com.capco.transaction.domain.Transaction;
import com.capco.transaction.domain.TransactionRepository;
import com.capco.transaction.domain.TransactionStatus;
import com.capco.transaction.service.TransactionScheduler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private TransactionScheduler scheduler;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testFullTransactionFlow() throws Exception {
        // 1. Submit a transaction
        Transaction tx = Transaction.builder()
                .transactionId("txn-integration-001")
                .amount(new BigDecimal("1250.75"))
                .currency("USD")
                .payee("john.doe@example.com")
                .timestamp(OffsetDateTime.now())
                .build();

        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tx)))
                .andExpect(status().isCreated());

        // Verify it is stored as PENDING
        Transaction saved = repository.findByTransactionId("txn-integration-001").orElseThrow();
        assertThat(saved.getStatus()).isEqualTo(TransactionStatus.PENDING);

        // 2. Trigger Scheduler manually for testing
        scheduler.processPendingTransactions();

        // 3. Wait for async processing to complete (using Awaitility)
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            Transaction updated = repository.findByTransactionId("txn-integration-001").orElseThrow();
            assertThat(updated.getStatus()).isIn(TransactionStatus.SUCCESS, TransactionStatus.FAILED);
        });
    }
}
