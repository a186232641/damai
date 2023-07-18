package com.example.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.ApiResponse;
import com.example.dto.ApiDataDto;
import com.example.service.ApiDataService;
import com.example.vo.ApiDataVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @program: cook-frame
 * @description:
 * @author: 星哥
 * @create: 2023-04-17
 **/
@RestController
@RequestMapping("/apiData")
@Api(tags = "apiData", description = "api调用记录")
public class ApiDataController {
    
    @Autowired
    private ApiDataService apiDataService;
    
    @ApiOperation(value = "分页查询api调用记录")
    @RequestMapping(value = "/pageList",method = RequestMethod.POST)
    public ApiResponse<Page<ApiDataVo>> pageList(@Valid @RequestBody ApiDataDto dto) {
        return ApiResponse.ok(apiDataService.pageList(dto));
    }
}
