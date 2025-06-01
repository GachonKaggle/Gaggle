package com.yourorg.grading.port.in;

import com.yourorg.grading.adapter.out.dto.SendRequestDto;

public interface ComparisonPort {
    void comparisionRequest(SendRequestDto dto);
}
