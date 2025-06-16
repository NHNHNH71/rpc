package com.gcd.rpc.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author nhnhnh7171
 * @Date 2025/6/16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcServiceConfig {
    private String version="";
    private String group="";
    private Object service;
    public RpcServiceConfig(Object service){
        this.service=service;
    }
    public List<String> rpcServiceNames(){
        return interfaceNames().stream()
                .map(name->name+"-"+getGroup()+"-"+getVersion())
                .collect(Collectors.toList());
    }
    private List<String> interfaceNames(){
        return Arrays.stream(service.getClass().getInterfaces())
                .map(Class::getCanonicalName)
                .collect(Collectors.toList());
    }
}
