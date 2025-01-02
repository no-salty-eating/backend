package com.sparta.product.domain.core;

import com.sparta.product.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TimeSaleProduct extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "discount_rate")
    private int discountRate;

    @Column(name = "discount_price")
    private int discountPrice;

    private int quantity;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "timesale_start_time")
    private LocalDateTime timeSaleStartTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "timesale_end_time")
    private LocalDateTime timeSaleEndTime;

    @Column(name = "is_sold_out")
    private boolean isSoldOut;

    @OneToMany(mappedBy = "timeSaleProduct", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<TimeSaleSoldOut> timeSaleSoldOutList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
}
