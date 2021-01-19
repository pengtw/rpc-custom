package com.austin.rpc.common.service;

import com.austin.rpc.common.pojo.Demo;

public interface IDemoService {

    String testDemo(String name);

    String testDemoObject(Demo demo);

}
