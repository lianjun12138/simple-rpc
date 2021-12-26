package com.example.rpc.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * @author LAJ
 * @date 2021-12-25 14:07:32
 */
@Data
public class Response implements Serializable {
    /**
     * 响应错误码
     */
    private int code = 0;
    /**
     * 异常信息
     */
    private String errMsg;
    /**
     * 响应结果
     */
    private Object result;
}
