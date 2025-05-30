package by.aleksabrakor.bank_accounts.exception;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO для возврата информации об ошибке
 *
 *  @author  Brakor Aleksandra
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Error Response")
public class ErrorResponse {

    @Schema(description = "message", example = "Validation failed: description: не должно быть пустым; title: не должно быть пустым")
    private String message;

    @Schema(description = "localDateTime", example = "2025-03-29T12:44:38.510+00:00")
    private LocalDateTime localDateTime;

}