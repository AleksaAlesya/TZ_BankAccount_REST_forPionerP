package by.aleksabrakor.bank_accounts.service.impl;


import by.aleksabrakor.bank_accounts.dto.response.UserResponse;
import by.aleksabrakor.bank_accounts.entity.User;
import by.aleksabrakor.bank_accounts.exception.NotFoundException;
import by.aleksabrakor.bank_accounts.mapper.UserMapper;
import by.aleksabrakor.bank_accounts.repository.UserRepository;
import by.aleksabrakor.bank_accounts.service.UserService;
import by.aleksabrakor.bank_accounts.repository.specification.UserSpecifications;
import lombok.RequiredArgsConstructor;

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
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserSpecifications userSpecifications;
    private final UserMapper userMapper;


    @Cacheable(value = "users", key = "#id")
    public UserResponse getUserById(Long id) {
        return userMapper.userToUserResponse(findUserOrThrow(id));
    }


    public Page<UserResponse> searchUsers(String name,
                                          LocalDate dateOfBirth,
                                          String phone,
                                          String email,
                                          Pageable pageable
    ) {
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
        return userRepository.findAll(spec, pageable)
                .map(userMapper::userToUserResponse);
    }

    User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Юзер с id = " + userId + " не найден"));
    }
}
