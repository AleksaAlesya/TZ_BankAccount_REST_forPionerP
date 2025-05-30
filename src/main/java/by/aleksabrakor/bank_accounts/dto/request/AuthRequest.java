package by.aleksabrakor.bank_accounts.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Auth Request")
public class AuthRequest {

    @Schema(description = "login", example = "test@email.ru", examples = "71243654785")
    @NotBlank(message = "Введите логи - email или номер телефона ( в формате 79201234567 (начинаться с 7 всего 11 цифр)")
    @Size(max = 200)
    private String login;

    @Schema(description = "password", example = "12345678")
    @NotBlank(message = "Введите пароль")
    @Size(min=8, max = 13)
    private String password;

}
