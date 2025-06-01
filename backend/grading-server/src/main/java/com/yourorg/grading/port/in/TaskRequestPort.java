package com.yourorg.grading.port.in;

import com.yourorg.grading.adapter.out.dto.SendRequestDto;

public interface TaskRequestPort {
    void taskRequest(SendRequestDto dto);
}
