package by.aleksabrakor.bank_accounts.service;


import by.aleksabrakor.bank_accounts.dto.request.PhoneAddOrDeleteRequest;
import by.aleksabrakor.bank_accounts.dto.request.PhoneUpdateRequest;
import by.aleksabrakor.bank_accounts.dto.response.UserResponse;

public interface PhoneDataService {

    UserResponse addPhone(Long userId, PhoneAddOrDeleteRequest request);

    UserResponse updatePhone(Long userId, PhoneUpdateRequest request);

    UserResponse deletePhone(Long userId, PhoneAddOrDeleteRequest request);
}
