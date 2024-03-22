/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.cloud.nacos.ribbon;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractServerList;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: 极度真实还原大麦网高并发实战项目。 添加 阿宽不是程序员 微信，添加时备注 damai 来获取项目的完整资料 
 * @description: 获取服务列表
 * @author: 阿宽不是程序员
 **/
public class NacosServerList extends AbstractServerList<NacosServer> {
    
    private NacosDiscoveryProperties discoveryProperties;
    
    private String serviceId;
    
    @Autowired
    private NacosServiceManager nacosServiceManager;
    
    public NacosServerList(NacosDiscoveryProperties discoveryProperties) {
        this.discoveryProperties = discoveryProperties;
    }
    
    @Override
    public List<NacosServer> getInitialListOfServers() {
        return getServers();
    }
    
    @Override
    public List<NacosServer> getUpdatedListOfServers() {
        return getServers();
    }
    
    private List<NacosServer> getServers() {
        try {
            String group = discoveryProperties.getGroup();
            List<Instance> instances = nacosServiceManager.getNamingService(discoveryProperties.getNacosProperties())
                    .selectInstances(serviceId, group, true,false);
            return instancesToServerList(instances);
        }
        catch (Exception e) {
            throw new IllegalStateException(
                    "Can not get service instances from nacos, serviceId=" + serviceId,
                    e);
        }
    }
    
    private List<NacosServer> instancesToServerList(List<Instance> instances) {
        List<NacosServer> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(instances)) {
            return result;
        }
        for (Instance instance : instances) {
            result.add(new NacosServer(instance));
        }
        
        return result;
    }
    
    public String getServiceId() {
        return serviceId;
    }
    
    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {
        this.serviceId = iClientConfig.getClientName();
    }
    
}
