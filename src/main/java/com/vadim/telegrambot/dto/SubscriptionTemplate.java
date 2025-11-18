package com.vadim.telegrambot.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubscriptionTemplate {
    @NotBlank(message = "Unique code must not be blank")
    private String uniqueCode;

    @NotBlank(message = "Name must not be blank")
    private String name;

    @NotNull(message = "Price must not be null")
    @Min(value = 0, message = "price must be non-negative")
    private Integer price;

    @NotNull(message = "Day of month must not be null")
    @Min(value = 1, message = "dayOfMonth must be >= 1")
    @Max(value = 31, message = "dayOfMonth must be <= 31")
    private Integer dayOfMonth;
}
