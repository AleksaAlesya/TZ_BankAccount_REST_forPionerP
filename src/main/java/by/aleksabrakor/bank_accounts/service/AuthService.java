package by.aleksabrakor.bank_accounts.service;


import by.aleksabrakor.bank_accounts.dto.request.AuthRequest;


public interface AuthService {

    String authenticate(AuthRequest request);

    void checkSelfAccess(Long targetUserId);
}
