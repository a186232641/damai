package com.damai.client;

import com.damai.common.ApiResponse;
import com.damai.dto.JobCallBackDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

/**
 * @program: 极度真实还原大麦网高并发实战项目。 添加 阿宽不是程序员 微信，添加时备注 damai 来获取项目的完整资料 
 * @description: job服务 feign
 * @author: 阿宽不是程序员
 **/
@Component
@FeignClient(value = "job-service",fallback = JobClientFallback.class)
public interface JobClient {
    
    @RequestMapping(value = "jobRunRecord/callBack", method = RequestMethod.POST)
    ApiResponse<Boolean> callBack(@Valid @RequestBody JobCallBackDto dto);
}
