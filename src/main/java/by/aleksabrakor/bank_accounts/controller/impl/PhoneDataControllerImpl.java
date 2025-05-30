package by.aleksabrakor.bank_accounts.controller.impl;

import by.aleksabrakor.bank_accounts.controller.PhoneDataController;
import by.aleksabrakor.bank_accounts.dto.request.PhoneAddOrDeleteRequest;
import by.aleksabrakor.bank_accounts.dto.request.PhoneUpdateRequest;
import by.aleksabrakor.bank_accounts.dto.response.UserResponse;
import by.aleksabrakor.bank_accounts.service.AuthService;
import by.aleksabrakor.bank_accounts.service.PhoneDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users/{userId}/phones")
@RequiredArgsConstructor
@Slf4j
public class PhoneDataControllerImpl implements PhoneDataController {

    private final PhoneDataService phoneDataService;
    private final AuthService authService;

    @PostMapping
    public UserResponse addPhone(@PathVariable Long userId,
                                 @RequestBody @Valid PhoneAddOrDeleteRequest request
    ) {
        log.info("PUT: /api/users/{userId}/phones — Добавление телефона");
        authService.checkSelfAccess(userId);
        return phoneDataService.addPhone(userId, request);
    }

    @PutMapping()
//    @PreAuthorize("#userId == principal.id")
    public UserResponse updateEmails(@PathVariable Long userId,
                                     @RequestBody @Valid PhoneUpdateRequest request
    ) {
        log.info("PUT: /api/users/{userId}/phones — Изменение телефона");
        authService.checkSelfAccess(userId);
        return phoneDataService.updatePhone(userId, request);
    }


    @DeleteMapping()
//    @PreAuthorize("#userId == principal.id")
    public UserResponse deleteEmail(@PathVariable Long userId,
                                    @RequestBody @Valid PhoneAddOrDeleteRequest request
    ) {
        log.info("PUT: /api/users/{userId}/phones — Удаление телефона");
        authService.checkSelfAccess(userId);
        return phoneDataService.deletePhone(userId, request);
    }
}
