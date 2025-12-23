package com.ecr14.marketplace.exception;

import java.time.LocalDate;

public class InvalidDeliveryDateException extends RuntimeException {

    private final LocalDate requestedDate;
    private final LocalDate earliestDate;
    private final Integer minNoticeDays;

    public InvalidDeliveryDateException(LocalDate requestedDate, LocalDate earliestDate, Integer minNoticeDays) {
        super(String.format("Delivery date %s is too soon. This brand requires %d day(s) notice. " +
                        "Earliest available date is %s.",
                requestedDate, minNoticeDays, earliestDate));
        this.requestedDate = requestedDate;
        this.earliestDate = earliestDate;
        this.minNoticeDays = minNoticeDays;
    }

    public LocalDate getRequestedDate() {
        return requestedDate;
    }

    public LocalDate getEarliestDate() {
        return earliestDate;
    }

    public Integer getMinNoticeDays() {
        return minNoticeDays;
    }
}
