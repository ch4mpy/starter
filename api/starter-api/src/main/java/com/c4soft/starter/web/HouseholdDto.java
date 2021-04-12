package com.c4soft.starter.web;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HouseholdDto {
    private long id;
    private String label;
    private String type;
    private TaxpayerDto taxpayer;
}
