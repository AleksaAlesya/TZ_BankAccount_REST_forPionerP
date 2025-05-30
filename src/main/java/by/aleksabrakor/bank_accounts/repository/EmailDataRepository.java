package by.aleksabrakor.bank_accounts.repository;


import by.aleksabrakor.bank_accounts.entity.EmailData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailDataRepository extends JpaRepository<EmailData, Long>, JpaSpecificationExecutor<EmailData> {

    Boolean existsByEmail(String email);

    Optional<EmailData> findByEmailAndUserId(String oldEmail, Long userId);
}