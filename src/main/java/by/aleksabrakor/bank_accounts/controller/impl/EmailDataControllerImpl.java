package by.aleksabrakor.bank_accounts.controller.impl;


import by.aleksabrakor.bank_accounts.controller.EmailDataController;
import by.aleksabrakor.bank_accounts.dto.request.*;
import by.aleksabrakor.bank_accounts.dto.response.UserResponse;
import by.aleksabrakor.bank_accounts.service.AuthService;
import by.aleksabrakor.bank_accounts.service.EmailDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users/{userId}/emails")
@RequiredArgsConstructor
@Slf4j
public class EmailDataControllerImpl implements EmailDataController {

    private final EmailDataService emailService;
    private final AuthService authService;

    @PostMapping
    public UserResponse addEmail(@PathVariable Long userId,
                                 @RequestBody @Valid EmailAddOrDeleteRequest request
    ) {
        log.info("POST: /api/users/{userId}/emails — Добавление email");
        authService.checkSelfAccess(userId);
        return emailService.addEmail(userId, request);
    }

    @PutMapping()
//    @PreAuthorize("#userId == principal.id")
    public UserResponse updateEmails(@PathVariable Long userId,
                                     @RequestBody @Valid EmailUpdateRequest request
    ) {
        log.info("PUT: /api/users/{userId}/emails — Изменение email");
        authService.checkSelfAccess(userId);
        return emailService.updateEmail(userId, request);
    }


    @DeleteMapping()
//    @PreAuthorize("#userId == principal.id")
    public UserResponse deleteEmail(@PathVariable Long userId,
                                    @RequestBody @Valid EmailAddOrDeleteRequest request
    ) {
        log.info("PUT: /api/users/{userId}/emails — Удаление email");
        authService.checkSelfAccess(userId);
        return emailService.deleteEmail(userId, request);
    }
}
