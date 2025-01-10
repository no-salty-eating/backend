package com.sparta.product.application.dtos.timesale;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sparta.product.domain.core.TimeSaleProduct;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TimeSaleProductResponseDto(
        @JsonProperty("timesale_product_id") Long timeSaleProductId,
        @JsonProperty("product_id") Long productId,
        @JsonProperty("discount_rate") Integer discountRate,
        @JsonProperty("discount_price") Integer discountPrice,
        Integer quantity,
        @JsonProperty("timesale_start_time") LocalDateTime timeSaleStartTime,
        @JsonProperty("timesale_end_time") LocalDateTime timeSaleEndTime,
        @JsonProperty("is_sold_out") Boolean isSoldOut
) {
    public static TimeSaleProductResponseDto createFrom(TimeSaleProduct timeSaleProduct) {
        return new TimeSaleProductResponseDto(
                timeSaleProduct.getId(),
                timeSaleProduct.getProduct().getId(),
                timeSaleProduct.getDiscountRate(),
                timeSaleProduct.getDiscountPrice(),
                timeSaleProduct.getStock(),
                timeSaleProduct.getTimeSaleStartTime(),
                timeSaleProduct.getTimeSaleEndTime(),
                timeSaleProduct.getIsSoldOut()
        );
    }
}
