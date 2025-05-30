package by.aleksabrakor.bank_accounts.service.impl;

import by.aleksabrakor.bank_accounts.dto.TransferDto;
import by.aleksabrakor.bank_accounts.entity.Account;
import by.aleksabrakor.bank_accounts.entity.Transaction;
import by.aleksabrakor.bank_accounts.entity.User;
import by.aleksabrakor.bank_accounts.exception.NotFoundException;
import by.aleksabrakor.bank_accounts.repository.AccountRepository;
import by.aleksabrakor.bank_accounts.repository.TransactionRepository;
import by.aleksabrakor.bank_accounts.util.AccountPair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class AccountServiceImplTest {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private AccountServiceImpl accountService;


    private final Long fromUserId = 1L;
    private final Long toUserId = 2L;
    private final BigDecimal amount = new BigDecimal("100.00");

    private Account createAccount(Long accountId,Long userId, BigDecimal balance) {
        return Account.builder()
                .id(accountId)
                .user(User.builder().id(userId).build())
                .balance(balance)
                .initialBalance(balance)
                .build();
    }

    @DisplayName("Успешный перевод денег")
    @Test
    void transferMoney_ValidRequest_Success() {
        // Arrange
        Account fromAccount = createAccount(11L, fromUserId, new BigDecimal("500.00"));
        Account toAccount = createAccount(22L, toUserId, new BigDecimal("200.00"));

        when(accountRepository.findByIdWithLock(fromUserId))
                .thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByIdWithLock(toUserId))
                .thenReturn(Optional.of(toAccount));

        // Act
        TransferDto result = accountService.transferMoney(fromUserId, toUserId, amount);

        // Assert
        assertEquals(new BigDecimal("400.00"), fromAccount.getBalance());
        assertEquals(new BigDecimal("300.00"), toAccount.getBalance());
        assertEquals("Перевод прошел успешно!", result.getDescription());

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());

        Transaction transaction = transactionCaptor.getValue();
        assertEquals(fromUserId, transaction.getFromUserId());
        assertEquals(toUserId, transaction.getToUserId());
        assertEquals(amount, transaction.getAmount());
        assertNotNull(transaction.getTimestamp());

    }


    @DisplayName("Перевод с отрицательной суммой")
    @Test
    void transferMoney_NegativeAmount_ThrowsException() {
        // Arrange
        BigDecimal negativeAmount = new BigDecimal("-50.00");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.transferMoney(fromUserId, toUserId, negativeAmount)
        );

        assertEquals("Сумма перевода должна быть положительной", exception.getMessage());
        verifyNoInteractions(accountRepository, transactionRepository);
    }


    @DisplayName("Перевод самому себе")
    @Test
    void transferMoney_SameUser_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.transferMoney(fromUserId, fromUserId, amount)
        );

        assertEquals("Перевод самому себе запрещен", exception.getMessage());
        verifyNoInteractions(accountRepository, transactionRepository);
    }


    @DisplayName("Недостаточно средств")
    @Test
    void transferMoney_InsufficientFunds_ThrowsException() {
        // Arrange
        Account fromAccount = createAccount(11L, fromUserId, new BigDecimal("500.00"));
        Account toAccount = createAccount(22L, toUserId, new BigDecimal("200.00"));
        BigDecimal largeAmount = new BigDecimal("1000.00");

        when(accountRepository.findByIdWithLock(fromUserId))
                .thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByIdWithLock(toUserId))
                .thenReturn(Optional.of(toAccount));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.transferMoney(fromUserId, toUserId, largeAmount)
        );

        assertEquals("Недостаточно средств", exception.getMessage());
        verify(transactionRepository, never()).save(any());
    }


    @DisplayName("Счет отправителя не найден")
    @Test
    void transferMoney_FromAccountNotFound_ThrowsException() {
        // Arrange
        when(accountRepository.findByIdWithLock(fromUserId))
                .thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> accountService.transferMoney(fromUserId, toUserId, amount)
        );

        assertEquals("Счет не найден: " + fromUserId, exception.getMessage());
        verify(accountRepository, never()).findByIdWithLock(toUserId);
        verifyNoInteractions(transactionRepository);
    }


    @DisplayName("Счет получателя не найден")
    @Test
    void transferMoney_ToAccountNotFound_ThrowsException() {
        // Arrange
        Account fromAccount = createAccount(11L, fromUserId, new BigDecimal("500.00"));

        when(accountRepository.findByIdWithLock(fromUserId))
                .thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByIdWithLock(toUserId))
                .thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> accountService.transferMoney(fromUserId, toUserId, amount)
        );

        assertEquals("Счет не найден: " + toUserId, exception.getMessage());
        verify(transactionRepository, never()).save(any());
    }


    @DisplayName("Проверка порядка блокировки (fromUserId < toUserId)")
    @Test
    void lockAccountsInOrder_FromIdLessThanToId_ReturnsCorrectOrder() {
        // Arrange
        Account fromAccount = createAccount(11L, fromUserId, new BigDecimal("500.00"));
        Account toAccount = createAccount(22L, toUserId, new BigDecimal("200.00"));
        Long minId = Math.min(fromUserId, toUserId);
        Long maxId = Math.max(fromUserId, toUserId);

        when(accountRepository.findByIdWithLock(minId))
                .thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByIdWithLock(maxId))
                .thenReturn(Optional.of(toAccount));

        // Act
        AccountPair pair = accountService.lockAccountsInOrder(fromUserId, toUserId);

        // Assert
        assertSame(fromAccount, pair.fromAccount());
        assertSame(toAccount, pair.toAccount());
        verify(accountRepository).findByIdWithLock(minId);
        verify(accountRepository).findByIdWithLock(maxId);
    }


    @DisplayName("Проверка обновления балансов")
    @Test
    void updateAccountBalances_ValidAmount_UpdatesCorrectly() {
        // Arrange
        Account fromAccount = createAccount(11L, fromUserId, new BigDecimal("500.00"));
        Account toAccount = createAccount(22L, toUserId, new BigDecimal("200.00"));
        BigDecimal initialFromBalance = fromAccount.getBalance();
        BigDecimal initialToBalance = toAccount.getBalance();

        // Act
        accountService.updateAccountBalances(fromAccount, toAccount, amount);

        // Assert
        assertEquals(initialFromBalance.subtract(amount), fromAccount.getBalance());
        assertEquals(initialToBalance.add(amount), toAccount.getBalance());
    }


    @DisplayName("Проверка создания транзакции")
    @Test
    void createTransactionRecord_ValidData_CreatesTransaction() {
        // Arrange
        Transaction savedTransaction = Transaction.builder().id(1L).build();
        when(transactionRepository.save(any())).thenReturn(savedTransaction);

        // Act
        accountService.createTransactionRecord(fromUserId, toUserId, amount);

        // Assert
        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(captor.capture());

        Transaction transaction = captor.getValue();
        assertEquals(fromUserId, transaction.getFromUserId());
        assertEquals(toUserId, transaction.getToUserId());
        assertEquals(amount, transaction.getAmount());
        assertNotNull(transaction.getTimestamp());
        assertTrue(transaction.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(transaction.getTimestamp().isAfter(LocalDateTime.now().minusSeconds(1)));
    }
}