package com.winky.srb.core.controller.admin;


import com.winky.common.result.R;
import com.winky.srb.core.pojo.entity.Lend;
import com.winky.srb.core.service.LendService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的准备表 前端控制器
 * </p>
 *
 * @author Winky
 * @since 2022-02-20
 */
@Slf4j
@Api(tags = "标的管理")
@RestController
@RequestMapping("/admin/core/lend")
public class AdminLendController {
    @Autowired
    private LendService lendService;

    @ApiOperation(value = "标的列表")
    @GetMapping("/list")
    public R list(){
        List<Lend> list = lendService.selectList();
        return R.ok().data("list",list);
    }

    @ApiOperation("获取标的信息")
    @GetMapping("/show/{id}")
    public R show(
            @ApiParam(value = "标的id", required = true)
            @PathVariable Long id) {
        Map<String, Object> result = lendService.getLendDetail(id);
        return R.ok().data("lendDetail", result);
    }




}

