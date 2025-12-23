package com.ecr14.marketplace.entity;

public enum OrderStatus {
    PENDING,      // Order created, WhatsApp sent
    CONFIRMED,    // Admin confirmed via dashboard
    DELIVERED,    // Admin marked as delivered
    CANCELLED     // User or Admin cancelled
}
