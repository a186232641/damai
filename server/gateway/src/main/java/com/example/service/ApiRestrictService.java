package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.baidu.fsg.uid.UidGenerator;
import com.example.core.RedisKeyEnum;
import com.example.core.StringUtil;
import com.example.dto.ApiDataDto;
import com.example.enums.BaseCode;
import com.example.enums.RuleTimeUnit;
import com.example.exception.ToolkitException;
import com.example.kafka.ApiDataMessageSend;
import com.example.property.GatewayProperty;
import com.example.redis.RedisCache;
import com.example.redis.RedisKeyWrap;
import com.example.util.DateUtils;
import com.example.vo.DepthRuleVo;
import com.example.vo.RuleVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @program: cook-frame
 * @description:
 * @author: 星哥
 * @create: 2023-06-03
 **/
@Slf4j
@Component
public class ApiRestrictService {
    
    @Autowired
    private RedisCache redisCache;
    
    @Autowired
    private GatewayProperty gatewayProperty;
    
    @Autowired(required = false)
    private ApiDataMessageSend apiDataMessageSend;
    
    @Resource
    private UidGenerator uidGenerator;
    
    private DefaultRedisScript redisScript;
    
    private ConcurrentHashMap<String,Long> zSetMap = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init(){
        try {
            redisScript = new DefaultRedisScript();
            redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/apiLimit.lua")));
            redisScript.setResultType(List.class);
        } catch (Exception e) {
            log.error("redisScript init lua error",e);
        }
    }
    
    public boolean checkApiRestrict(String requestURI){
        if (gatewayProperty.getApiRestrictPaths() != null && gatewayProperty.getApiRestrictPaths().length > 0) {
            for (String apiRestrictPath : gatewayProperty.getApiRestrictPaths()) {
                PathMatcher matcher = new AntPathMatcher();
                if(matcher.match(apiRestrictPath, requestURI)){
                    return true;
                }
            }
        }
        return false;
    }
    
    public void apiRestrict(String id, String url, ServerHttpRequest request) {
        if (checkApiRestrict(url)) {
            long triggerResult = 0L;
            long triggerCallStat = 0L;
            long apiCount = 0L;
            long threshold = 0L;
            long messageIndex = -1L;
            String message = "";
            
            String ip = getIpAddress(request);
            
            StringBuilder stringBuilder = new StringBuilder(ip);
            if (StringUtil.isNotEmpty(id)) {
                stringBuilder.append("_").append(id);
            }
            String commonkey = stringBuilder.append("_").append(url).toString();
            try {
                List<DepthRuleVo> depthRuleVoList = new ArrayList<>();
                //查询规则 Hash结构 
                //普通规则
                RuleVo ruleVo = redisCache.getForHash(RedisKeyWrap.createRedisKey(RedisKeyEnum.ALL_RULE_HASH),RedisKeyWrap.createRedisKey(RedisKeyEnum.RULE).getRelKey(),RuleVo.class);
                //深度规则
                String depthRuleStr = redisCache.getForHash(RedisKeyWrap.createRedisKey(RedisKeyEnum.ALL_RULE_HASH),RedisKeyWrap.createRedisKey(RedisKeyEnum.DEPTH_RULE).getRelKey(),String.class);
                if (StringUtil.isNotEmpty(depthRuleStr)) {
                    depthRuleVoList = JSON.parseArray(depthRuleStr,DepthRuleVo.class);
                }
                //规则类型
                int apiRuleType = 0;
                if (Optional.ofNullable(ruleVo).isPresent()) {
                    apiRuleType = 1;
                    message = ruleVo.getMessage();
                }
                if (Optional.ofNullable(ruleVo).isPresent() && depthRuleVoList.size() > 0) {
                    apiRuleType = 2;
                }
                if (apiRuleType == 1 || apiRuleType == 2) {
                    List<String> parameters = new ArrayList<>();
                    parameters.add(String.valueOf(apiRuleType));
                    StringBuffer stringBuffer = new StringBuffer("rule_api_limit");
                    String rulekey = stringBuffer.append("_").append(commonkey).toString();
                    //普通规则中要进行统计请求数
                    parameters.add(rulekey);
                    //普通规则中进行统计的时间
                    parameters.add(String.valueOf(ruleVo.getStatTimeType() == RuleTimeUnit.SECOND.getCode() ? ruleVo.getStatTime() : ruleVo.getStatTime() * 60));
                    //普通规则中进行统计的阈值
                    parameters.add(String.valueOf(ruleVo.getThreshold()));
                    //普通规则超过阈值后限制的时间
                    parameters.add(String.valueOf(ruleVo.getEffectiveTimeType() == RuleTimeUnit.SECOND.getCode() ? ruleVo.getEffectiveTime() : ruleVo.getEffectiveTime() * 60));
                    //实现普通规则执行限制
                    parameters.add(RedisKeyWrap.createRedisKey(RedisKeyEnum.RULE_LIMIT,commonkey).getRelKey());
                    //进行统计超过普通规则的数量sorted set结构
                    parameters.add(RedisKeyWrap.createRedisKey(RedisKeyEnum.Z_SET_RULE_STAT,commonkey).getRelKey());
                    
                    String[] timeParameters = {String.valueOf(System.currentTimeMillis())};
                    if (apiRuleType == 2) {
                        //将限制时间窗口进行排序
                        depthRuleVoList = sortStartTimeWindow(depthRuleVoList);
                        int timeIndex = 1;
                        //深度规则的个数
                        parameters.add(String.valueOf(depthRuleVoList.size()));
                        //深度规则的时间参数 
                        String[] relTimeParameters = new String[2 * depthRuleVoList.size() + 1];
                        //第一个元素为当前请求时间
                        relTimeParameters[0] = String.valueOf(System.currentTimeMillis());
                        for (int i = 0; i < depthRuleVoList.size(); i++) {
                            DepthRuleVo depthRuleVo = depthRuleVoList.get(i);
                            //深度规则中进行统计的时间
                            parameters.add(String.valueOf(depthRuleVo.getStatTimeType() == RuleTimeUnit.SECOND.getCode() ? depthRuleVo.getStatTime() : depthRuleVo.getStatTime() * 60));
                            //深度规则中进行统计的阈值
                            parameters.add(String.valueOf(depthRuleVo.getThreshold()));
                            //深度规则超过阈值后限制的时间
                            parameters.add(String.valueOf(depthRuleVo.getEffectiveTimeType() == RuleTimeUnit.SECOND.getCode() ? depthRuleVo.getEffectiveTime() : depthRuleVo.getEffectiveTime() * 60));
                            //实现深度规则执行限制
                            parameters.add(RedisKeyWrap.createRedisKey(RedisKeyEnum.DEPTH_RULE_LIMIT,i,commonkey).getRelKey());
                            //深度规则限制时间窗口
                            relTimeParameters[timeIndex] = String.valueOf(depthRuleVo.getStartTimeWindowTimestamp());
                            relTimeParameters[timeIndex+1] = String.valueOf(depthRuleVo.getEndTimeWindowTimestamp());
                            timeIndex = timeIndex + 2;
                        }
                        timeParameters = relTimeParameters;
                    }
                    List<Long> executeResult = (ArrayList)redisCache.getInstance().execute(redisScript, parameters, timeParameters);
                    //是否需要进行限制
                    triggerResult = Optional.ofNullable(executeResult.get(0)).orElse(0L);
                    //是否进行保存记录
                    triggerCallStat = Optional.ofNullable(executeResult.get(1)).orElse(0L);
                    //请求数
                    apiCount = Optional.ofNullable(executeResult.get(2)).orElse(0L);
                    //规则阈值
                    threshold = Optional.ofNullable(executeResult.get(3)).orElse(0L);
                    //定制的规则提示语
                    messageIndex = Optional.ofNullable(executeResult.get(4)).orElse(-1L);
                    if (messageIndex != -1) {
                        message = Optional.ofNullable(depthRuleVoList.get((int)messageIndex))
                                .map(DepthRuleVo::getMessage)
                                .filter(StringUtil::isNotEmpty)
                                .orElse(message);
                    }
                    log.info("api rule [key : {}], [triggerResult : {}], [triggerCallStat : {}], [apiCount : {}], [threshold : {}]",commonkey,triggerResult,triggerCallStat,apiCount,threshold);
                }
            }catch (Exception e) {
                log.error("redis Lua eror", e);
            }
            if (triggerResult == 1) {
                if (triggerCallStat == 1 || triggerCallStat == 2) {
                    saveApiData(request, url, (int)triggerCallStat);
                }
                String defaultMessage = BaseCode.API_RULE_TRIGGER.getMsg();
                if (StringUtil.isNotEmpty(message)) {
                    defaultMessage = message;
                }
                throw new ToolkitException(BaseCode.API_RULE_TRIGGER.getCode(),defaultMessage);
            }
        }
    }
    
    public List<DepthRuleVo> sortStartTimeWindow(List<DepthRuleVo> depthRuleVoList){
        return depthRuleVoList.stream().map(depthRuleVo -> {
            depthRuleVo.setStartTimeWindowTimestamp(getTimeWindowTimestamp(depthRuleVo.getStartTimeWindow()));
            depthRuleVo.setEndTimeWindowTimestamp((getTimeWindowTimestamp(depthRuleVo.getEndTimeWindow())));
            return depthRuleVo;
        }).sorted(Comparator.comparing(DepthRuleVo::getStartTimeWindowTimestamp)).collect(Collectors.toList());
    }
    
    public long getTimeWindowTimestamp(String timeWindow){
        String today = DateUtil.today();
        return DateUtil.parse(today + " " + timeWindow).getTime();
    }
    
    /**
      * 获取请求的归属IP地址
      *
      * @param request 请求
      */
    public static String getIpAddress(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        String ip = headers.getFirst("x-forwarded-for");
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            if (ip.indexOf(",") != -1) {
                ip = ip.split(",")[0];
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddress().getAddress().getHostAddress();
        }
        return ip;
    }
    
    public void saveApiData(ServerHttpRequest request, String apiUrl, Integer type){
        ApiDataDto apiDataDto = new ApiDataDto();
        apiDataDto.setId(String.valueOf(uidGenerator.getUID()));
        apiDataDto.setApiAddress(getIpAddress(request));
        apiDataDto.setApiUrl(apiUrl);
        apiDataDto.setCreateTime(DateUtils.now());
        apiDataDto.setCallDayTime(DateUtils.nowStr(DateUtils.FORMAT_DATE));
        apiDataDto.setCallHourTime(DateUtils.nowStr(DateUtils.FORMAT_HOUR));
        apiDataDto.setCallMinuteTime(DateUtils.nowStr(DateUtils.FORMAT_MINUTE));
        apiDataDto.setCallSecondTime(DateUtils.nowStr(DateUtils.FORMAT_SECOND));
        apiDataDto.setType(type);
        Optional.ofNullable(apiDataMessageSend).ifPresent(send -> send.sendMessage(JSON.toJSONString(apiDataDto)));
    }
}
