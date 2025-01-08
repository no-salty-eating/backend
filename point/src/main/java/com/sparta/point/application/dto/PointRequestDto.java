package com.sparta.point.application.dto;

import com.sparta.point.domain.model.PointHistoryType;

public record PointRequestDto(int pointAmount, PointHistoryType pointType, String description) {
}
