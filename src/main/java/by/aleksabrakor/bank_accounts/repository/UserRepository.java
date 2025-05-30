package by.aleksabrakor.bank_accounts.repository;


import by.aleksabrakor.bank_accounts.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    @Query("SELECT u FROM User u " +
           "LEFT JOIN u.emails e " +
           "LEFT JOIN u.phones p " +
           "WHERE e.email = :login OR p.phone = :login")
    Optional<User> findByEmailOrPhone(@Param("login") String login);

}