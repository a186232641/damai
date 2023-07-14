package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dto.ApiDataDto;
import com.example.entity.ApiData;
import com.example.vo.ApiDataVo;

/**
 * @program: toolkit
 * @description:
 * @author: kuan
 * @create: 2023-06-14
 **/
public interface ApiDataMapper extends BaseMapper<ApiData> {
    
    Page<ApiDataVo> pageList(Page<ApiData> page, ApiDataDto apiDataDto);
}
