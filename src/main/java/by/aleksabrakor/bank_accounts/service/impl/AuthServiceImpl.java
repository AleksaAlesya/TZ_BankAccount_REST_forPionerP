package by.aleksabrakor.bank_accounts.service.impl;


import by.aleksabrakor.bank_accounts.dto.request.AuthRequest;
import by.aleksabrakor.bank_accounts.entity.User;
import by.aleksabrakor.bank_accounts.repository.UserRepository;
import by.aleksabrakor.bank_accounts.security.JwtService;
import by.aleksabrakor.bank_accounts.security.JwtUserDetails;
import by.aleksabrakor.bank_accounts.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    public String authenticate(AuthRequest request) {
        User user = userRepository.findByEmailOrPhone(request.getLogin())
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с таким email/number не найден"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Неверный пароль");
        }

        return jwtService.generateToken(user.getId());
    }


    public void checkSelfAccess(Long targetUserId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();

        if (!userDetails.getId().equals(targetUserId)) {
            throw new AccessDeniedException("Доступ запрещен");
        }
    }
}
