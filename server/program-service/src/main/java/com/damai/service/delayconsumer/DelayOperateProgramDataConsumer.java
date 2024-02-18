package com.damai.service.delayconsumer;

import com.alibaba.fastjson.JSON;
import com.damai.core.ConsumerTask;
import com.damai.core.StringUtil;
import com.damai.dto.ProgramOperateDataDto;
import com.damai.service.ProgramService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.damai.service.constant.ProgramOrderConstant.DELAY_OPERATE_PROGRAM_DATA_TOPIC;

/**
 * @program: 极度真实还原大麦网高并发实战项目。 添加 阿宽不是程序员 微信，添加时备注 damai 来获取项目的完整资料 
 * @description: 节目缓存操作
 * @author: 阿宽不是程序员
 **/
@Slf4j
@Component
public class DelayOperateProgramDataConsumer implements ConsumerTask {
    
    @Autowired
    private ProgramService programService;
    
    @Override
    public void execute(String content) {
        log.info("延迟操作节目数据消息进行消费 content : {}", content);
        if (StringUtil.isEmpty(content)) {
            log.error("延迟队列消息不存在");
            return;
        }
        ProgramOperateDataDto programOperateDataDto = JSON.parseObject(content, ProgramOperateDataDto.class);
        programService.OperateProgramData(programOperateDataDto);
    }
    
    @Override
    public String topic() {
        return DELAY_OPERATE_PROGRAM_DATA_TOPIC;
    }
}
