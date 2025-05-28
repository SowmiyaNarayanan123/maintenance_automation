package com.nectar.maintenance_automation.dtos;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CassandraNodeStatus {
    private String address;
    private String status;
    private String state;
    private String load;
    private String tokens;
    private String owns;
    private String hostId;
    private String rack;
} 