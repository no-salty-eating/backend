package com.sparta.point.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_POINT_HISTORY")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PointHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private int pointAmount;

    private String description;

    @Enumerated(EnumType.STRING)
    private PointHistoryType pointHistoryType;

    public PointHistory(Long userId, int pointAmount, String description, PointHistoryType pointHistoryType) {
        this.userId = userId;
        this.pointAmount = pointAmount;
        this.description = description;
        this.pointHistoryType = pointHistoryType;
    }
}
