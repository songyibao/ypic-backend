package com.syb.ypic;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.syb.ypic.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class YpicApplication {

    public static void main(String[] args) {
        SpringApplication.run(YpicApplication.class, args);
    }

}
