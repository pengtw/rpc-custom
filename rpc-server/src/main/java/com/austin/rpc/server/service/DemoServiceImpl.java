package com.austin.rpc.server.service;

import com.austin.rpc.common.pojo.Demo;
import com.austin.rpc.common.service.IDemoService;
import com.austin.rpc.frame.annotation.Service;

@Service
public class DemoServiceImpl implements IDemoService {

    @Override
    public String testDemo(String name) {
        System.out.println("接受到参数："+name);
        return "success";
    }

    @Override
    public String testDemoObject(Demo demo) {
        System.out.println("接受到参数："+ demo);
        return "success";
    }
}
