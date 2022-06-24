package com.winky.srb.sms.client.fallback;

import com.winky.srb.sms.client.CoreUserInfoClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @auther Li Wenjie
 * @create 2022-02-27 14:47
 */


@Slf4j
@Service
public class CoreUserInfoClientFallback implements CoreUserInfoClient {

    //服务器宕机的时候，服务器不进行熔断，直接先发送验证码
    @Override
    public boolean checkMobile(String mobile) {
        log.error("远程调用失败，服务熔断");
        return false; //手机号不重复，可以进行注册
    }
}
