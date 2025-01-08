package com.sparta.point.presentation.controller;

import com.sparta.point.application.dto.PointRequestDto;
import com.sparta.point.application.dto.PointResponseDto;
import com.sparta.point.application.service.PointService;
import com.sparta.point.presentation.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @PostMapping
    public Response<PointResponseDto> createPointHistory(@RequestBody PointRequestDto pointRequestDto,
                                                         @RequestHeader(name = "X-UserId", required = false) String userId) {
        PointResponseDto result = pointService.createPointHistory(userId, pointRequestDto);
        return Response.<PointResponseDto>builder()
                .data(result)
                .build();
    }

}
