package by.aleksabrakor.bank_accounts.controller;


import by.aleksabrakor.bank_accounts.dto.request.EmailAddOrDeleteRequest;
import by.aleksabrakor.bank_accounts.dto.request.EmailUpdateRequest;
import by.aleksabrakor.bank_accounts.dto.response.UserResponse;
import by.aleksabrakor.bank_accounts.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/api/users/{userId}/emails")
@Tag(name = "EmailData Controller", description = "API c  операциями по добавлению, обновлению, удалению email. Пользователь может менять только собственные данные ")
public interface EmailDataController {


    @Operation(summary = "Добавление email")
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
    UserResponse addEmail(@PathVariable Long userId,
                                 @RequestBody @Valid EmailAddOrDeleteRequest request);

    @Operation(summary = "Изменение email")
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
    @PutMapping
    UserResponse updateEmails(@PathVariable Long userId,
                                     @RequestBody @Valid EmailUpdateRequest request
    );


    @Operation(summary = "Удаление email (если больше одного")
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
    @DeleteMapping
    UserResponse deleteEmail(@PathVariable Long userId,
                                    @RequestBody @Valid EmailAddOrDeleteRequest request
    );
}
