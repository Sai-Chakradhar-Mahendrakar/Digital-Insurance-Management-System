package com.innov8ors.insurance.repository.dao;

import com.innov8ors.insurance.entity.User;
import com.innov8ors.insurance.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDao extends UserRepository {
    default Optional<User> getByEmail(String email) {
        return findByEmail(email);
    }
}
