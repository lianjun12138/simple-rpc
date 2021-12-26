package com.example.rpc.protocol;

import com.example.rpc.Constants;
import com.example.rpc.serialization.Serialization;
import com.example.rpc.serialization.SerializationFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

import static com.example.rpc.protocol.Message.Header;


/**
 * @author LAJ
 * @date 2021-12-25 14:34:35
 */
public class MessageCodec extends ByteToMessageCodec<Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        Header header = msg.getHeader();
        // 依次序列化消息头中的魔数、版本、附加信息以及消息ID
        out.writeShort(header.getMagic());
        out.writeByte(header.getVersion());
        out.writeByte(header.getExtraInfo());
        out.writeLong(header.getMessageId());
        Object content = msg.getContent();
        // 心跳消息，没有消息体，这里写入0
        if (Constants.isHeartBeat(header.getExtraInfo())) {
            out.writeInt(0);
        } else {
            Serialization serialization = SerializationFactory.get(header.getExtraInfo());
            byte[] payload = serialization.serialize(content);
            // 写入消息体长度
            out.writeInt(payload.length);
            // 写入消息体
            out.writeBytes(payload);
        }
        
    }
    
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 不到16字节无法解析消息头, 暂不读取
        if (in.readableBytes() < Constants.HEADER_SIZE) {
            return;
        }
        // 记录当前readIndex指针的位置, 方便重置
        in.markReaderIndex();
        // 尝试读取消息头的魔数部分
        short magic = in.readShort();
        if (magic != Constants.MAGIC) {
            in.resetReaderIndex();
            throw new RuntimeException("Magic number error:" + magic);
        }
        // 依次读取消息其他部分
        byte version = in.readByte();
        byte extraInfo = in.readByte();
        long messageId = in.readLong();
        int size = in.readInt();
        Object body = null;
        // 心跳消息是没有消息体的，无需读取
        if (!Constants.isHeartBeat(extraInfo)) {
            if (in.readableBytes() < size) {
                in.resetReaderIndex();
                return;
            }
            // 读取消息体并进行反序列化
            byte[] payload = new byte[size];
            in.readBytes(payload);
            // 这里根据消息头中的extraInfo部分选择相应的序列化和压缩方式
            Serialization serialization = SerializationFactory.get(extraInfo);
            if (Constants.isRequest(extraInfo)) {
                // 得到消息体
                body = serialization.deserialize(payload, Request.class);
            } else {
                // 得到消息体
                body = serialization.deserialize(payload,
                        Response.class);
            }
        }
        // 将上面读取到的消息头和消息体拼装成完整的Message并向后传递
        Header header = new Header(magic, version, extraInfo, messageId, size);
        Message message = new Message(header, body);
        out.add(message);
    }
}
