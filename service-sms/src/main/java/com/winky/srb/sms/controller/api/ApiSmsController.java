package com.winky.srb.sms.controller.api;

import com.winky.common.exception.Assert;
import com.winky.common.result.R;
import com.winky.common.result.ResponseEnum;
import com.winky.common.util.RandomUtils;
import com.winky.common.util.RegexValidateUtils;
import com.winky.srb.sms.service.SmsService;
import com.winky.srb.sms.util.SmsProperties;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @auther Li Wenjie
 * @create 2022-02-22 20:11
 */
@RestController
@RequestMapping("/api/sms")
@Api(tags = "短信管理")
@CrossOrigin
@Slf4j
public class ApiSmsController {
    @Autowired
    private SmsService smsService;
    @Autowired
    private RedisTemplate redisTemplate;

//    @GetMapping("/sendShortMessege/{mobile}")
//    @ApiOperation("获取验证码发送到手机")
//    public R sendShortMessege(@PathVariable("mobile")
//                  @ApiParam(value = "手机号",required = true) String mobile){
//        //校验手机号是否为空
//        Assert.notEmpty(mobile, ResponseEnum.MOBILE_NULL_ERROR);
//        //校验手机号和合法性
//        Assert.isTrue(RegexValidateUtils.checkCellphone(mobile),ResponseEnum.MOBILE_ERROR);
//        //发送短信
//        HashMap<String,Object > mobileMap = new HashMap<>();
//        String code = RandomUtils.getFourBitRandom();
//        mobileMap.put("code", code);
//        smsService.send(mobile, SmsProperties.TEMPLATE_CODE,mobileMap);
//        //将验证码存在Redis中
//        redisTemplate.opsForValue().set("srb:sms:map",code,3, TimeUnit.MINUTES);
//        return R.ok().message("短信发送成功");
//    }

    @GetMapping("/send/{mobile}")
    @ApiOperation("获取验证码，不发送至手机")
    public R send(@PathVariable("mobile")
                  @ApiParam(value = "手机号",required = true) String mobile){
        //校验手机号是否为空
        Assert.notEmpty(mobile, ResponseEnum.MOBILE_NULL_ERROR);
        //校验手机号和合法性
        Assert.isTrue(RegexValidateUtils.checkCellphone(mobile),ResponseEnum.MOBILE_ERROR);

        //判断是否已经注册，注册了就不许要发送验证码，不然浪费短信费用


        //发送短信
        HashMap<String,Object > mobileMap = new HashMap<>();
        String code = RandomUtils.getFourBitRandom();
        mobileMap.put("code", code);
        //将验证码存在Redis中
        redisTemplate.opsForValue().set(mobile + "verCode",code,3, TimeUnit.MINUTES);
        Object codeSout = mobileMap.get("code");
        System.out.println(codeSout);
        return R.ok().message("短信发送成功");
    }
}
