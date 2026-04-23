package com.capco.transaction.infrastructure;

import com.capco.transaction.infrastructure.dto.PaymentRequest;
import com.capco.transaction.infrastructure.dto.PaymentResponse;

public interface ExternalPaymentClient {
    PaymentResponse submitPayment(PaymentRequest request);
}
