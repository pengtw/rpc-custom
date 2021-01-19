package com.austin.rpc.consumer;

import com.austin.rpc.common.pojo.Demo;
import com.austin.rpc.common.service.IDemoService;
import com.austin.rpc.frame.annotation.SpringBootApplication;
import com.austin.rpc.frame.client.RPCClient;
import com.austin.rpc.frame.enums.BootstrapType;
import com.austin.rpc.frame.ioc.ApplicationContext;
import com.austin.rpc.frame.ioc.SpringApplication;

import java.util.Date;

@SpringBootApplication(bootstrapType = BootstrapType.CLIENT,ip = "127.0.0.1",port = 8888)
public class ClientBoot {

    public static void main(String[] args) throws InterruptedException {
        ApplicationContext context = SpringApplication.run(ClientBoot.class, args);
        IDemoService demoService = context.getService(IDemoService.class);
//        while (true){
//            String result = demoService.testDemo(new Date() + "呵呵呵");
//            System.out.println(result);
//            Thread.sleep(1000);
//        }

        while (true){
            Demo demo = new Demo();
            demo.setName("张三");
            demo.setAge(20);
            demo.setNow(new Date());
            String result = demoService.testDemoObject(demo);
            System.out.println(result);
            Thread.sleep(1000);
        }
    }
}
