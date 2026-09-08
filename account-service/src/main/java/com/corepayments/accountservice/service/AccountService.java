package com.corepayments.accountservice.service;

import com.corepayments.accountservice.exception.AccountNotFoundException;
import com.corepayments.accountservice.exception.InsufficientFundsException;
import com.corepayments.accountservice.model.Account;
import com.corepayments.accountservice.model.AccountType;
import com.corepayments.accountservice.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account createAccount(String customerName, AccountType accountType, BigDecimal openingBalance) {
        String accountNumber = generateAccountNumber();
        Account account = new Account(accountNumber, customerName, accountType, openingBalance);
        return accountRepository.save(account);
    }

    public Account getAccount(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("No account found with id " + id));
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public BigDecimal getBalance(Long id) {
        return getAccount(id).getBalance();
    }

    /**
     * NOTE: intentionally simple — no overdraft limit, no daily transfer cap.
     * This is a deliberate target for a future "brownfield" agent task
     * (e.g. "add a daily transfer limit"), where the existing test suite's
     * pass/fail is the objective signal for whether the change was safe.
     */
    public Account debit(Long id, BigDecimal amount) {
        Account account = getAccount(id);
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(
                    "Account " + account.getAccountNumber() + " has insufficient funds for this debit");
        }
        account.setBalance(account.getBalance().subtract(amount));
        return accountRepository.save(account);
    }

    public Account credit(Long id, BigDecimal amount) {
        Account account = getAccount(id);
        account.setBalance(account.getBalance().add(amount));
        return accountRepository.save(account);
    }

    private String generateAccountNumber() {
        return "ACC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}