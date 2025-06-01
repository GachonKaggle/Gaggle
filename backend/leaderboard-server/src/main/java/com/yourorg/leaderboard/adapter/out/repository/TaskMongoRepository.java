package com.yourorg.leaderboard.adapter.out.repository;

import com.yourorg.leaderboard.domain.entity.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

public interface TaskMongoRepository extends MongoRepository<Task, String> {

    // 전체 Task의 taskName만 projection으로 가져오기
    @Query(value = "{}", fields = "{ 'taskName' : 1, '_id': 0 }")
    List<TaskNameOnly> findAllTaskNames();

    // Projection 인터페이스
    interface TaskNameOnly {
        String getTaskName();
    }
}
