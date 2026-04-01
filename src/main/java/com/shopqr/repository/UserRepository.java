package com.shopqr.repository;

import com.shopqr.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhoneNumber(String phoneNumber);
    List<User> findByCompanyId(Long companyId);
    List<User> findByIsApproved(boolean isApproved);
}
