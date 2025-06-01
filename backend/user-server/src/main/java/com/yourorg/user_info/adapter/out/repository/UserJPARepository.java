package com.yourorg.user_info.adapter.out.repository;

import java.util.Optional;
import com.yourorg.user_info.domain.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;



public interface UserJPARepository extends MongoRepository<User, String> {
    Optional<User> findByLoginId(String findByLoginId);
}