package by.aleksabrakor.bank_accounts.controller;

import by.aleksabrakor.bank_accounts.dto.request.PhoneAddOrDeleteRequest;
import by.aleksabrakor.bank_accounts.dto.request.PhoneUpdateRequest;
import by.aleksabrakor.bank_accounts.dto.response.UserResponse;
import by.aleksabrakor.bank_accounts.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/api/users/{userId}/phones")
@Tag(name = "PhoneData Controller", description = "API c  операциями по добавлению, обновлению, удалению телефона. Пользователь может менять только собственные данные ")
public interface PhoneDataController {

    @Operation(summary = "Добавление телефона")
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
    @PostMapping
    UserResponse addPhone(@PathVariable Long userId,
                          @RequestBody @Valid PhoneAddOrDeleteRequest request);


    @Operation(summary = "Изменение телефона")
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
    @PutMapping()
    UserResponse updateEmails(@PathVariable Long userId,
                              @RequestBody @Valid PhoneUpdateRequest request
    );


    @Operation(summary = "Удаление телефона (если больше одного")
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
    @DeleteMapping()
    UserResponse deleteEmail(@PathVariable Long userId,
                             @RequestBody @Valid PhoneAddOrDeleteRequest request
    );
}
