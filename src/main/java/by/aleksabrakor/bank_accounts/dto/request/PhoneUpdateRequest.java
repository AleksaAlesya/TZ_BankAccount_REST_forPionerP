package by.aleksabrakor.bank_accounts.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Phone Update Request")
public class PhoneUpdateRequest {

    @Schema(description = "oldNumber", example = "72134567891")
    @NotBlank(message = "Введите старый номер телефона для изменения")
    @Size(max = 13)
    @Pattern(regexp = "^7\\d{10}$", message = "Номер телефона должен быть в формате 79201234567 (начинаться с 7 всего 11 цифр)")
    private String oldNumber;

    @Schema(description = "newNumber", example = "72134567891")
    @NotBlank(message = "Введите новый номер телефона на который необходимо заменить")
    @Size(max = 13)
    @Pattern(regexp = "^7\\d{10}$", message = "Номер телефона должен быть в формате 79201234567 (начинаться с 7 всего 11 цифр)")
    private String newNumber;
}
