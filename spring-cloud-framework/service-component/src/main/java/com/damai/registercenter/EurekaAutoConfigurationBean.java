package com.damai.registercenter;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: 极度真实还原大麦网高并发实战项目。 添加 阿宽不是程序员 微信，添加时备注 damai 来获取项目的完整资料 
 * @description: eureka排除
 * @author: 阿宽不是程序员
 **/
public class EurekaAutoConfigurationBean {
    
    private static final String DiscoveryClientOptionalArgsConfiguration = "org.springframework.cloud.netflix.eureka.config.DiscoveryClientOptionalArgsConfiguration";
    
    private static final String EurekaClientAutoConfiguration = "org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration";
    
    private static final String RibbonEurekaAutoConfiguration = "org.springframework.cloud.netflix.ribbon.eureka.RibbonEurekaAutoConfiguration";
    
    private static final String EurekaDiscoveryClientConfiguration = "org.springframework.cloud.netflix.eureka.EurekaDiscoveryClientConfiguration";
    
    private static final String EurekaReactiveDiscoveryClientConfiguration = "org.springframework.cloud.netflix.eureka.reactive.EurekaReactiveDiscoveryClientConfiguration";
    
    private static final String LoadBalancerEurekaAutoConfiguration = "org.springframework.cloud.netflix.eureka.loadbalancer.LoadBalancerEurekaAutoConfiguration";
    
    private static final Map<String,String> map = new HashMap<>(10);
    
    static {
        map.put(DiscoveryClientOptionalArgsConfiguration,DiscoveryClientOptionalArgsConfiguration);
        map.put(EurekaClientAutoConfiguration,EurekaClientAutoConfiguration);
        map.put(RibbonEurekaAutoConfiguration,RibbonEurekaAutoConfiguration);
        map.put(EurekaDiscoveryClientConfiguration,EurekaDiscoveryClientConfiguration);
        map.put(EurekaReactiveDiscoveryClientConfiguration,EurekaReactiveDiscoveryClientConfiguration);
        map.put(LoadBalancerEurekaAutoConfiguration,LoadBalancerEurekaAutoConfiguration);
    }
    
    public static Map<String,String> autoConfigurationBeanNameMap() {
        return map;
    }
}
