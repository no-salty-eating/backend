package com.sparta.product.domain.core;

import com.sparta.product.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "TB_TIMESALE_SOLDOUT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TimeSaleSoldOut extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "sold_out_time")
    private LocalDateTime soldOutTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timesale_id")
    private TimeSaleProduct timeSaleProduct;

    public void updateTimeSaleProduct(TimeSaleProduct timeSaleProduct) {
        this.timeSaleProduct = timeSaleProduct;
    }
}
