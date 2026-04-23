package com.capco.transaction.service;

import com.capco.transaction.domain.Transaction;
import com.capco.transaction.domain.TransactionRepository;
import com.capco.transaction.domain.TransactionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);
    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Transaction submitTransaction(Transaction transaction) {
        log.info("Storing new transaction: {}", transaction.getTransactionId());
        transaction.setStatus(TransactionStatus.PENDING);
        return repository.save(transaction);
    }

    public List<Transaction> getPendingTransactions() {
        return repository.findByStatus(TransactionStatus.PENDING);
    }

    @Transactional
    public void updateStatus(Long id, TransactionStatus status, String failureReason) {
        repository.findById(id).ifPresent(tx -> {
            tx.setStatus(status);
            tx.setFailureReason(failureReason);
            if (status == TransactionStatus.SUCCESS) {
                tx.setProcessedAt(java.time.OffsetDateTime.now());
            }
            repository.save(tx);
        });
    }

    public List<Transaction> getAllTransactions() {
        return repository.findAll();
    }
    
    public Optional<Transaction> getByTransactionId(String transactionId) {
        return repository.findByTransactionId(transactionId);
    }
}
