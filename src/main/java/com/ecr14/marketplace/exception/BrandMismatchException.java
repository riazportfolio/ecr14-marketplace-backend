package com.ecr14.marketplace.exception;

public class BrandMismatchException extends RuntimeException {

    private final String currentBrandId;
    private final String currentBrandName;
    private final String attemptedBrandId;
    private final String attemptedBrandName;

    public BrandMismatchException(String currentBrandId, String currentBrandName,
                                   String attemptedBrandId, String attemptedBrandName) {
        super(String.format("Cart contains items from '%s'. Cannot add items from '%s'. " +
                "Please clear your cart first.", currentBrandName, attemptedBrandName));
        this.currentBrandId = currentBrandId;
        this.currentBrandName = currentBrandName;
        this.attemptedBrandId = attemptedBrandId;
        this.attemptedBrandName = attemptedBrandName;
    }

    public String getCurrentBrandId() {
        return currentBrandId;
    }

    public String getCurrentBrandName() {
        return currentBrandName;
    }

    public String getAttemptedBrandId() {
        return attemptedBrandId;
    }

    public String getAttemptedBrandName() {
        return attemptedBrandName;
    }
}
