package com.sparta.product.application.exception.scheduler;

import com.sparta.product.application.exception.common.Error;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class TimeSaleScheduleException extends ScheduleException {
    public TimeSaleScheduleException() {
        super(Error.TIMESALE_SCHEDULE_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
