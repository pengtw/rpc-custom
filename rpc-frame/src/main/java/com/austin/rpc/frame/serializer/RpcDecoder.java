package com.austin.rpc.frame.serializer;

import com.austin.rpc.frame.request.RpcRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @Class: RpcDecoder
 * @Description: TODO
 * @Author: Austin peng
 * @Create: 2021/1/18 14:15
 **/
public class RpcDecoder extends ByteToMessageDecoder {
    // 消息头：发送端写的是一个int，占用4字节。
    private final static int HEAD_LENGTH = 4;

    private Class<?> clazz;

    private Serializer serializer;

    public RpcDecoder(Class<?> clazz, Serializer serializer) {
        this.clazz = clazz;
        this.serializer = serializer;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() < HEAD_LENGTH) {
            return;
        }

        // 标记一下当前的readIndex的位置
        byteBuf.markReaderIndex();

        // 读取数据长度
        int dataLength = byteBuf.readInt();

        //读到的消息体长度如果小于我们传送过来的消息长度，则resetReaderIndex.
        // 这个配合markReaderIndex使用的。
        // 把readIndex重置到mark的地方
        if (byteBuf.readableBytes() < dataLength) {
            byteBuf.resetReaderIndex();
            return;
        }

        byte[] req = new byte[dataLength];
        byteBuf.readBytes(req);
        Object object = serializer.deserialize(clazz,req);
        list.add(object);
    }
}
