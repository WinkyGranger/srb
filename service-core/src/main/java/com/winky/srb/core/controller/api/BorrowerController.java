package com.winky.srb.core.controller.api;


import com.winky.common.result.R;
import com.winky.srb.base.util.JwtUtils;
import com.winky.srb.core.pojo.vo.BorrowerVO;
import com.winky.srb.core.service.BorrowerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 借款人 前端控制器
 * </p>
 *
 * @author Winky
 * @since 2022-02-20
 */
@RestController
@RequestMapping("/api/core/borrower")
@Slf4j
@Api(tags = "借款人信息保存")
public class BorrowerController {
    @Autowired
    private BorrowerService borrowerService;

    @PostMapping("/auth/save")
    @ApiOperation("保存借款人信息")
    public R save(@RequestBody BorrowerVO borrowerVO, HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        borrowerService.saveBorrowerVOByUserId(borrowerVO,userId);
        return R.ok().message("信息提交成功");
    }

    @GetMapping("/auth/getBorrowerStatus")
    @ApiOperation("获取借款人认证状态")
    public R getBorrowerStatus(HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        Integer status = borrowerService.getStatusByUserId(userId);
        return R.ok().message("获取借款人认证状态获取成功").data("borrowerStatus",status);

    }


}

