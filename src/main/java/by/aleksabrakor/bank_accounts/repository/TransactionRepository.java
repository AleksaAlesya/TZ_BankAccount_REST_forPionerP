package by.aleksabrakor.bank_accounts.repository;


import by.aleksabrakor.bank_accounts.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

}