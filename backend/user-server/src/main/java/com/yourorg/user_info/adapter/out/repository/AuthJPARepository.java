package com.yourorg.user_info.adapter.out.repository;

import com.yourorg.user_info.domain.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuthJPARepository extends MongoRepository<User, String> {
    User findByLoginId(String loginId);
}
