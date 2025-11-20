package com.bank.account_service.infrastructure.output.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {

    private Long id;
    private String name;
    private String gender;
    private String identification;
    private String address;
    private String phone;
    private Boolean status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}