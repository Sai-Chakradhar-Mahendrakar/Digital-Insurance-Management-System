package com.innov8ors.insurance.repository.dao;

import com.innov8ors.insurance.entity.User;
import com.innov8ors.insurance.enums.Role;
import com.innov8ors.insurance.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDao extends UserRepository {
    default Optional<User> getByEmail(String email) {
        return findByEmail(email);
    }

    default Boolean userExistsByEmail(String email) {
        return existsByEmail(email);
    }

    default Page<User> getByRole(Role role, Pageable pageable) {
        return findByRole(role, pageable);
    }
}
