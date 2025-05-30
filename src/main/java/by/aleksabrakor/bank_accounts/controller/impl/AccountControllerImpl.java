package by.aleksabrakor.bank_accounts.controller.impl;


import by.aleksabrakor.bank_accounts.controller.AccountController;
import by.aleksabrakor.bank_accounts.dto.TransferDto;
import by.aleksabrakor.bank_accounts.security.JwtUserDetails;
import by.aleksabrakor.bank_accounts.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/transfer")
@RequiredArgsConstructor
@Slf4j
public class AccountControllerImpl implements AccountController {

    private final AccountService transferService;

    @PostMapping()
    public TransferDto transferMoney(@AuthenticationPrincipal JwtUserDetails userPrincipal,
                                             @RequestBody @Valid TransferDto request
    ) {
        log.info("GET /api/transfer — Трансфер денег от одного пользователя к другому");
        return  transferService.transferMoney(
                userPrincipal.getId(), // ID из токена
                request.getToUserId(),
                request.getAmount()
        );
    }
}
