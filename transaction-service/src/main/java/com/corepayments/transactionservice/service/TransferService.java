package com.corepayments.transactionservice.service;

import com.corepayments.transactionservice.client.AccountServiceClient;
import com.corepayments.transactionservice.dto.AmountRequest;
import com.corepayments.transactionservice.dto.TransferRequest;
import com.corepayments.transactionservice.dto.TransferResponse;
import com.corepayments.transactionservice.exception.TransferFailedException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;

@Service
public class TransferService {

    private final AccountServiceClient accountServiceClient;

    public TransferService(AccountServiceClient accountServiceClient) {
        this.accountServiceClient = accountServiceClient;
    }

    /**
     * NOTE: this is intentionally NOT atomic across the two services —
     * debit and credit are two separate network calls. If the credit
     * fails after a successful debit, we attempt a naive compensating
     * credit back to the source account. This compensation is itself
     * not guaranteed (it could also fail) — a deliberate, realistic
     * limitation and a strong future "brownfield" target (e.g. proper
     * saga pattern, outbox pattern, or idempotent retry) rather than
     * something papered over here.
     */
    public TransferResponse transfer(TransferRequest request) {
        try {
            accountServiceClient.debit(request.fromAccountId(), new AmountRequest(request.amount()));
        } catch (RestClientException e) {
            throw new TransferFailedException(
                    "Debit failed for account " + request.fromAccountId(), e);
        }

        try {
            accountServiceClient.credit(request.toAccountId(), new AmountRequest(request.amount()));
        } catch (RestClientException e) {
            attemptCompensatingCredit(request);
            throw new TransferFailedException(
                    "Credit failed for account " + request.toAccountId()
                            + " — attempted compensating refund to " + request.fromAccountId(), e);
        }

        return new TransferResponse(
                request.fromAccountId(),
                request.toAccountId(),
                request.amount(),
                "COMPLETED",
                LocalDateTime.now()
        );
    }

    private void attemptCompensatingCredit(TransferRequest request) {
        try {
            accountServiceClient.credit(request.fromAccountId(), new AmountRequest(request.amount()));
        } catch (RestClientException e) {
            // Compensation itself failed — funds are now genuinely stuck.
            // This is exactly the kind of event an audit/reliability layer
            // needs to catch and escalate, rather than silently swallow.
            throw new TransferFailedException(
                    "CRITICAL: compensating credit also failed for account "
                            + request.fromAccountId() + " — manual intervention required", e);
        }
    }
}