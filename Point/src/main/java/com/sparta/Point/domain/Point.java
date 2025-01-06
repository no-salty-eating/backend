package com.sparta.Point.domain;

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
}
