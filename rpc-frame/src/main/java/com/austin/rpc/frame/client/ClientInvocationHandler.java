package com.austin.rpc.frame.client;

import com.austin.rpc.frame.request.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Class: ClientInvocationHandler
 * @Description: TODO
 * @Author: Austin peng
 * @Create: 2021/1/18 13:55
 **/
public class ClientInvocationHandler implements InvocationHandler {

    //线程池
    private ExecutorService executorService;
    //客户端Handler处理器
    private ClientHandler clientHandler;
    //接口名
    private String className;

    public ClientInvocationHandler(ExecutorService executorService, ClientHandler clientHandler,String className) {
        this.executorService = executorService;
        this.clientHandler = clientHandler;
        this.className = className;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //封装request
        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(className);
        request.setMethodName(method.getName());
        request.setParameters(args);
        //设置参数类型
        String[] parameterTypes = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            parameterTypes[i] = args[i].getClass().getName();
        }
        request.setParameterTypes(parameterTypes);

        clientHandler.setRequest(request);
        //使用线程池执行调用
        Object result = executorService.submit(clientHandler).get();
        return result;
    }
}
