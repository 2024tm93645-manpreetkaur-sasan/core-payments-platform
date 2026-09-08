package com.corepayments.accountservice.config;

import com.corepayments.accountservice.model.Account;
import com.corepayments.accountservice.model.AccountType;
import com.corepayments.accountservice.repository.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Seeds the in-memory database with synthetic (not real) customer/account
 * data on startup. No external dataset or library dependency — deliberately
 * simple and self-contained, consistent with the dissertation's
 * "public or synthetic datasets only" scope constraint.
 */
@Configuration
public class SyntheticDataSeeder {

    private static final List<String> FIRST_NAMES = List.of(
            "Alex", "Priya", "James", "Fatima", "Liam", "Sofia", "Omar", "Chen",
            "Grace", "Noah", "Ana", "Kwame"
    );
    private static final List<String> LAST_NAMES = List.of(
            "Sharma", "Patel", "Smith", "Khan", "Garcia", "Nguyen", "Kim",
            "Okafor", "Rossi", "Muller", "Ali", "Brown"
    );

    @Bean
    @Profile("!test")
    public CommandLineRunner seedAccounts(AccountRepository accountRepository) {
        return args -> {
            if (accountRepository.count() > 0) {
                return;
            }
            Random random = new Random(42); // fixed seed — reproducible synthetic data
            for (int i = 0; i < 10; i++) {
                String name = FIRST_NAMES.get(random.nextInt(FIRST_NAMES.size())) + " "
                        + LAST_NAMES.get(random.nextInt(LAST_NAMES.size()));
                AccountType type = random.nextBoolean() ? AccountType.SAVINGS : AccountType.CURRENT;
                BigDecimal openingBalance = BigDecimal.valueOf(500 + random.nextInt(9500));
                String accountNumber = "ACC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

                accountRepository.save(new Account(accountNumber, name, type, openingBalance));
            }
        };
    }
}