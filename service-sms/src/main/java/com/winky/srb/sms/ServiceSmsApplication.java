package com.winky.srb.sms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @auther Li Wenjie
 * @create 2022-02-22 17:05
 */
@SpringBootApplication
@ComponentScan({"com.winky.srb","com.winky.common"})
public class ServiceSmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceSmsApplication.class,args);
    }
}
