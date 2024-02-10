package com.example.controller;

import com.example.common.ApiResponse;
import com.example.dto.ProgramAddDto;
import com.example.dto.ProgramGetDto;
import com.example.dto.ProgramListDto;
import com.example.dto.ProgramPageListDto;
import com.example.dto.ProgramSearchDto;
import com.example.page.PageVo;
import com.example.service.ProgramService;
import com.example.vo.ProgramListVo;
import com.example.vo.ProgramVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 节目表 前端控制器
 * </p>
 *
 * @author k
 * @since 2024-01-08
 */
@RestController
@RequestMapping("/program")
@Api(tags = "program", description = "节目")
public class ProgramController {
    
    @Autowired
    private ProgramService programService;
    
    @ApiOperation(value = "添加")
    @PostMapping(value = "/add")
    public ApiResponse<Long> add(@Valid @RequestBody ProgramAddDto programAddDto) {
        return ApiResponse.ok(programService.add(programAddDto));
    }
    
    @ApiOperation(value = "搜索")
    @PostMapping(value = "/search")
    public ApiResponse<PageVo<ProgramListVo>> search(@Valid @RequestBody ProgramSearchDto programSearchDto) {
        return ApiResponse.ok(programService.search(programSearchDto));
    }
    
    @ApiOperation(value = "查询主页列表")
    @PostMapping(value = "/home/list")
    public ApiResponse<Map<String,List<ProgramListVo>>> selectHomeList(@Valid @RequestBody ProgramListDto programPageListDto) {
        return ApiResponse.ok(programService.selectHomeList(programPageListDto));
    }
    
    @ApiOperation(value = "查询分页列表")
    @PostMapping(value = "/page")
    public ApiResponse<PageVo<ProgramListVo>> selectPage(@Valid @RequestBody ProgramPageListDto programPageListDto) {
        return ApiResponse.ok(programService.selectPage(programPageListDto));
    }
    
    @ApiOperation(value = "查询详情(根据id)")
    @PostMapping(value = "/detail")
    public ApiResponse<ProgramVo> getDetail(@Valid @RequestBody ProgramGetDto programGetDto) {
        return ApiResponse.ok(programService.getDetail(programGetDto));
    }
    
    @PostMapping(value = "/es/index/add")
    public ApiResponse<Void> indexAdd(@Valid @RequestBody ProgramGetDto programGetDto) {
        programService.indexAdd(programGetDto);
        return ApiResponse.ok();
    }
    @PostMapping(value = "/es/data/add")
    public ApiResponse<ProgramVo> dataAdd(@Valid @RequestBody ProgramGetDto programGetDto) {
        return ApiResponse.ok(programService.dataAdd(programGetDto));
    }
}
