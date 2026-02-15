package com.sky.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class OrderTask {

    @Scheduled(cron="0 * * * * ?")
    public void processTimeoutOrder(){
        log.info("处理支付超时订单: {}", new Date());

    }

    @Scheduled(cron="0 0 1 * * ?")
    public void processDeliveryOrder(){
        log.info("处理派送中订单: {}", new Date());

    }
}
