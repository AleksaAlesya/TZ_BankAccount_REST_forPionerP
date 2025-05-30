package by.aleksabrakor.bank_accounts.controller;


import by.aleksabrakor.bank_accounts.dto.TransferDto;
import by.aleksabrakor.bank_accounts.exception.ErrorResponse;
import by.aleksabrakor.bank_accounts.security.JwtUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("/api/transfer")

@Tag(name = "Account Controller", description = "API c операциями по переводам")
public interface AccountController {


    @Operation(summary = "Трансфер денег от одного пользователя к другому")
    @ApiResponse(responseCode = "200", content =
    @Content(schema = @Schema(implementation = TransferDto.class),
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
    @PostMapping()
    TransferDto transferMoney(@AuthenticationPrincipal JwtUserDetails userPrincipal,
                                      @RequestBody @Valid TransferDto request
    );
}
