package com.gcd.rpc.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nhnhnh7171
 * @Date 2025/6/27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcConfig {
    private String serializer="kryo";
    private String compress="gzip";


    private int SERVER_PORT=7777;
    private String ZK_IP="localhost";
    private int ZK_PORT=2181;
    private String ZK_RPC_ROOT_PATH="/felix-rpc";

    private String NETTY_RPC_KEY="RpcResp";

    private byte[] RPC_MAGIC_CODE=new byte[]{(byte) 'z',(byte)'r',(byte)'p',(byte)'c'};
}
