package com.winky.srb.core.controller.admin;



import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.winky.common.result.R;
import com.winky.srb.core.pojo.entity.UserInfo;
import com.winky.srb.core.pojo.query.UserInfoQuery;
import com.winky.srb.core.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

    @ApiOperation("获取会员分页列表")
    @GetMapping("/list/{page}/{limit}")
    public R listPage(@ApiParam(value = "查询对象", required = false) UserInfoQuery query,
                      @ApiParam(value = "当前页码")
                      @PathVariable("page") Integer page,
                      @ApiParam("每页记录数")
                      @PathVariable("limit") Integer limit){
        Page<UserInfo> userInfoPage = new Page<>(page,limit);
        IPage<UserInfo> iPage = userInfoService.listPage(userInfoPage,query);
        return R.ok().message("列表获取成功").data("list",iPage);
    }

    @ApiOperation("锁定/解锁用户")
    @PutMapping("/lock/{id}/{status}")
    public R lock(@ApiParam(value = "用户id", required = true)
                  @PathVariable("id") Long id,
                  @ApiParam(value = "锁定状态（0：锁定 1：解锁）", required = true)
                  @PathVariable("status") Integer status){
        userInfoService.lock(id,status);
        return R.ok().message(status == 1 ? "解锁成功" : "锁定成功");
    }




}

