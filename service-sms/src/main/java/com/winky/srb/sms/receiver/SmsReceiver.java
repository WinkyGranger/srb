package com.winky.srb.sms.receiver;

import com.winky.rabbitutil.constant.MQConst;
import com.winky.srb.base.dto.SmsDTO;
import com.winky.srb.sms.service.SmsService;
import com.winky.srb.sms.util.SmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;

@Component
@Slf4j
public class SmsReceiver {
    @Resource
    private SmsService smsService;
    @Resource
    private AmqpTemplate amqpTemplate;

    //队列帮定器
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MQConst.QUEUE_SMS_ITEM,durable = "true"),  //队列
            exchange = @Exchange(value = MQConst.EXCHANGE_TOPIC_SMS, type = "topic"),  //交换机
            key =  {MQConst.ROUTING_SMS_ITEM} //路由
    ))
    public void send(SmsDTO smsDTO){
        log.info("SmsReceiver监听器 。。。。。接受消息");
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("code",smsDTO.getMessage());
        smsService.send(smsDTO.getMobile(), SmsProperties.TEMPLATE_CODE,stringObjectHashMap);
    }
}
