package com.mdd.bootInit;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan(basePackages = "com.mdd.bootInit.mapper")
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class SpringBootProjectInitApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootProjectInitApplication.class, args);
    }

}
