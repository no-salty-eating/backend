package com.sparta.product.domain.core;

import com.sparta.product.application.dtos.timesale.TimeSaleProductRequestDto;
import com.sparta.product.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.List;

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

    private Integer quantity;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "timesale_start_time")
    private LocalDateTime timeSaleStartTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "timesale_end_time")
    private LocalDateTime timeSaleEndTime;

    @Column(name = "is_sold_out")
    private Boolean isSoldOut = false;

    @OneToMany(mappedBy = "timeSaleProduct", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<TimeSaleSoldOut> timeSaleSoldOutList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    public static TimeSaleProduct createOf(TimeSaleProductRequestDto timeSaleProductRequestDto, Product product) {
        return new TimeSaleProduct(
                product,
                timeSaleProductRequestDto.discountRate(),
                timeSaleProductRequestDto.quantity(),
                timeSaleProductRequestDto.timeSaleStartTime(),
                timeSaleProductRequestDto.timeSaleEndTime()
        );
    }

    private TimeSaleProduct(Product product, Integer discountRate, Integer quantity,
                            LocalDateTime timeSaleStartTime, LocalDateTime timeSaleEndTime) {
        this.product = product;
        this.discountRate = discountRate;
        discountPrice = product.getPrice() * (1 - discountRate / 100);
        this.quantity = quantity;
        this.timeSaleStartTime = timeSaleStartTime;
        this.timeSaleEndTime = timeSaleEndTime;
    }

    public void addTimeSaleSoldOutList(TimeSaleSoldOut timeSaleSoldOut) {
        timeSaleSoldOutList.add(timeSaleSoldOut);
        timeSaleSoldOut.updateTimeSaleProduct(this);
    }

    public void updateProduct(Product product) {
        this.product = product;
    }
}
