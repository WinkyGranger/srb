package com.winky.srb.core.controller.api;


import com.winky.common.exception.Assert;
import com.winky.common.result.R;
import com.winky.common.result.ResponseEnum;
import com.winky.common.util.RegexValidateUtils;
import com.winky.srb.base.util.JwtUtils;
import com.winky.srb.core.pojo.vo.LoginVo;
import com.winky.srb.core.pojo.vo.RegisterVo;
import com.winky.srb.core.pojo.vo.UserInfoVo;
import com.winky.srb.core.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 用户基本信息 前端控制器
 * </p>
 *
 * @author Winky
 * @since 2022-02-20
 */
@RestController
@RequestMapping("/api/core/userInfo")
//@CrossOrigin
@Api(tags = "会员接口")
public class UserInfoController {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    UserInfoService userInfoService;

    @PostMapping("/register")
    @ApiOperation("会员注册")
    public R register(@RequestBody RegisterVo registerVo){
        String mobile = registerVo.getMobile();
        String password = registerVo.getPassword();
        String code = registerVo.getCode();

        //MOBILE_NULL_ERROR(-202, "手机号不能为空"),
        Assert.notEmpty(mobile, ResponseEnum.MOBILE_NULL_ERROR);
        //MOBILE_ERROR(-203, "手机号不正确"),
        Assert.isTrue(RegexValidateUtils.checkCellphone(mobile), ResponseEnum.MOBILE_ERROR);
        //PASSWORD_NULL_ERROR(-204, "密码不能为空"),
        Assert.notEmpty(password, ResponseEnum.PASSWORD_NULL_ERROR);
        //CODE_NULL_ERROR(-205, "验证码不能为空"),
        Assert.notEmpty(code, ResponseEnum.CODE_NULL_ERROR);
        //校验验证码
        String codeGen = (String)redisTemplate.opsForValue().get(mobile + "verCode");
        //CODE_ERROR(-206, "验证码不正确"),
        Assert.equals(code, codeGen, ResponseEnum.CODE_ERROR);
        //注册
        userInfoService.register(registerVo);
        return R.ok().message("注册成功");
    }

    @ApiOperation("会员登录")
    @PostMapping("/login")
    public R login(@RequestBody LoginVo loginVo, HttpServletRequest request){
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();

        Assert.notEmpty(mobile,ResponseEnum.MOBILE_NULL_ERROR);
        Assert.notEmpty(password,ResponseEnum.PASSWORD_NULL_ERROR);

        //拿到客户端的ip地址
        String ip = request.getRemoteAddr();
        UserInfoVo info = userInfoService.login(loginVo,ip);
        return R.ok().message("").data("userInfo",info);
    }

    @ApiOperation("校验令牌")
    @GetMapping("/checkToken")
    public R checkToken(HttpServletRequest request) {

        String token = request.getHeader("token");
        boolean result = JwtUtils.checkToken(token);

        if(result){
            return R.ok();
        }else{
            //LOGIN_AUTH_ERROR(-211, "未登录"),
            return R.setResult(ResponseEnum.LOGIN_AUTH_ERROR);
        }
    }



    @ApiOperation("校验手机号是否注册")
    @GetMapping("/checkMobile/{mobile}")
    public boolean checkMobile(@PathVariable String mobile){
        boolean b = userInfoService.checkMobile(mobile);
        return b;
    }

}

