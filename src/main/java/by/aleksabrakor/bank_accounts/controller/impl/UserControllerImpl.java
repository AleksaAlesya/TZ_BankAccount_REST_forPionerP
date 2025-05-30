package by.aleksabrakor.bank_accounts.controller.impl;


import by.aleksabrakor.bank_accounts.controller.UserController;
import by.aleksabrakor.bank_accounts.dto.response.UserResponse;
import by.aleksabrakor.bank_accounts.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @GetMapping("/filter")
    public Page<UserResponse> findFilteredUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateOfBirth,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email,
            @PageableDefault(sort = "name") Pageable pageable) {

        log.info("GET /api/users/filter — Получение списка всех пользователя c фильтрацией по полям и пагинацией с сортировкой по имени (искать может любой любого)");
        return userService.searchUsers(name, dateOfBirth, phone, email, pageable);
    }
}
