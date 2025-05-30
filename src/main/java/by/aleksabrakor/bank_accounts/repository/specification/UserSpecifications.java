package by.aleksabrakor.bank_accounts.repository.specification;

import by.aleksabrakor.bank_accounts.entity.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class UserSpecifications {

    public  Specification<User> nameStartsWith(String prefix) {
        return (root, query, cb) -> prefix == null ? null :
                cb.like(
                        cb.lower(root.get("name")),
                        prefix.toLowerCase() + "%"
                );
    }

    public  Specification<User> bornAfter(LocalDate date) {
        return (root, query, cb) -> date == null ? null :
                cb.greaterThan(
                        root.get("dateOfBirth"),
                        date
                );
    }

    public  Specification<User> hasExactPhone(String phone) {
        return (root, query, cb) -> phone == null ? null :
                cb.equal(
                        root.join("phones").get("phone"),
                        phone
                );
    }

    public Specification<User> hasExactEmail(String email) {
        return (root, query, cb) -> email == null ? null :
                cb.equal(
                        root.join("emails").get("email"),
                        email
                );
    }
}
