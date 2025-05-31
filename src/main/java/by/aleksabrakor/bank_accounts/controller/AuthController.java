package by.aleksabrakor.bank_accounts.controller;

import by.aleksabrakor.bank_accounts.dto.request.AuthRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("/api/auth")
@Tag(name = "Auth Controller", description = "API для аутентификации")
public interface AuthController {


    @Operation(
            summary = "Аутентификация пользователя",
            description = "Не требует аутентификации"
    )
    @ApiResponse(responseCode = "200", content =
    @Content(schema = @Schema(implementation = String.class),
            mediaType = "application/json"))
    @ApiResponse(responseCode = "400", content =
    @Content(schema = @Schema(implementation = String.class),
            mediaType = "application/json"))
    @ApiResponse(responseCode = "404", content =
    @Content(schema = @Schema(implementation = String.class),
            mediaType = "application/json"))
    @ApiResponse(responseCode = "500", content =
    @Content(schema = @Schema(implementation = String.class),
            mediaType = "application/json"))
    @SecurityRequirements() //будет доступен в Swagger без требования токена
    @PostMapping()
    ResponseEntity<String> login(@RequestBody @Valid AuthRequest request);
}
