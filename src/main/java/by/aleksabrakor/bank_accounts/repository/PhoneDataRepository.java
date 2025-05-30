package by.aleksabrakor.bank_accounts.repository;


import by.aleksabrakor.bank_accounts.entity.PhoneData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhoneDataRepository extends JpaRepository<PhoneData, Long>, JpaSpecificationExecutor<PhoneData> {
    Boolean existsByPhone(String phone);

    Optional<PhoneData> findByPhoneAndUserId(String phone, Long userId);
}