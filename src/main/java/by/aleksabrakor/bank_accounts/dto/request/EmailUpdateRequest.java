package by.aleksabrakor.bank_accounts.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
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
@Schema(description = "Email Update Request")
public class EmailUpdateRequest {

    @Schema(description = "oldEmail", example = "test@email.ru")
    @NotBlank(message = "Введите старый email, который хотите  изменить")
    @Size(max = 200)
    @Email(message = "Некорректный формат старого email")
    private String oldEmail;

    @Schema(description = "newEmail", example = "test1@email.ru")
    @NotBlank(message = "Введите новый email, на который хотите  изменить")
    @Size(max = 200)
    @Email(message = "Некорректный формат нового email")
    private String newEmail;
}
