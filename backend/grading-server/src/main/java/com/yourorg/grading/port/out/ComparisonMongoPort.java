package com.yourorg.grading.port.out;

public interface ComparisonMongoPort {
    void saveResultJPA(String userId, String requestId, String loginId, Double psnrAvg, Double ssimAvg, String task);
    void saveTaskRepository(String token, String taskName);
}
