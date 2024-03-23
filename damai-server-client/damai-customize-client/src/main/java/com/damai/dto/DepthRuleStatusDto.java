package com.damai.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @program: 极度真实还原大麦网高并发实战项目。 添加 阿宽不是程序员 微信，添加时备注 大麦 来获取项目的完整资料 
 * @description: 深度规则状态修改 dto
 * @author: 阿宽不是程序员
 **/
@Data
@ApiModel(value="DepthRuleStatusDto", description ="深度规则状态修改")
public class DepthRuleStatusDto {
    
    @ApiModelProperty(name ="id", dataType ="String", value ="深度规则id", required =true)
    private Long id;
    
    @ApiModelProperty(name ="status", dataType ="Integer", value ="状态 1生效 0禁用", required =true)
    @NotNull
    private Integer status;
}
