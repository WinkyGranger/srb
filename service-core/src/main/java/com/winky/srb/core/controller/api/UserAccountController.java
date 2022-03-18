package com.winky.srb.core.controller.api;


import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.client.naming.utils.SignUtil;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import com.winky.common.result.R;
import com.winky.srb.base.util.JwtUtils;
import com.winky.srb.core.hfb.RequestHelper;
import com.winky.srb.core.service.TransFlowService;
import com.winky.srb.core.service.UserAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 用户账户 前端控制器
 * </p>
 *
 * @author Winky
 * @since 2022-02-20
 */

@Api(tags = "会员账户")
@RestController
@RequestMapping("/api/core/userAccount")
@Slf4j
public class UserAccountController {
    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private TransFlowService transFlowService;

    @ApiOperation("充值")
    @PostMapping("/auth/commitCharge/{chargeAmt}")
    public R commitCharge(
            @ApiParam(value = "充值金额", required = true)
            @PathVariable BigDecimal chargeAmt, HttpServletRequest request) {

        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        //组装表单字符串用于远程提交数据
        String formStr = userAccountService.commitCharge(chargeAmt, userId);
        return R.ok().data("formStr", formStr);
    }

    @ApiOperation(value = "用户充值异步回调")
    @PostMapping("/notify")
    public String notify(HttpServletRequest request) {
        Map<String, Object> paramMap = RequestHelper.switchMap(request.getParameterMap());
        log.info("用户充值异步回调：" + JSON.toJSONString(paramMap));

        //验证签名
        if (RequestHelper.isSignEquals(paramMap)) {

            //判断账户是否充值成功
            if("0001".equals(paramMap.get("resultCode"))){
                //同步账户数据
                return userAccountService.notify(paramMap);

            }else{
                //这里的success是指不需要在重新让汇付宝发送失败重试了，因为验签已经失败了，不需要重试
                return "success";
            }
        }else{
            //这里需要重试的原因是验签失败了，可能服务器有问题，需要多次验证
            return "fail";
        }

    }
}



