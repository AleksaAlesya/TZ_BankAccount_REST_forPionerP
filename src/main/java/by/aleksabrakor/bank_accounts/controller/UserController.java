package by.aleksabrakor.bank_accounts.controller;


import by.aleksabrakor.bank_accounts.dto.response.UserResponse;
import by.aleksabrakor.bank_accounts.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@RequestMapping("/api/users")
@Tag(name = "User Controller", description = "API c операциями по управлению пользователями ")
public interface UserController {

    @Operation(summary = "Получение списка всех пользователя c фильтрацией по полям и пагинацией (искать может любой любого)")
    @ApiResponse(responseCode = "200", content =
    @Content(schema = @Schema(implementation = UserResponse.class),
            mediaType = "application/json"))
    @ApiResponse(responseCode = "400", content =
    @Content(schema = @Schema(implementation = ErrorResponse.class),
            mediaType = "application/json"))
    @ApiResponse(responseCode = "403", content =
    @Content(schema = @Schema(implementation = ErrorResponse.class),
            mediaType = "application/json"))
    @ApiResponse(responseCode = "404", content =
    @Content(schema = @Schema(implementation = ErrorResponse.class),
            mediaType = "application/json"))
    @ApiResponse(responseCode = "500", content =
    @Content(schema = @Schema(implementation = ErrorResponse.class),
            mediaType = "application/json"))
    @GetMapping("/filter")
    Page<UserResponse> findFilteredUsers(@RequestParam(required = false) String name,
                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateOfBirth,
                                         @RequestParam(required = false) String phone,
                                         @RequestParam(required = false) String email,
                                         @PageableDefault(sort = "name") Pageable pageable);

}
