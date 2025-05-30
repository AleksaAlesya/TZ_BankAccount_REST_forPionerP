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
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional
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
    @Test
    @Transactional
    void addPhone_Success() throws Exception {
        // Получаем id пользователя
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
        ;
    }

    @Test
    void updatePhone_ValidRequest_ShouldUpdatePhone() throws Exception {
        // Подготовка запроса
        Long userId = userRepository.findByEmailOrPhone("71234567894").orElseThrow().getId();
        PhoneUpdateRequest request = new PhoneUpdateRequest(
                "71234567894",  // старый телефон
                "79999999999"   // новый телефон
        );

        mockMvc.perform(put("/api/users/{userId}/phones", userId)
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))

                // Проверка результатов
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.phones.length()").value(1))
                ;
    }

    @Test
    void deletePhone_ValidRequest_ShouldDeletePhone() throws Exception {
        // Добавляем второй телефон для удаления
        User user = userRepository.findByEmailOrPhone("71234567894").orElseThrow();
        Long userId=user.getId();
        PhoneData phoneToDelete = PhoneData.builder()
                .phone("79205555555")
                .user(user)
                .build();
        phoneDataRepository.save(phoneToDelete);

        // Подготовка запроса
        PhoneAddOrDeleteRequest request = new PhoneAddOrDeleteRequest("79205555555");

        mockMvc.perform(delete("/api/users/{userId}/phones", userId)
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))

                // Проверка результатов
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.phones.length()").value(1))
        ;
    }
}