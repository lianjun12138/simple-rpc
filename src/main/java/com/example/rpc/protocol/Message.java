package com.example.rpc.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author LAJ
 * @date 2021-12-25 13:43:46
 */
@Data
@AllArgsConstructor
public class Message<T> {
    
    private Header header;
    
    private T content;
    
    public boolean isHeartBeat() {
        return (header.extraInfo & 32) != 0;
    }
    
    @Data
    public static class Header {
        /**
         * 魔数
         */
        private short magic;
        /**
         * 版本号
         */
        private byte version;
        /**
         * 附加信息
         */
        private byte extraInfo;
        /**
         * 消息ID
         */
        private Long messageId;
        /**
         * 消息体长度
         */
        private Integer size;
        
        public Header(short magic, byte version) {
            this.magic = magic;
            this.version = version;
        }
        
        public Header(short magic, byte version, byte extraInfo, Long messageId, Integer size) {
            this.magic = magic;
            this.version = version;
            this.extraInfo = extraInfo;
            this.messageId = messageId;
            this.size = size;
        }
    }
    
}
