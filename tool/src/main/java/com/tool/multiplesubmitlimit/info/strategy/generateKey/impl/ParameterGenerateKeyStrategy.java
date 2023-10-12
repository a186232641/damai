package com.tool.multiplesubmitlimit.info.strategy.generateKey.impl;

import com.tool.core.Constants;
import com.example.core.StringUtil;
import com.tool.multiplesubmitlimit.info.GenerateKeyStrategy;
import com.tool.multiplesubmitlimit.info.MultipleSubmitLimitInfoProvider;
import com.tool.multiplesubmitlimit.info.strategy.generateKey.GenerateKeyHandler;
import com.tool.multiplesubmitlimit.info.strategy.generateKey.GenerateKeyStrategyContext;
import lombok.AllArgsConstructor;
import org.aspectj.lang.JoinPoint;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.StringJoiner;

import static com.tool.core.Constants.SEPARATOR;

/**
 * @program: redis-tool
 * @description: 生成键通用策略 REPEAT_LIMIT(标识)+类名+方法名+参数名+userId
 * @author: 星哥
 * @create: 2023-05-28
 **/
@AllArgsConstructor
public class ParameterGenerateKeyStrategy implements GenerateKeyHandler {
    
    private final MultipleSubmitLimitInfoProvider multipleSubmitLimitInfoProvider;

    @PostConstruct
    public void init(){
        GenerateKeyStrategyContext.put(GenerateKeyStrategy.PARAMETER_GENERATE_KEY_STRATEGY.getMsg(),this);
    }
    
    @Override
    public String generateKey(JoinPoint joinPoint) {
        HttpServletRequest request = multipleSubmitLimitInfoProvider.getRequest();
        String userId = request.getHeader(Constants.REPEAT_LIMIT_USERID);

        Object target = joinPoint.getTarget();
        Method method = multipleSubmitLimitInfoProvider.getMethod(joinPoint);

        String key = target.getClass().getName().concat(SEPARATOR).concat(method.getName());
        Object[] params = joinPoint.getArgs();
        if (params != null && params.length > 0) {
            StringJoiner joiner = new StringJoiner(",");
            for (Object param : params) {
                joiner.add(param.getClass().getName());
            }
            key = key.concat(SEPARATOR).concat(joiner.toString());
        }
        if (StringUtil.isNotEmpty(userId)) {
            key = key.concat(SEPARATOR).concat(userId);
        }
        return key;
    }
}
