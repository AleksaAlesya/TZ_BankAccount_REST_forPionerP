package by.aleksabrakor.bank_accounts.service;


import by.aleksabrakor.bank_accounts.dto.request.EmailAddOrDeleteRequest;
import by.aleksabrakor.bank_accounts.dto.request.EmailUpdateRequest;
import by.aleksabrakor.bank_accounts.dto.response.UserResponse;

public interface EmailDataService {

    UserResponse addEmail(Long userId, EmailAddOrDeleteRequest request);

    UserResponse updateEmail(Long userId, EmailUpdateRequest request);

    UserResponse deleteEmail(Long userId, EmailAddOrDeleteRequest request);
}
