package com.winky.srb.oss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @auther Li Wenjie
 * @create 2022-02-23 10:21
 */
@SpringBootApplication
@ComponentScan({"com.winky.srb", "com.winky.common"})
public class ServiceOssApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceOssApplication.class, args);
    }

}
