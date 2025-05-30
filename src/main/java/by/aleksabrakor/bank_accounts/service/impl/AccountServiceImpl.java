package by.aleksabrakor.bank_accounts.service.impl;


import by.aleksabrakor.bank_accounts.dto.TransferDto;
import by.aleksabrakor.bank_accounts.entity.Account;
import by.aleksabrakor.bank_accounts.entity.Transaction;
import by.aleksabrakor.bank_accounts.exception.NotFoundException;
import by.aleksabrakor.bank_accounts.repository.AccountRepository;
import by.aleksabrakor.bank_accounts.repository.TransactionRepository;
import by.aleksabrakor.bank_accounts.service.AccountService;
import by.aleksabrakor.bank_accounts.util.AccountPair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

//основной сервис для операций перевода
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;


    @Transactional
    public TransferDto transferMoney(Long fromUserId, Long toUserId, BigDecimal amount) {
        // Генерируем уникальный ID для операции
        String transferId = "TRF-" + UUID.randomUUID().toString().substring(0, 8);
        log.info("[{}] Начало перевода денег: fromUserId={}, toUserId={}, amount={}",
                transferId, fromUserId, toUserId, amount);

        validateTransferRequest(fromUserId, toUserId, amount);
        log.debug("[{}] Валидация пройдена успешно", transferId);

        AccountPair accountPair = lockAccountsInOrder(fromUserId, toUserId);
        Account fromAccount = accountPair.fromAccount();
        Account toAccount = accountPair.toAccount();
        log.info("[{}] Счета заблокированы: fromAccountId={}; toAccountId={}",
                transferId,
                fromAccount.getId(),
                toAccount.getId());

        checkAccountBalance(fromAccount, amount);
        log.debug("[{}] Подтверждено наличие достаточных средств", transferId);

        updateAccountBalances(fromAccount, toAccount, amount);
        log.info("[{}] Балансы обновлены: fromNewBalance={}, toNewBalance={}",
                transferId, fromAccount.getBalance(), toAccount.getBalance());


        createTransactionRecord(fromUserId, toUserId, amount);
        log.info("[{}] Транзакция создана", transferId);

        return TransferDto.builder()
                .toUserId(toUserId)
                .amount(amount)
                .description("Перевод прошел успешно!")
                .build();
    }

    // 1. Валидация входных параметров
    private void validateTransferRequest(Long fromUserId, Long toUserId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма перевода должна быть положительной");
        }
        if (fromUserId.equals(toUserId)) {
            throw new IllegalArgumentException("Перевод самому себе запрещен");
        }
    }

    // 2. Проверка достаточности средств
    private void checkAccountBalance(Account account, BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            log.warn("Недостаточно средств: accountId={}, balance={}, required={}",
                    account.getId(), account.getBalance(), amount);
            throw new IllegalArgumentException("Недостаточно средств");
        }
    }

    // 3. Блокировка счетов в детерминированном порядке
    AccountPair lockAccountsInOrder(Long fromUserId, Long toUserId) {
        Long minId = Math.min(fromUserId, toUserId);
        Long maxId = Math.max(fromUserId, toUserId);
        log.debug("[] Блокировка счетов в порядке очередности: minId={}, maxId={}", minId, maxId);

        //  Блокировки аккаунтов
        Account firstAccount = getAccountByUserIdWithLock(minId);
        Account secondAccount = getAccountByUserIdWithLock(maxId);
        log.debug("[] Счета заблокированы: first={}, second={}", firstAccount.getId(), secondAccount.getId());

        // Определяем соответствие
        if (fromUserId.equals(minId)) {
            return new AccountPair(firstAccount, secondAccount);
        } else {
            return new AccountPair(secondAccount, firstAccount);
        }
    }

    // 4. Получение счета с блокировкой
    private Account getAccountByUserIdWithLock(Long userId) {
        return accountRepository.findByIdWithLock(userId)
                .orElseThrow(() -> new NotFoundException("Счет не найден: " + userId));
    }

    // 5. Обновление балансов
    void updateAccountBalances(Account fromAccount, Account toAccount, BigDecimal amount) {
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));
    }

    // 6. Создание записи о транзакции
    void createTransactionRecord(Long fromUserId, Long toUserId, BigDecimal amount) {
        Transaction transaction = Transaction.builder()
                .fromUserId(fromUserId)
                .toUserId(toUserId)
                .amount(amount)
                .timestamp(LocalDateTime.now())
                .build();
        transactionRepository.save(transaction);
    }
}
