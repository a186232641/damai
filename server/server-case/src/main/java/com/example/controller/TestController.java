package com.example.controller;

import com.example.entity.Test;
import com.example.service.ITestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: 测试事务
 * @description:
 * @author: k
 * @create: 2023-04-24
 **/
@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private ITestService testService;

    @RequestMapping("/insert/{number}")
    public Integer insert(@PathVariable Integer number){
        Test test = new Test();
        test.setColumn1(number);
        test.setColumn2(number);
        test.setColumn3(number);
        test.setColumn4("test4");
        test.setColumn5("test5");
        test.setColumn6("test6");
        test.setNumber(number);
        int result = 0;
        try {
            result = testService.insert(test);
        }catch (Exception e) {
            log.error("出现异常",e);
        }
        return result;
    }
    @RequestMapping("getById/{id}")
    public Test getById(@PathVariable Long id){
        return testService.getById(id);
    }

    @RequestMapping("updateNumberById/{number}/{id}")
    public Integer updateNumberById(@PathVariable Integer number,@PathVariable Long id){
        return testService.updateNumberById(number,id);
    }
    
    @RequestMapping("insertNumber/{number}/{id}")
    public boolean insertNumber(@PathVariable Integer number,@PathVariable Long id){
        return testService.insertNumber(number,id);
    }

}
