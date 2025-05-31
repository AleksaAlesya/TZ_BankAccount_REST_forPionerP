package by.aleksabrakor.bank_accounts.service.impl;


import by.aleksabrakor.bank_accounts.dto.request.PhoneAddOrDeleteRequest;
import by.aleksabrakor.bank_accounts.dto.request.PhoneUpdateRequest;
import by.aleksabrakor.bank_accounts.dto.response.UserResponse;
import by.aleksabrakor.bank_accounts.entity.PhoneData;
import by.aleksabrakor.bank_accounts.entity.User;
import by.aleksabrakor.bank_accounts.exception.AlreadyExistsException;
import by.aleksabrakor.bank_accounts.exception.NotFoundException;
import by.aleksabrakor.bank_accounts.repository.PhoneDataRepository;
import by.aleksabrakor.bank_accounts.service.PhoneDataService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PhoneDataServiceImpl implements PhoneDataService {

    private final UserServiceImpl userService;
    private final PhoneDataRepository phoneDataRepository;

    @CacheEvict(value = "users", key = "#userId")
    @Transactional
    public UserResponse addPhone(Long userId, PhoneAddOrDeleteRequest request) {
        log.info("Добавление phone для пользователя [userId: {}] | Запрос: {}", userId, request.getNumber());

        String phone = request.getNumber();
        User user = userService.findUserOrThrow(userId);

        validatePhoneUniqueness(phone);
        log.debug("Phone {} прошел проверку уникальности", phone);

        PhoneData newPhone = createPhoneEntity(user, phone);
        phoneDataRepository.save(newPhone);
        log.info("Phone {} добавлен для пользователя [userId: {}] | Новый ID newPhone: {}",
                phone, userId, newPhone.getId());

        return userService.getUserById(userId);
    }

    @CacheEvict(value = "users", key = "#userId")
    @Transactional
    public UserResponse updatePhone(Long userId, PhoneUpdateRequest request) {
        String oldPhone = request.getOldNumber();
        String newPhone = request.getNewNumber();
        log.info("Обновление phone для пользователя [userId: {}] | oldEmail: {} → newEmail: {}",
                userId, oldPhone, newPhone);

        PhoneData existingPhone =getPhoneByUser(userId, oldPhone);

        validatePhoneUniqueness(newPhone);
        log.debug("Новый newPhone {} прошел проверку уникальности", newPhone);

        existingPhone.setPhone(newPhone);
        phoneDataRepository.save(existingPhone);
        log.info("Email обновлен [phoneId: {}] | {} → {}",
                existingPhone.getId(), oldPhone, newPhone);

        return userService.getUserById(userId);
    }

    @CacheEvict(value = "users", key = "#userId")
    @Transactional
    public UserResponse deletePhone(Long userId, PhoneAddOrDeleteRequest request ) {
        String phone = request.getNumber();
        log.info("Удаление phone {} для пользователя [userId: {}]", phone, userId);
        User user = userService.findUserOrThrow(userId);

        validateNotLastPhone(user);
        log.debug("Проверка на последний phone пройдена");

        PhoneData phoneToDelete = getPhoneByUser(userId, phone);

        user.getPhones().remove(phoneToDelete);
        phoneDataRepository.delete(phoneToDelete);
        log.info("Phone удален {} Пользователь [userId: {}] ",
                phone, userId);

        return userService.getUserById(userId);
    }

    private void validateNotLastPhone(User user) {
        if (user.getPhones().size() <= 1) {
            log.warn("Попытка удаления последнего phone | [userId: {}]", user.getId());
            throw new IllegalStateException("Невозможно удалить последний номер телефона");
        }
    }

    private PhoneData createPhoneEntity(User user, String phone) {
        PhoneData newPhone = new PhoneData();
        newPhone.setPhone(phone);
        newPhone.setUser(user);
        return newPhone;
    }

    private void validatePhoneUniqueness(String phone) {
        if (phoneDataRepository.existsByPhone(phone)) {
            log.warn("Phone уже существует в системе | {}", phone);
            throw new AlreadyExistsException("Этот номер телефона уже существует");
        }
    }

    private PhoneData getPhoneByUser(Long userId, String oldPhone) {
        return phoneDataRepository.findByPhoneAndUserId(oldPhone, userId)
                .orElseThrow(() ->{
                    log.warn("Phone не найден |  [usrId: {}] | Phone: {}", userId, oldPhone);
                    return new NotFoundException("Phone не найден: " + oldPhone);
                });
    }
}
