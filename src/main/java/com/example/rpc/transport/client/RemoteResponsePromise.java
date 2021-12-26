package com.example.rpc.transport.client;

import com.example.rpc.protocol.Request;
import com.example.rpc.protocol.Response;
import com.example.rpc.protocol.Message;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author LAJ
 * @date 2021-12-25 16:16:15
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RemoteResponsePromise extends DefaultPromise<Message<Response>> {

    private Message<Request> requestMessage;
    
    private long createTime;
    
    private long timeOut;
    
    public RemoteResponsePromise(Message<Request> requestMessage) {
        super(new DefaultEventLoop());
        this.requestMessage = requestMessage;
        this.createTime = System.currentTimeMillis();
    }
}
