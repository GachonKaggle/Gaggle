package com.yourorg.leaderboard.adapter.out.repository;

import com.yourorg.leaderboard.domain.entity.LeaderBoard;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface LeaderBoardMongoRepository extends MongoRepository<LeaderBoard, String> {
    List<LeaderBoard> findByTaskOrderByPsnrAvgDesc(String task);
}
