package com.austin.rpc.frame.server;

import com.austin.rpc.frame.request.RpcRequest;
import com.austin.rpc.frame.serializer.JSONSerializer;
import com.austin.rpc.frame.serializer.RpcDecoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @Class: RPCServer
 * @Description: TODO
 * @Author: Austin peng
 * @Create: 2021/1/18 11:33
 **/
public class RPCServer {

    private String ip;
    private int port;

    public RPCServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void start(ServiceHandler serviceHandler) {
        NioEventLoopGroup boosGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boosGroup,workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        ChannelPipeline pipeline = nioSocketChannel.pipeline();
                        pipeline.addLast(new StringEncoder());
                        //设置json解码器
                        pipeline.addLast( new RpcDecoder(RpcRequest.class, new JSONSerializer()));

                        pipeline.addLast(serviceHandler);
                    }
                });
        try {
            bootstrap.bind(ip, port).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
