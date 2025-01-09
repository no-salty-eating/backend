package com.sparta.product.application.scheduler.redis;

import com.sparta.product.application.exception.product.NotFoundProductException;
import com.sparta.product.application.scheduler.TimeSaleSchedulerService;
import com.sparta.product.domain.core.TimeSaleProduct;
import com.sparta.product.domain.repository.TimeSaleProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimeSaleScheduler {
    private final TimeSaleRedisManager redisManager;
    private final TimeSaleSchedulerService timeSaleSchedulerService;
    private final TimeSaleProductRepository timeSaleProductRepository;

    @Scheduled(fixedDelay = 1000)
    // 분산 환경에서 스케줄러의 동시 실행을 방지
    @SchedulerLock(name = "processTimeSales", lockAtLeastFor = "PT1S", lockAtMostFor = "PT5S")
    public void processTimeSales() {
        long currentTime = System.currentTimeMillis();
        processStartTimeSales(currentTime);
        processEndTimeSales(currentTime);
    }

    private void processStartTimeSales(long currentTime) {
        try {
            Set<String> startProducts = redisManager.getStartTimeSales(currentTime);
            if (startProducts != null && !startProducts.isEmpty()) {
                for (String productId : startProducts) {
                    TimeSaleProduct timeSaleProduct = timeSaleProductRepository.findById(Long.valueOf(productId))
                            .orElseThrow(NotFoundProductException::new);

                    redisManager.createTimeSaleProduct(timeSaleProduct);

                    timeSaleSchedulerService.startTimeSale(Long.valueOf(productId));
                    redisManager.removeStartSchedule(productId);
                }
            }
        } catch (Exception e) {
            log.error("Error processing start timesales", e);
            // TODO : exception 만들기
        }
    }

    private void processEndTimeSales(long currentTime) {
        try {
            Set<String> endProducts = redisManager.getEndTimeSales(currentTime);
            if (endProducts != null && !endProducts.isEmpty()) {
                for (String timeSaleId : endProducts) {
                    timeSaleSchedulerService.endTimeSale(Long.valueOf(timeSaleId));
                    redisManager.removeEndSchedule(timeSaleId);
                    redisManager.removeTimeSaleOn(timeSaleId);
                }
            }
        } catch (Exception e) {
            log.error("Error processing end timesales", e);
            // TODO : exception 만들기
        }
    }
}
