package com.austin.rpc.server;

import com.austin.rpc.frame.annotation.SpringBootApplication;
import com.austin.rpc.frame.ioc.SpringApplication;

@SpringBootApplication
public class ServerBoot {

    public static void main(String[] args) {
        SpringApplication.run(ServerBoot.class, args);
    }
}
