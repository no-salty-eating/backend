package com.sparta.product.infrastructure.scheduler;

import com.sparta.product.application.scheduler.TimeSaleSchedulerService;
import com.sparta.product.application.scheduler.redis.TimeSaleRedisManager;
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
                for (String productId : endProducts) {
                    timeSaleSchedulerService.endTimeSale(Long.valueOf(productId));
                    redisManager.removeEndSchedule(productId);
                }
            }
        } catch (Exception e) {
            log.error("Error processing end timesales", e);
            // TODO : exception 만들기
        }
    }
}
