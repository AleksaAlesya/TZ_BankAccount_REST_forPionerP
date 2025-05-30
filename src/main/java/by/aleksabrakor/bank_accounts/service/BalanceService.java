package by.aleksabrakor.bank_accounts.service;


import by.aleksabrakor.bank_accounts.entity.Account;

import java.util.List;


public interface BalanceService {

    void applyPercent();

    void processAccountsBatch(List<Account> accounts);

}
