package by.aleksabrakor.bank_accounts.controller.impl;

import by.aleksabrakor.bank_accounts.dto.request.AuthRequest;
import by.aleksabrakor.bank_accounts.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthControllerImpl  {
    private final AuthService authService;

    @PostMapping()
    public ResponseEntity<String> login(@RequestBody @Valid AuthRequest request) {
        log.info("POST: /api/auth - Аутентификация пользователя");
        return ResponseEntity.ok(authService.authenticate(request));
    }
}
