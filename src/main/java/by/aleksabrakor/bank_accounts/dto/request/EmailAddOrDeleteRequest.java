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
@Schema(description = "Email Add Or Delete Request")
public class EmailAddOrDeleteRequest {

    @Schema(description = "email", example = "test@email.ru")
    @NotBlank(message = "Введите email для добавления или удаления")
    @Size(max = 200)
    @Email(message = "Некорректный формат  email")
    private String email;

}
