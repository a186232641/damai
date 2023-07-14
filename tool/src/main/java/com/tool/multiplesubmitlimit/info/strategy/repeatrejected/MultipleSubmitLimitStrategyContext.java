package com.tool.multiplesubmitlimit.info.strategy.repeatrejected;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: redis-tool
 * @description: 防重复提交触发时策略上下文
 * @author: kuan
 * @create: 2023-05-28
 **/
public class MultipleSubmitLimitStrategyContext {

    private static ConcurrentHashMap<String, MultipleSubmitLimitHandler> map = new ConcurrentHashMap<>();

    public static void put(String key, MultipleSubmitLimitHandler value){
        map.put(key,value);
    }

    public static MultipleSubmitLimitHandler get(String key){
        return map.get(key);
    }
}
