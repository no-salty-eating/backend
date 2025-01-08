package com.sparta.point.application.service;

import com.sparta.point.application.dto.PointRequestDto;
import com.sparta.point.application.dto.PointResponseDto;
import com.sparta.point.application.exception.NotFoundPointException;
import com.sparta.point.domain.model.Point;
import com.sparta.point.domain.model.PointHistory;
import com.sparta.point.domain.repository.PointHistoryRepository;
import com.sparta.point.domain.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Transactional
    public PointResponseDto createPointHistory(String userId, PointRequestDto pointRequestDto) {
        Long userIdLong = getUserLongId(userId);

        Point point = pointRepository.findByUserIdAndIsDeletedFalse(userIdLong).orElseThrow(NotFoundPointException::new);
        point.updatePoints(pointRequestDto.pointType(), pointRequestDto.pointAmount());

        PointHistory pointHistory = new PointHistory(userIdLong, pointRequestDto.pointAmount(), pointRequestDto.description(), pointRequestDto.pointType());
        pointHistoryRepository.save(pointHistory);

        return new PointResponseDto(point.getPoints());
    }

    // todo: user-service에서 Long 타입의 id 값 받아오기
    private Long getUserLongId(String userId) {
        return 1L;
    }

}
