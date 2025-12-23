package com.ecr14.marketplace.dto.response;

import com.ecr14.marketplace.entity.Order;
import com.ecr14.marketplace.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private String id;
    private String userName;
    private String userPhone;
    private String apartmentNumber;
    private LocalDate deliveryDate;
    private String brandId;
    private String brandName;
    private String brandPhone;
    private Double totalAmount;
    private OrderStatus status;
    private Boolean whatsappSent;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;

    public static OrderResponse fromEntity(Order order, List<OrderItemResponse> items) {
        return new OrderResponse(
                order.getId(),
                order.getUserName(),
                order.getUserPhone(),
                order.getApartmentNumber(),
                order.getDeliveryDate(),
                order.getBrandId(),
                order.getBrandName(),
                order.getBrandPhone(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getWhatsappSent(),
                order.getCreatedAt(),
                items
        );
    }
}
