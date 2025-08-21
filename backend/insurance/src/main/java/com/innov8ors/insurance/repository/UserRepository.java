package com.innov8ors.insurance.repository;

import com.innov8ors.insurance.entity.User;
import com.innov8ors.insurance.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface UserRepository extends BaseRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    Page<User> findByRole(Role role, Pageable pageable);
}
