package by.aleksabrakor.bank_accounts.service.impl;


import by.aleksabrakor.bank_accounts.dto.request.AuthRequest;
import by.aleksabrakor.bank_accounts.entity.User;
import by.aleksabrakor.bank_accounts.repository.UserRepository;
import by.aleksabrakor.bank_accounts.security.JwtService;
import by.aleksabrakor.bank_accounts.security.JwtUserDetails;
import by.aleksabrakor.bank_accounts.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    public String authenticate(AuthRequest request) {
        log.info("Попытка аутентификации для логина: {}", request.getLogin());
        User user = userRepository.findByEmailOrPhone(request.getLogin())
                .orElseThrow(() -> {
                    log.warn("Пользователь не найден: {}", request.getLogin());
                   return new UsernameNotFoundException("Пользователь с таким email/number не найден");

                });
        log.debug("Найден пользователь [ID: {}] для входа", user.getId());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Неверный пароль для пользователя [ID: {}]", user.getId());
            throw new BadCredentialsException("Неверный пароль");
        }

        String token = jwtService.generateToken(user.getId());
        log.info("Успешная аутентификация [ID: {}]. Токен сгенерирован", user.getId());

        return token;
    }


    public void checkSelfAccess(Long targetUserId) {
        log.debug("Проверка доступа к ресурсу пользователя [ID: {}]", targetUserId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();

        if (!userDetails.getId().equals(targetUserId)) {
            log.warn("Отказ в доступе. Текущий пользователь [ID: {}] запросил ресурс [ID: {}]",
                    userDetails.getId(), targetUserId);
            throw new AccessDeniedException("Доступ запрещен");
        }
        log.debug("Доступ к ресурсу [ID: {}] подтвержден", targetUserId);
    }
}
