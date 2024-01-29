package com.example.entity;

import lombok.Data;

/**
 * @program: cook-frame
 * @description:
 * @author: k
 * @create: 2024-01-29
 **/
@Data
public class OrderTicketUserAggregate {
    
    private Long orderId;
    
    private Integer orderTicketUserCount;
}
