package com.winky.srb.core.controller.admin;



import com.winky.common.result.R;
import com.winky.srb.core.pojo.query.UserInfoQuery;
import com.winky.srb.core.service.UserInfoService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 * 用户基本信息 前端控制器
 * </p>
 *
 * @author Winky
 * @since 2022-02-20
 */
@RestController
@RequestMapping("/admin/core/userInfo")
@CrossOrigin
@Api(tags = "会员管理接口")
public class AdminUserInfoController {

    @Autowired
    UserInfoService userInfoService;

    @GetMapping("/list/{page}/{limit}")
    public R listPage(@RequestBody UserInfoQuery query){

        return R.ok();

    }

}

