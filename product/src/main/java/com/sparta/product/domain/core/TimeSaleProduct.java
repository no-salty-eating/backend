package com.sparta.product.domain.core;

import com.sparta.product.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Getter
@DynamicUpdate
@Table(name = "TB_TIMESALE_PRODUCT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TimeSaleProduct extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "discount_rate")
    private Integer discountRate;

    @Column(name = "discount_price")
    private Integer discountPrice;

    private Integer stock;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "timesale_start_time")
    private LocalDateTime timeSaleStartTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "timesale_end_time")
    private LocalDateTime timeSaleEndTime;

    @Column(name = "is_sold_out")
    private Boolean isSoldOut = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    public static TimeSaleProduct createOf(Integer discountRate, Integer quantity, LocalDateTime timeSaleStartTime, LocalDateTime timeSaleEndTime, Product product) {
        return new TimeSaleProduct(
                product,
                discountRate,
                quantity,
                timeSaleStartTime,
                timeSaleEndTime
        );
    }

    private TimeSaleProduct(Product product, Integer discountRate, Integer stock,
                            LocalDateTime timeSaleStartTime, LocalDateTime timeSaleEndTime) {
        double discountedPrice = product.getPrice() * (1 - discountRate / 100.0);

        this.product = product;
        this.discountRate = discountRate;
        this.discountPrice = (int) (Math.round(discountedPrice / 10.0) * 10);
        this.stock = stock;
        this.timeSaleStartTime = timeSaleStartTime;
        this.timeSaleEndTime = timeSaleEndTime;
    }

    public void updateIsSoldOut(Boolean isSoldOut) {
        this.isSoldOut = isSoldOut;
    }

    public void decreaseStock(Integer quantity) {
        stock -= quantity;
    }

    public void increaseStock(Integer quantity) {
        stock += quantity;
    }
}
