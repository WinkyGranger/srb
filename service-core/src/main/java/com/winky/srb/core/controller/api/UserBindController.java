package com.winky.srb.core.controller.api;


import com.alibaba.fastjson.JSON;
import com.winky.common.result.R;
import com.winky.srb.base.util.JwtUtils;
import com.winky.srb.core.hfb.RequestHelper;
import com.winky.srb.core.pojo.vo.UserBindVo;
import com.winky.srb.core.service.UserBindService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * 用户绑定表 前端控制器
 * </p>
 *
 * @author Winky
 * @since 2022-02-20
 */
@Api(tags = "会员支付账号绑定")
@Slf4j
@RestController
@RequestMapping("/api/core/userBind")
public class UserBindController {

    @Autowired
    UserBindService userBindService;

    @ApiOperation("账户绑定提交数据")
    @PostMapping("/auth/bind")
    public R bind(@RequestBody UserBindVo userBindVo, HttpServletRequest request){
        //从head中拿去token，并对token进行校验，确保用户已登陆
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        //根据userId绑定,生成一个动态表单字符串
        String formStr = userBindService.commitBindUser(userBindVo,userId);
        return R.ok().data("formStr",formStr);
    }

    @ApiOperation("账户绑定异步回调")
    @PostMapping("/notify")
    public String notify(HttpServletRequest request) {
        Map<String, Object> paramMap = RequestHelper.switchMap(request.getParameterMap());
        log.info("用户账号绑定异步回调：" + JSON.toJSONString(paramMap));

        //校验签名
        if(!RequestHelper.isSignEquals(paramMap)) {
            log.error("用户账号绑定异步回调签名错误：" + JSON.toJSONString(paramMap));
            return "fail";
        }
        //修改绑定状态
        userBindService.notify(paramMap);
        return "success";
    }

}

