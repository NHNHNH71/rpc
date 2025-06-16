package com.gcd.rpc.dto;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RpcReq implements Serializable {
    private static final long serialVersionUID = 1L;

    private String reqId;
    private String interfaceName;
    private String methodName;
    private Object[] params;
    private Class<?>[] paramTypes;
    /*
    version用于区别对于一个接口的多个版本实现
    * UserService -> UserServiceImpl1.getUser()
    *             -> UserServiceImpl2.getUser()
    * */
    private String version;
    /*
    * 一个接口可能有多个不同类型的实现，用group区分
    * UserService-> CommonUserServiceImpl.getUser()
    *            -> AdminUserServiceImpl.getUser()
    * */
    private String group;
    //最终通过version和group得到唯一的一个实现
    public String rpcServiceName(){
        return getInterfaceName()
                +"-" + StrUtil.blankToDefault(getGroup(),StrUtil.EMPTY)
                +"-" + StrUtil.blankToDefault(getVersion(),StrUtil.EMPTY);
    }
}
