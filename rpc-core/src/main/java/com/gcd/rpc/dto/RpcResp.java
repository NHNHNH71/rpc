package com.gcd.rpc.dto;

import com.gcd.rpc.enums.RpcRespStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RpcResp<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private String reqId;
    private Integer code;
    private String msg;
    private T data;

    public static <T> RpcResp<T> success(String reqId,T data) {
        RpcResp<T> resp=new RpcResp<T>();
        resp.setData(data);
        resp.setCode(200);
        resp.setReqId(reqId);
        resp.setMsg("success");
        return resp;
    }
    public static <T> RpcResp<T> fail(String reqId, RpcRespStatus status) {
        RpcResp<T> resp=new RpcResp<T>();
        resp.setReqId(reqId);
        resp.setCode(status.getCode());
        resp.setMsg(status.getMsg());

        return resp;
    }
}
