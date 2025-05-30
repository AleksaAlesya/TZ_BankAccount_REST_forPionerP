package by.aleksabrakor.bank_accounts.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Transfer Dto")
public class TransferDto {

    @Schema(description = "toUserId - id получателя", example = "2")
    @NotNull(message = "Введите ID получателя")
    @Positive(message = "Некорректный ID получателя")
    private Long toUserId;

    @Schema(description = "amount", example = "10.00")
    @NotNull(message = "Введите сумму перевода")
    @DecimalMin(value = "0.01", message = "Минимальная сумма перевода: 0.01")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal amount;

    @Schema(description = "description", example = "Перевод прошел успешно!")
    @Size(max = 255, message = "Описание перевода, не более 255 символов")
    private String description; // Опциональное поле с описанием перевода
}
