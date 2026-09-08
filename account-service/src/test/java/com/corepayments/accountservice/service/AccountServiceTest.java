package com.corepayments.accountservice.service;

import com.corepayments.accountservice.exception.AccountNotFoundException;
import com.corepayments.accountservice.exception.InsufficientFundsException;
import com.corepayments.accountservice.model.Account;
import com.corepayments.accountservice.model.AccountType;
import com.corepayments.accountservice.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * These are the tests that must keep passing when an agent later modifies
 * this class (e.g. adding a transfer limit) — a broken test here is the
 * objective "brownfield" failure signal for the dissertation's evaluation.
 */
class AccountServiceTest {

    private AccountRepository accountRepository;
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountRepository = Mockito.mock(AccountRepository.class);
        accountService = new AccountService(accountRepository);
    }

    @Test
    void createAccount_savesAndReturnsNewAccount() {
        Mockito.when(accountRepository.save(Mockito.any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Account result = accountService.createAccount("Test User", AccountType.SAVINGS, BigDecimal.valueOf(1000));

        assertEquals("Test User", result.getCustomerName());
        assertEquals(AccountType.SAVINGS, result.getAccountType());
        assertEquals(BigDecimal.valueOf(1000), result.getBalance());
        assertNotNull(result.getAccountNumber());
    }

    @Test
    void getAccount_throwsWhenNotFound() {
        Mockito.when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.getAccount(99L));
    }

    @Test
    void debit_succeedsWhenSufficientBalance() {
        Account account = new Account("ACC-TEST01", "Test User", AccountType.CURRENT, BigDecimal.valueOf(500));
        Mockito.when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        Mockito.when(accountRepository.save(Mockito.any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Account result = accountService.debit(1L, BigDecimal.valueOf(200));

        assertEquals(BigDecimal.valueOf(300), result.getBalance());
    }

    @Test
    void debit_throwsWhenInsufficientBalance() {
        Account account = new Account("ACC-TEST02", "Test User", AccountType.CURRENT, BigDecimal.valueOf(100));
        Mockito.when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        assertThrows(InsufficientFundsException.class, () -> accountService.debit(1L, BigDecimal.valueOf(200)));
    }

    @Test
    void credit_increasesBalance() {
        Account account = new Account("ACC-TEST03", "Test User", AccountType.SAVINGS, BigDecimal.valueOf(100));
        Mockito.when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        Mockito.when(accountRepository.save(Mockito.any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Account result = accountService.credit(1L, BigDecimal.valueOf(50));

        assertEquals(BigDecimal.valueOf(150), result.getBalance());
    }
}