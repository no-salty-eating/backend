package com.sparta.product.application.exception.scheduler;

import com.sparta.product.application.exception.common.Error;
import org.springframework.http.HttpStatus;

public class TimeSaleScheduleException extends ScheduleException {
    public TimeSaleScheduleException() {
        super(Error.TIMESALE_SCHEDULE_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
