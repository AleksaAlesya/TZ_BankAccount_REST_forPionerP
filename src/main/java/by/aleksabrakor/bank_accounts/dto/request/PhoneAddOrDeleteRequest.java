package by.aleksabrakor.bank_accounts.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Phone Add Or Delete Request")
public class PhoneAddOrDeleteRequest {

    @Schema(description = "number", example = "72134567891")
    @NotBlank(message = "Введите номер телефона для добавления или удаления")
    @Size(max = 13)
    @Pattern(regexp = "^7\\d{10}$", message = "Телефон должен быть в формате 79201234567 (начинаться с 7 всего 11 цифр)")
    private String number;
}
