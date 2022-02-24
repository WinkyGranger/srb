package com.winky.srb.sms.service;

import java.util.Map;

/**
 * @auther Li Wenjie
 * @create 2022-02-22 19:57
 */
public interface SmsService {
    void send(String mobile, String templateCode, Map<String,Object> param);
}
