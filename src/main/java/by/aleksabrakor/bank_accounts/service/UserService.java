package by.aleksabrakor.bank_accounts.service;


import by.aleksabrakor.bank_accounts.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface UserService {

    UserResponse getUserById(Long id);

    Page<UserResponse> searchUsers(String name,
                                          LocalDate dateOfBirth,
                                          String phone,
                                          String email,
                                          Pageable pageable
    );
}
