package com.yourorg.grading.port.in;

public interface ComparisonSavePort {
    void saveResult(String userId, String requestId, String loginId, Double psnrAvg, Double ssimAvg, String task);
}
