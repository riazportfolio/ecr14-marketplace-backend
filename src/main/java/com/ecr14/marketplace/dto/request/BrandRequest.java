package com.ecr14.marketplace.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandRequest {

    @NotBlank(message = "Brand name is required")
    @Size(max = 100, message = "Brand name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    private String logo;

    @NotBlank(message = "Phone number is required")
    @Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 digits")
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @NotEmpty(message = "At least one category is required")
    private List<String> categories;

    @NotNull(message = "Minimum notice days is required")
    @Min(value = 0, message = "Minimum notice days cannot be negative")
    @Max(value = 30, message = "Minimum notice days cannot exceed 30 days")
    private Integer minNoticeDays = 0;
}
