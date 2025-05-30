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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class PhoneDataServiceImpl implements PhoneDataService {

    private final UserServiceImpl userService;
    private final PhoneDataRepository phoneDataRepository;

    @CacheEvict(value = "users", key = "#userId")
    @Transactional
    public UserResponse addPhone(Long userId, PhoneAddOrDeleteRequest request) {
        String phone = request.getNumber();
        User user = userService.findUserOrThrow(userId);

        validatePhoneUniqueness(phone);
        PhoneData newPhone = createPhoneEntity(user, phone);
        phoneDataRepository.save(newPhone);

        return userService.getUserById(userId);
    }

    @CacheEvict(value = "users", key = "#userId")
    @Transactional
    public UserResponse updatePhone(Long userId, PhoneUpdateRequest request) {
        String oldPhone = request.getOldNumber();
        String newPhone = request.getNewNumber();

        PhoneData existingPhone =getPhoneByUser(userId, oldPhone);
        validatePhoneUniqueness(newPhone);

        existingPhone.setPhone(newPhone);
        phoneDataRepository.save(existingPhone);
        return userService.getUserById(userId);
    }

    @CacheEvict(value = "users", key = "#userId")
    @Transactional
    public UserResponse deletePhone(Long userId, PhoneAddOrDeleteRequest request ) {
        String phone = request.getNumber();
        User user = userService.findUserOrThrow(userId);

        validateNotLastPhone(user);
        PhoneData phoneToDelete = getPhoneByUser(userId, phone);

        user.getPhones().remove(phoneToDelete);
        phoneDataRepository.delete(phoneToDelete);
        return userService.getUserById(userId);
    }

    private void validateNotLastPhone(User user) {
        if (user.getPhones().size() <= 1) {
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
            throw new AlreadyExistsException("Этот номер телефона уже существует");
        }
    }

    private PhoneData getPhoneByUser(Long userId, String oldPhone) {
        return phoneDataRepository.findByPhoneAndUserId(oldPhone, userId)
                .orElseThrow(() -> new NotFoundException("Phone не найден: " + oldPhone));
    }
}
