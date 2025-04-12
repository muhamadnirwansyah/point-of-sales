package com.be.pos.backend_app.repository;

import com.be.pos.backend_app.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long> {

    @Query(value = "SELECT * FROM account WHERE email=:email",nativeQuery = true)
    Optional<Account> getAccountByEmail(@Param("email")String email);

    @Query(value = "SELECT COUNT(*) FROM account WHERE email=:email",nativeQuery = true)
    Long checkEmail(@Param("email")String email);

    @Query(value = "SELECT COUNT(*) FROM account WHERE phone_number=:phoneNumber",nativeQuery = true)
    Long checkPhoneNumber(@Param("phoneNumber")String phoneNumber);
}
