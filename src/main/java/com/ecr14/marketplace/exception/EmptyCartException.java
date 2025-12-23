package com.ecr14.marketplace.exception;

public class EmptyCartException extends RuntimeException {

    public EmptyCartException() {
        super("Cannot place order with an empty cart. Please add items to your cart first.");
    }

    public EmptyCartException(String message) {
        super(message);
    }
}
