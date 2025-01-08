package com.sparta.point.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_POINT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Point extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private int points;

    public Point(Long userId, int points) {
        this.userId = userId;
        this.points = points;
    }

    public void updatePoints(PointHistoryType pointHistoryType, int pointAmount) {
        if (pointHistoryType == PointHistoryType.EARN) {
            points += pointAmount;
        } else {
            if (points - pointAmount < 0) {
                throw new IllegalArgumentException("사용 가능한 적립금을 초과했습니다.");
            }
            points -= pointAmount;
        }
    }
}
