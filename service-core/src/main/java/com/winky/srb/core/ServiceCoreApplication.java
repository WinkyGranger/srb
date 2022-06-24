package com.winky.srb.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @auther Li Wenjie
 * @create 2022-02-20 19:04
 */
@SpringBootApplication
@ComponentScan({"com.winky.srb","com.winky.common","com.winky.rabbitutil.config"})
public class ServiceCoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceCoreApplication.class, args);
    }
}
