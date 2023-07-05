package com.example.vo;

import lombok.Data;

import java.util.Date;

/**
 * @program: toolkit
 * @description:
 * @author: k
 * @create: 2023-07-05
 **/
@Data
public class TokenDataVo {

    private String id;
    
    private String name;
    
    private String secret;
    
    private Integer status;
    
    private Date createTime;
}
