package com.tool.servicelock;

import com.example.core.StringUtil;
import com.tool.core.BaseInfoProvider;
import org.aspectj.lang.JoinPoint;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.tool.core.Constants.SEPARATOR;

/**
 * @program: cook-frame
 * @description: 锁业务名和标识进行组装并获取类
 * @author: 星哥
 * @create: 2023-05-28
 **/
public class ServiceLockInfoProvider extends BaseInfoProvider {

    private static final String LOCK_NAME_PREFIX = "LOCK";

    private static final String LOCK_DISTRIBUTE_ID_NAME_PREFIX = "LOCK_DISTRIBUTE_ID";

    public String getLockName(JoinPoint joinPoint,String name,String[] keys){
        return LOCK_NAME_PREFIX + SEPARATOR + name + getDefinitionKey(joinPoint, keys);
    }

    public String simpleGetLockName(String name,String[] keys){
        List<String> definitionKeyList = new ArrayList<>();
        for (String key : keys) {
            if (StringUtil.isNotEmpty(key)) {
                definitionKeyList.add(key);
            }
        }
        String definitionKeys = StringUtils.collectionToDelimitedString(definitionKeyList, "", SEPARATOR, "");
        return LOCK_DISTRIBUTE_ID_NAME_PREFIX + SEPARATOR + name + definitionKeys;
    }
}
