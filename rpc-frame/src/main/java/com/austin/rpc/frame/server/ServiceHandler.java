package com.austin.rpc.frame.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.austin.rpc.frame.ioc.ApplicationContext;
import com.austin.rpc.frame.ioc.ApplicationContextAware;
import com.austin.rpc.frame.request.RpcRequest;
import com.austin.rpc.frame.serializer.JSONSerializer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Method;

/**
 * @Class: ServiceHandler
 * @Description: TODO
 * @Author: Austin peng
 * @Create: 2021/1/18 14:51
 **/
public class ServiceHandler extends ChannelInboundHandlerAdapter implements ApplicationContextAware {

    private ApplicationContext context;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcRequest request = (RpcRequest) msg;
        //从ioc容器中获取实例对象
        Object bean = context.getBean(request.getClassName());
        if (bean == null){
            ctx.writeAndFlush("Not find class");
        }
        //参数和参数类型需要序列化
        Class<?>[] parameterTypes = new Class[request.getParameterTypes().length];
        Object[] parameters = new Object[request.getParameterTypes().length];
        for (int i = 0; i < request.getParameterTypes().length; i++) {
            Class clazz = Class.forName(request.getParameterTypes()[i]);
            parameterTypes[i] = clazz;
            //如果参数是json对象，则还需要转换一次
            Object param = request.getParameters()[i];
            if(param instanceof JSON){
                param = JSON.parseObject(param.toString(),clazz);
            }
            parameters[i] = param;
        }
        //通过反射获取方法
        Method method= bean.getClass().getMethod(request.getMethodName(),parameterTypes);
        if (method == null){
            ctx.writeAndFlush("Not find method");
        }

        //通过反射调用方法
        Object result = method.invoke(bean,parameters);

        //返回客户端结果
        ctx.writeAndFlush(result);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }

    /**
     * 判断是否是基础数据类型，即 int,double,long等类似格式
     */
    public static boolean isCommonDataType(Class clazz){
        return clazz.isPrimitive();
    }

    /**
     * 判断是否是基础数据类型的包装类型
     *
     * @param clz
     * @return
     */
    public static boolean isWrapClass(Class clz) {
        try {
            return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }
}
