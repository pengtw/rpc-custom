package com.austin.rpc.frame.ioc;

import com.austin.rpc.frame.annotation.Service;
import com.austin.rpc.frame.client.ClientInvocationHandler;
import com.austin.rpc.frame.client.RPCClient;
import com.austin.rpc.frame.server.RPCServer;

import java.io.File;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * @Class: ApplicationContext
 * @Description: TODO
 * @Author: Austin peng
 * @Create: 2021/1/18 9:28
 **/
public class ApplicationContext {

    //启动类
    private Class<?> primarySources;

    //ioc容器，存放service
    private Map<String,Object> iocMap = new HashMap<>();

    private List<String> classNames = new ArrayList<>();

    //客户端对象
    private RPCClient rpcClient;

    //服务端对象
    private RPCServer rpcServer;

    public ApplicationContext(Class<?> primarySources) {
        this.primarySources = primarySources;
    }

    public void refresh() {
        //获取扫包路径
        String scanPackage = primarySources.getPackage().getName();
        //扫描包路径下的class
        doScan(scanPackage);
        //实例化Bean
        initBean();
    }

    //扫描包并注入容器中
    private void doScan(String scanPackage){
        String scanPackagePath = Thread.currentThread().getContextClassLoader().getResource("").getPath() + scanPackage.replaceAll("\\.", "/");
        File pack = new File(scanPackagePath);

        File[] files = pack.listFiles();

        for(File file: files) {
            if(file.isDirectory()) { // 子package
                // 递归
                doScan(scanPackage + "." + file.getName());
            }else if(file.getName().endsWith(".class")) {
                String className = scanPackage + "." + file.getName().replaceAll(".class", "");
                classNames.add(className);
            }
        }
    }

    //初始化启动时实例化bean
    private void initBean(){
        if (classNames.isEmpty()){
            return;
        }
        try {
            for (String className : classNames) {
                Class<?> aClass = Class.forName(className);
                if(aClass.isAnnotationPresent(Service.class)) {
                    doCreateBean(aClass);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //根据权限的类名实例化Bean
    private Object doCreateBean(Class<?> aClass) {
        try {
            Service annotation = aClass.getAnnotation(Service.class);
            //如果有Service注解标注
            String beanName = annotation!= null ? annotation.value() : "";
            Object bean = aClass.newInstance();
            //如果没有指定，就以类名首字母小写
            if (beanName.trim().isEmpty()){
                beanName = lowerFirst(aClass.getSimpleName());
            }

            //是否实现了ApplicationContextAware接口
            if (bean instanceof ApplicationContextAware){
                ((ApplicationContextAware)bean).setApplicationContext(this);
            }

            iocMap.put(beanName,bean);

            // service层往往是有接口的，面向接口开发，此时再以接口名为id，放入一份对象到ioc中，便于后期根据接口类型注入
            Class<?>[] interfaces = aClass.getInterfaces();
            for (int j = 0; j < interfaces.length; j++) {
                Class<?> anInterface = interfaces[j];
                // 以接口的全限定类名作为id放入
                iocMap.put(anInterface.getName(),bean);
            }

            return bean;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //主动向ioc容器注册bean
    public <T> T registerBean(Class<T> aClass){
        return (T) doCreateBean(aClass);
    }

    //根据名称获取bean实例
    public Object getBean(String beanName){
        return iocMap.get(beanName);
    }

    //根据class全限定类名获取bean实例
    public <T> T getBean(Class<T> aClass){
        return (T) iocMap.get(aClass.getName());
    }

    // 首字母小写
    public String lowerFirst(String str) {
        char[] chars = str.toCharArray();
        if('A' <= chars[0] && chars[0] <= 'Z') {
            chars[0] += 32;
        }
        return String.valueOf(chars);
    }

    /**
     * 客户端获取接口代理对象
     * @return
     */
    public <T> T getService(Class<T> tClass){
        ClientInvocationHandler handler = new ClientInvocationHandler(rpcClient.getExecutorService(),rpcClient.getClientHandler(),tClass.getName());
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{tClass}, handler);
    }

    public RPCClient getRpcClient() {
        return rpcClient;
    }

    public void setRpcClient(RPCClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    public RPCServer getRpcServer() {
        return rpcServer;
    }

    public void setRpcServer(RPCServer rpcServer) {
        this.rpcServer = rpcServer;
    }
}
