package by.aleksabrakor.bank_accounts.service.impl;


import by.aleksabrakor.bank_accounts.dto.response.UserResponse;
import by.aleksabrakor.bank_accounts.entity.User;
import by.aleksabrakor.bank_accounts.exception.NotFoundException;
import by.aleksabrakor.bank_accounts.mapper.UserMapper;
import by.aleksabrakor.bank_accounts.repository.UserRepository;
import by.aleksabrakor.bank_accounts.service.UserService;
import by.aleksabrakor.bank_accounts.repository.specification.UserSpecifications;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserSpecifications userSpecifications;
    private final UserMapper userMapper;


    @Cacheable(value = "users", key = "#id")
    public UserResponse getUserById(Long userId) {
        User user = findUserOrThrow(userId);
        log.debug("Пользователь [userId: {}] успешно получен", userId);
        return userMapper.userToUserResponse(user);
    }


    public Page<UserResponse> searchUsers(String name,
                                          LocalDate dateOfBirth,
                                          String phone,
                                          String email,
                                          Pageable pageable
    ) {
        log.info("Поиск пользователей | Параметры: "
                 + "name={}, dateOfBirth={}, phone={}, email={}, page={}, size={}",
                name, dateOfBirth, phone, email,
                pageable.getPageNumber(), pageable.getPageSize());

        Specification<User> spec = Specification.where(null);
        if (name != null) {
            spec = spec.and(userSpecifications.nameStartsWith(name));
        }
        if (dateOfBirth != null) {
            spec = spec.and(userSpecifications.bornAfter(dateOfBirth));
        }
        if (phone != null) {
            spec = spec.and(userSpecifications.hasExactPhone(phone));
        }
        if (email != null) {
            spec = spec.and(userSpecifications.hasExactEmail(email));
        }
        log.debug("Сформированная спецификация: {}", spec);

        return userRepository.findAll(spec, pageable)
                .map(userMapper::userToUserResponse);
    }


    User findUserOrThrow(Long userId) {
        log.debug("Поиск пользователя в БД по userId: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Пользователь с userId: {} не найден в БД", userId);
                    return new NotFoundException("Юзер с id = " + userId + " не найден");
                });
    }
}
