package com.capco.transaction.service;

import com.capco.transaction.domain.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransactionScheduler {

    private static final Logger log = LoggerFactory.getLogger(TransactionScheduler.class);
    private final TransactionService transactionService;
    private final AsyncTransactionProcessor processor;

    public TransactionScheduler(TransactionService transactionService, AsyncTransactionProcessor processor) {
        this.transactionService = transactionService;
        this.processor = processor;
    }

    @Scheduled(fixedRateString = "${app.scheduling.rate:600000}")
    public void processPendingTransactions() {
        log.info("Scheduled task started: Fetching pending transactions...");
        
        List<Transaction> pending = transactionService.getPendingTransactions();
        log.info("Found {} pending transactions to process", pending.size());

        for (Transaction tx : pending) {
            processor.processTransaction(tx);
        }
        
        log.info("Scheduled task handoff complete.");
    }
}
