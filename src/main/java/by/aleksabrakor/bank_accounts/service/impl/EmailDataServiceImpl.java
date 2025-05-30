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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmailDataServiceImpl implements EmailDataService {

    private final UserServiceImpl userService;
    private final EmailDataRepository emailDataRepository;

    @CacheEvict(value = "users", key = "#userId")
    @Transactional
    public UserResponse addEmail(Long userId, EmailAddOrDeleteRequest request) {
        String email = request.getEmail();
        User user = userService.findUserOrThrow(userId);

        validateEmailUniqueness(email);
        EmailData newEmail = createEmailEntity(user, email);
        emailDataRepository.save(newEmail);

        return userService.getUserById(userId);
    }


    @CacheEvict(value = "users", key = "#userId")
    @Transactional
    public UserResponse updateEmail(Long userId, EmailUpdateRequest request) {
        String oldEmail = request.getOldEmail();
        String newEmail = request.getNewEmail();

        EmailData existingEmail = getEmailByUser(userId, oldEmail);
        validateEmailUniqueness(newEmail);

        existingEmail.setEmail(newEmail);
        emailDataRepository.save(existingEmail);
        return userService.getUserById(userId);
    }


    @CacheEvict(value = "users", key = "#userId")
    @Transactional
    public UserResponse deleteEmail(Long userId, EmailAddOrDeleteRequest request) {
        String email = request.getEmail();
        User user = userService.findUserOrThrow(userId);

        validateNotLastEmail(user);
        EmailData emailToDelete = getEmailByUser(userId, email);

        user.getEmails().remove(emailToDelete);
        emailDataRepository.delete(emailToDelete);
        return userService.getUserById(userId);
    }

    private void validateNotLastEmail(User user) {
        if (user.getEmails().size() <= 1) {
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
            throw new AlreadyExistsException("Этот email уже существует");
        }
    }
    private EmailData getEmailByUser(Long userId, String oldEmail) {
        return emailDataRepository.findByEmailAndUserId(oldEmail, userId)
                .orElseThrow(() -> new NotFoundException("Email не найден: " + oldEmail));
    }
}
