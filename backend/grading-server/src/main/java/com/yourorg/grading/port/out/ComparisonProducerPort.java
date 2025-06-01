package com.yourorg.grading.port.out;

import com.yourorg.grading.adapter.out.dto.SendRequestDto;

public interface ComparisonProducerPort {
    void sendZipFile(SendRequestDto dto);
    void sendTaskZipFile(SendRequestDto dto);
}
