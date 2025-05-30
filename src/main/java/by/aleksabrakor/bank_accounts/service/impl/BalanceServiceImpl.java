package by.aleksabrakor.bank_accounts.service.impl;

import by.aleksabrakor.bank_accounts.entity.Account;
import by.aleksabrakor.bank_accounts.repository.AccountRepository;
import by.aleksabrakor.bank_accounts.service.BalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class BalanceServiceImpl implements BalanceService {

    private final AccountRepository accountRepository;
    private static final int BATCH_SIZE = 500;
    private static final BigDecimal PERCENT_RATE = new BigDecimal("1.1");
    private static final BigDecimal MAX_MULTIPLIER = new BigDecimal("2.07");
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;
    private static final int SCALE = 2;

    @Scheduled(fixedRate = 30_000)
    public void applyPercent() {
        try {
            //Пакетная обработка счетов:
            log.info("Start apply percent");
            int page = 0;
            Page<Account> accountPage;

            do {
                accountPage = accountRepository.findAll(PageRequest.of(page, BATCH_SIZE));
                processAccountsBatch(accountPage.getContent());
                page++;
            } while (accountPage.hasNext());
            log.info("Completed apply percent. Total processed: {}", page);

        } catch (Exception e) {
            log.error("Error apply percent", e);
            throw new RuntimeException(e);
        }
    }

    //Отдельные транзакции для каждого пакета:
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processAccountsBatch(List<Account> accounts) {
        log.debug("Processing batch of {} accounts", accounts.size());

        List<Account> updatedAccounts = accounts.stream()
                .map(this::calculateNewBalance)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        accountRepository.saveAll(updatedAccounts);
    }

    private Account calculateNewBalance(Account account) {
        BigDecimal maxAllowed = calculateMaxAllowed(account);
        BigDecimal newBalance = calculateIncreasedBalance(account);

        if (newBalance.compareTo(maxAllowed) > 0) {
            newBalance = maxAllowed;
        }

        if (newBalance.compareTo(account.getBalance()) > 0) {
            account.setBalance(newBalance);
            return account;
        }
        return null;
    }

    private BigDecimal calculateMaxAllowed(Account account) {
        return account.getInitialBalance()
                .multiply(MAX_MULTIPLIER)
                .setScale(SCALE, ROUNDING_MODE);
    }

    private BigDecimal calculateIncreasedBalance(Account account) {
        return account.getBalance()
                .multiply(PERCENT_RATE)
                .setScale(SCALE, ROUNDING_MODE);
    }

}
