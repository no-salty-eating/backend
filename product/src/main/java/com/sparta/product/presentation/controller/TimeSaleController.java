package com.sparta.product.presentation.controller;

import com.sparta.product.application.dtos.timesale.TimeSaleProductPurchaseRequestDto;
import com.sparta.product.application.dtos.timesale.TimeSaleProductRequestDto;
import com.sparta.product.application.service.TimeSaleService;
import com.sparta.product.application.dtos.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/products/timedeals")
@RequiredArgsConstructor
public class TimeSaleController {

    private final TimeSaleService timeSaleService;

    @PostMapping
    public Response<Void> createTimeSaleProduct(@RequestBody @Valid TimeSaleProductRequestDto timeSaleProductRequestDto,
                                                @RequestHeader(name = "X-UserId", required = false) String userId,
                                                @RequestHeader(name = "X-Role") String role) {
        timeSaleService.createTimeSaleProduct(timeSaleProductRequestDto, role);
        return Response.<Void>builder()
                .code(HttpStatus.CREATED.value())
                .message(HttpStatus.CREATED.getReasonPhrase())
                .build();
    }

    // TODO : kafka 쪽으로 옮겨야..
    @PostMapping("/timesale-quantity-decrease")
    public Response<Void> purchaseTimeSaleProduct(@RequestBody @Valid TimeSaleProductPurchaseRequestDto timeSaleProductPurchaseRequestDto,
                                                  @RequestHeader(name = "X-UserId", required = false) String userId,
                                                  @RequestHeader(name = "X-Role") String role) {
        timeSaleService.purchaseTimeSaleProduct(timeSaleProductPurchaseRequestDto, role);
        return Response.<Void>builder().build();
    }
}
