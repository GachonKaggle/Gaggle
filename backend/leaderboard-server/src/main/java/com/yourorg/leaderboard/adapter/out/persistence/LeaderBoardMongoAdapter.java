package com.yourorg.leaderboard.adapter.out.persistence;

import com.yourorg.leaderboard.port.out.LeaderBoardQueryPort;
import com.yourorg.leaderboard.domain.entity.LeaderBoard;
import com.yourorg.leaderboard.adapter.in.dto.LeaderBoardDto;
import com.yourorg.leaderboard.adapter.out.repository.LeaderBoardMongoRepository;
import com.yourorg.leaderboard.util.DateUtils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class LeaderBoardMongoAdapter implements LeaderBoardQueryPort {

    private final LeaderBoardMongoRepository leaderBoardMongoRepository;

    // 전체 유저 랭킹 (psnrAvg 내림차순 + 순위 동적 계산)
    @Override
    public List<LeaderBoardDto> loadLeaderBoardsByTask(String task) {
        List<LeaderBoard> entities = leaderBoardMongoRepository.findByTaskOrderByPsnrAvgDesc(task);
        
        // 인덱스를 활용해서 순위(1등부터)를 DTO에 매핑
        return IntStream.range(0, entities.size())
                .mapToObj(i -> {
                    LeaderBoard e = entities.get(i);
                    long rank = i + 1L;
                    String days = DateUtils.getDaysAgoString(e.getDays());
                    return new LeaderBoardDto(e.getLoginId(), e.getPsnrAvg(), e.getSsimAvg(), e.getTask(), rank, days);
                })
                .collect(Collectors.toList());
    }

    // 개별 유저의 현재 랭킹 조회 (해당 task에서 userId의 현재 순위)
    @Override
    public List<LeaderBoardDto> loadLeaderBoardsByUserAndTask(String userId, String task) {
        List<LeaderBoard> entities = leaderBoardMongoRepository.findByTaskOrderByPsnrAvgDesc(task);

        return IntStream.range(0, entities.size())
                .filter(i -> {
                    String dbUserId = entities.get(i).getUserId();
                    return dbUserId != null && dbUserId.equals(userId);
                })
                .mapToObj(i -> {
                    LeaderBoard e = entities.get(i);
                    long rank = i + 1L;
                    String days = DateUtils.getDaysAgoString(e.getDays());
                    return new LeaderBoardDto(
                        e.getLoginId(),
                        e.getPsnrAvg(),
                        e.getSsimAvg(),
                        e.getTask(),
                        rank,
                        days
                    );
                })
                .limit(5) // ★ 최대 5개만 반환
                .collect(Collectors.toList());
    }
}
