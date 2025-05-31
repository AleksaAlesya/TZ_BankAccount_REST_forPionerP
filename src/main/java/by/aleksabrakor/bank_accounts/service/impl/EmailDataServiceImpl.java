package by.aleksabrakor.bank_accounts.service.impl;

import by.aleksabrakor.bank_accounts.dto.request.EmailAddOrDeleteRequest;
import by.aleksabrakor.bank_accounts.dto.request.EmailUpdateRequest;
import by.aleksabrakor.bank_accounts.dto.response.UserResponse;
import by.aleksabrakor.bank_accounts.entity.EmailData;
import by.aleksabrakor.bank_accounts.entity.User;
import by.aleksabrakor.bank_accounts.exception.AlreadyExistsException;
import by.aleksabrakor.bank_accounts.exception.NotFoundException;
import by.aleksabrakor.bank_accounts.repository.EmailDataRepository;
import by.aleksabrakor.bank_accounts.service.EmailDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class EmailDataServiceImpl implements EmailDataService {

    private final UserServiceImpl userService;
    private final EmailDataRepository emailDataRepository;

    @CacheEvict(value = "users", key = "#userId")
    @Transactional
    public UserResponse addEmail(Long userId, EmailAddOrDeleteRequest request) {
        log.info("Добавление email для пользователя [userId: {}] | Запрос: {}", userId, request.getEmail());

        String email = request.getEmail();
        User user = userService.findUserOrThrow(userId);

        validateEmailUniqueness(email);
        log.debug("Email {} прошел проверку уникальности", email);

        EmailData newEmail = createEmailEntity(user, email);
        emailDataRepository.save(newEmail);
        log.info("Email {} добавлен для пользователя [userId: {}] | Новый ID newEmail: {}",
                email, userId, newEmail.getId());

        return userService.getUserById(userId);
    }


    @CacheEvict(value = "users", key = "#userId")
    @Transactional
    public UserResponse updateEmail(Long userId, EmailUpdateRequest request) {
        String oldEmail = request.getOldEmail();
        String newEmail = request.getNewEmail();
        log.info("Обновление email для пользователя [ID: {}] | oldEmail: {} → newEmail: {}",
                userId, oldEmail, newEmail);

        EmailData existingEmail = getEmailByUser(userId, oldEmail);

        validateEmailUniqueness(newEmail);
        log.debug("Новый newEmail {} прошел проверку уникальности", newEmail);

        existingEmail.setEmail(newEmail);
        emailDataRepository.save(existingEmail);
        log.info("Email обновлен [emailId: {}] | {} → {}",
                existingEmail.getId(), oldEmail, newEmail);

        return userService.getUserById(userId);
    }


    @CacheEvict(value = "users", key = "#userId")
    @Transactional
    public UserResponse deleteEmail(Long userId, EmailAddOrDeleteRequest request) {
        String email = request.getEmail();
        log.info("Удаление email {} для пользователя [userId: {}]", email, userId);
        User user = userService.findUserOrThrow(userId);

        validateNotLastEmail(user);
        log.debug("Проверка на последний email пройдена");

        EmailData emailToDelete = getEmailByUser(userId, email);

        user.getEmails().remove(emailToDelete);
        emailDataRepository.delete(emailToDelete);
        log.info("Email удален {} Пользователь [userId: {}] ",
                email, userId);

        return userService.getUserById(userId);
    }

    private void validateNotLastEmail(User user) {
        if (user.getEmails().size() <= 1) {
            log.warn("Попытка удаления последнего email | [userId: {}]", user.getId());
            throw new IllegalStateException("Невозможно удалить последний email");
        }
    }

    private EmailData createEmailEntity(User user, String email) {
        EmailData newEmail = new EmailData();
        newEmail.setEmail(email);
        newEmail.setUser(user);
        return newEmail;
    }

    private void validateEmailUniqueness(String email) {
        if (emailDataRepository.existsByEmail(email)) {
            log.warn("Email уже существует в системе | {}", email);
            throw new AlreadyExistsException("Этот email уже существует");
        }
    }

    private EmailData getEmailByUser(Long userId, String oldEmail) {
        return emailDataRepository.findByEmailAndUserId(oldEmail, userId)
                .orElseThrow(() -> {
                    log.warn("Email не найден |  [usrId: {}] | Email: {}", userId, oldEmail);
                    return new NotFoundException("Email не найден: " + oldEmail);
                });
    }
}
