package com.example.service;

import com.example.core.RedisKeyEnum;
import com.example.redis.RedisCache;
import com.example.redis.RedisKeyWrap;
import com.example.structure.MethodData;
import com.example.structure.MethodDetailData;
import com.example.structure.MethodHierarchy;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ApiStatSystemService {

    @Autowired
    private RedisCache redisCache;

    public Set<MethodDetailData> getControllerMethods() {
        Set<ZSetOperations.TypedTuple<MethodDetailData>> typedTuples = redisCache.rangeWithScoreForZSet(RedisKeyWrap.createRedisKey(RedisKeyEnum.API_STAT_CONTROLLER_SORTED_SET), 1, 10, MethodDetailData.class);
        Set<MethodDetailData> set = typedTuples.stream().map(ZSetOperations.TypedTuple::getValue).collect(Collectors.toSet());
        return set;
    }

    public MethodDetailData getMethodChainList(String controllerMethod) {
        MethodDetailData controllerMethodDetailData = new MethodDetailData();
        MethodData controllerMethodData = redisCache.get(RedisKeyWrap.createRedisKey(RedisKeyEnum.API_STAT_CONTROLLER_METHOD_DATA, controllerMethod), MethodData.class);
        MethodHierarchy controllerMethodHierarchy = redisCache.get(RedisKeyWrap.createRedisKey(RedisKeyEnum.API_STAT_METHOD_HIERARCHY, controllerMethod), MethodHierarchy.class);
        if (controllerMethodData == null || controllerMethodHierarchy == null) {
            return controllerMethodDetailData;
        }
        BeanUtils.copyProperties(controllerMethodData,controllerMethodDetailData);
        BeanUtils.copyProperties(controllerMethodHierarchy,controllerMethodDetailData);
        Set<String> serviceMethodNameSet = redisCache.membersForSet(RedisKeyWrap.createRedisKey(RedisKeyEnum.API_STAT_CONTROLLER_CHILDREN_SET, controllerMethod), String.class);

        List<MethodDetailData> serviceList = new ArrayList<>();
        for (String serviceMethodName : serviceMethodNameSet) {
            MethodData serviceMethodData = redisCache.get(RedisKeyWrap.createRedisKey(RedisKeyEnum.API_STAT_SERVICE_METHOD_DATA, serviceMethodName), MethodData.class);
            MethodHierarchy serviceMethodHierarchy = redisCache.get(RedisKeyWrap.createRedisKey(RedisKeyEnum.API_STAT_METHOD_HIERARCHY, serviceMethodName), MethodHierarchy.class);
            if (serviceMethodData == null || serviceMethodHierarchy == null) {
                continue;
            }
            MethodDetailData serviceMethodDetailData = new MethodDetailData();
            BeanUtils.copyProperties(serviceMethodData,serviceMethodDetailData);
            BeanUtils.copyProperties(serviceMethodHierarchy,serviceMethodDetailData);

            Set<String> daoMethodNameSet = redisCache.membersForSet(RedisKeyWrap.createRedisKey(RedisKeyEnum.API_STAT_SERVICE_CHILDREN_SET, serviceMethodName), String.class);

            List<MethodDetailData> daoList = new ArrayList<>();
            for (String daoMethodName : daoMethodNameSet) {
                MethodData daoMethodData = redisCache.get(RedisKeyWrap.createRedisKey(RedisKeyEnum.API_STAT_DAO_METHOD_DATA, daoMethodName), MethodData.class);
                MethodHierarchy daoMethodHierarchy = redisCache.get(RedisKeyWrap.createRedisKey(RedisKeyEnum.API_STAT_METHOD_HIERARCHY, daoMethodName), MethodHierarchy.class);

                if (daoMethodData == null || daoMethodHierarchy == null) {
                    continue;
                }
                MethodDetailData daoMethodDetailData = new MethodDetailData();
                BeanUtils.copyProperties(daoMethodData,daoMethodDetailData);
                BeanUtils.copyProperties(daoMethodHierarchy,daoMethodDetailData);
                daoList.add(daoMethodDetailData);
            }
            serviceMethodDetailData.setChildrenMethodList(daoList);

            serviceList.add(serviceMethodDetailData);
        }
        controllerMethodDetailData.setChildrenMethodList(serviceList);
        return controllerMethodDetailData;
    }
}
