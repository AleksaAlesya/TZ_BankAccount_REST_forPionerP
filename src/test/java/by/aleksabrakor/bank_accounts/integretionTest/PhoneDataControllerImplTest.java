package by.aleksabrakor.bank_accounts.integretionTest;

import by.aleksabrakor.bank_accounts.dto.request.PhoneAddOrDeleteRequest;
import by.aleksabrakor.bank_accounts.dto.request.PhoneUpdateRequest;
import by.aleksabrakor.bank_accounts.entity.Account;
import by.aleksabrakor.bank_accounts.entity.EmailData;
import by.aleksabrakor.bank_accounts.entity.PhoneData;
import by.aleksabrakor.bank_accounts.entity.User;
import by.aleksabrakor.bank_accounts.repository.PhoneDataRepository;
import by.aleksabrakor.bank_accounts.repository.UserRepository;
import by.aleksabrakor.bank_accounts.security.JwtService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Slf4j
class PhoneDataControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PhoneDataRepository phoneDataRepository;


    @Autowired
    private JwtService jwtService;

    private String authToken;

    @BeforeEach
    void setup() {
        System.out.println("Очистка БД - настройка перед тестом");
        userRepository.deleteAll();
        phoneDataRepository.deleteAll();

        // Создание тестового пользователя
        User user = User.builder()
                .name("Test User")
                .dateOfBirth(LocalDate.now())
                .password(passwordEncoder.encode("password"))
                .build();

        Account account = Account.builder()
                .balance(new BigDecimal("100.00"))
                .initialBalance(new BigDecimal("100.00"))
                .user(user)
                .build();

        PhoneData phoneData = PhoneData.builder()
                .phone("71234567894")
                .user(user)
                .build();

        EmailData emailData = EmailData.builder()
                .email("fdusd@dfkds.ru")
                .user(user)
                .build();

        user.setPhones(List.of(phoneData));
        user.setEmails(List.of(emailData));
        user.setAccount(account);
        userRepository.save(user);

        // Генерация токена для тестового пользователя
        authToken = "Bearer " + jwtService.generateToken(user.getId());
    }

    @DisplayName("Успешное добавление нового телефона")
    @Test
    @SneakyThrows
    void addPhone_Success() {
        Long userId = userRepository.findByEmailOrPhone("71234567894").orElseThrow().getId();
        PhoneAddOrDeleteRequest request = new PhoneAddOrDeleteRequest("72345678912");

        mockMvc.perform(post("/api/users/{userId}/phones", userId)
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(result -> System.out.println("Response: " + result.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.phones").isArray())
                .andExpect(jsonPath("$.phones", containsInAnyOrder("72345678912", "71234567894")))
                .andExpect(jsonPath("$.phones.length()").value(2))
        ;
    }

    @DisplayName("Успешное редактирование существующего телефона")
    @Test
    @SneakyThrows
    void updatePhone_ValidRequest_ShouldUpdatePhone() {
        Long userId = userRepository.findByEmailOrPhone("71234567894").orElseThrow().getId();
        PhoneUpdateRequest request = new PhoneUpdateRequest(
                "71234567894",  // старый телефон
                "79999999999"   // новый телефон
        );

        mockMvc.perform(put("/api/users/{userId}/phones", userId)
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.phones.length()").value(1))
                .andExpect(jsonPath("$.phones", containsInAnyOrder("79999999999")))
        ;
    }

    @DisplayName("Успешное удаление существующего телефона, если он не единственный")
    @Test
    @SneakyThrows
    void deletePhone_ValidRequest_ShouldDeletePhone() {
        User user = userRepository.findByEmailOrPhone("71234567894").orElseThrow();
        Long userId = user.getId();

        // Добавляем юзеру второй телефон для удаления
        PhoneData phoneToDelete = PhoneData.builder()
                .phone("79205555555")
                .user(user)
                .build();
        phoneDataRepository.save(phoneToDelete);

        PhoneAddOrDeleteRequest request = new PhoneAddOrDeleteRequest("71234567894");

        mockMvc.perform(delete("/api/users/{userId}/phones", userId)
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.phones.length()").value(1))
                .andExpect(jsonPath("$.phones", containsInAnyOrder("79205555555")))
        ;
    }

    @DisplayName("Попытка удалить единственный телефон")
    @Test
    @SneakyThrows
    void deletePhone_IfLastPhone_ShouldReturnError() {
        User user = userRepository.findByEmailOrPhone("71234567894").orElseThrow();
        Long userId = user.getId();
        PhoneAddOrDeleteRequest request = new PhoneAddOrDeleteRequest("71234567894");

        mockMvc.perform(delete("/api/users/{userId}/phones", userId)
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cannot delete: Невозможно удалить последний номер телефона"));
    }

    @DisplayName("Попытка добавить не уникальный номер телефона")
    @Test
    @SneakyThrows
    void addPhone_IfDuplicatePhone_ShouldReturnError() {
        User user = userRepository.findByEmailOrPhone("71234567894").orElseThrow();
        Long userId = user.getId();
        PhoneAddOrDeleteRequest request = new PhoneAddOrDeleteRequest("71234567894");

        mockMvc.perform(post("/api/users/{userId}/phones", userId)
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Was not create: Этот номер телефона уже существует"));
    }

    @DisplayName("Попытка добавить телефон без авторизации")
    @Test
    @SneakyThrows
    void addPhone_unauthorizedAccess_ShouldReturnForbidden() {
        User user = userRepository.findByEmailOrPhone("71234567894").orElseThrow();
        Long userId = user.getId();
        PhoneAddOrDeleteRequest request = new PhoneAddOrDeleteRequest("79200000000");

        mockMvc.perform(post("/api/users/{userId}/phones", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @DisplayName("Попытка добавить невалидный телефона")
    @Test
    @SneakyThrows
    void addPhone_Success1() {
        Long userId = userRepository.findByEmailOrPhone("71234567894").orElseThrow().getId();
        PhoneAddOrDeleteRequest request = new PhoneAddOrDeleteRequest("723456");

        mockMvc.perform(post("/api/users/{userId}/phones", userId)
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed: number: Телефон должен быть в формате 79201234567 (начинаться с 7 всего 11 цифр)"));
    }
}