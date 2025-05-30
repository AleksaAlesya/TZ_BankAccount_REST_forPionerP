package by.aleksabrakor.bank_accounts.service;

import by.aleksabrakor.bank_accounts.dto.TransferDto;

import java.math.BigDecimal;

public interface AccountService {

    TransferDto transferMoney(Long fromUserId, Long toUserId, BigDecimal amount);

}
