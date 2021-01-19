package com.austin.rpc.frame.ioc;

import com.austin.rpc.frame.annotation.SpringBootApplication;
import com.austin.rpc.frame.client.RPCClient;
import com.austin.rpc.frame.enums.BootstrapType;
import com.austin.rpc.frame.server.RPCServer;
import com.austin.rpc.frame.server.ServiceHandler;

/**
 * @Class: SpringApplication
 * @Description: TODO
 * @Author: Austin peng
 * @Create: 2021/1/18 9:27
 **/
public class SpringApplication {

    public static ApplicationContext run(Class<?> primarySource,String... args){

        SpringBootApplication springBootApplication = primarySource.getAnnotation(SpringBootApplication.class);
        //启动类需要用SpringBootApplication注解标注
        if (springBootApplication == null){
            throw new IllegalArgumentException("启动类需要使用@SpringBootApplication注解标注！");
        }
        //初始化上下文
        ApplicationContext context = new ApplicationContext(primarySource);

        //刷新容器
        context.refresh();

        //启动netty服务
        if (springBootApplication.bootstrapType() == BootstrapType.SERVER){
            //服务端启动
            ServiceHandler serviceHandler = context.registerBean(ServiceHandler.class);
            RPCServer rpcServer = new RPCServer(springBootApplication.ip(),springBootApplication.port());
            rpcServer.start(serviceHandler);
            //给上下文设置服务端对象
            context.setRpcServer(rpcServer);
        }else if (springBootApplication.bootstrapType() == BootstrapType.CLIENT){
            //客户端启动
            RPCClient rpcClient = new RPCClient(springBootApplication.ip(),springBootApplication.port());
            rpcClient.start();
            //给上下文设置客户端对象
            context.setRpcClient(rpcClient);
        }

        return context;
    }
}
