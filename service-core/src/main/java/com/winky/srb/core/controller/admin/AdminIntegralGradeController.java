package com.winky.srb.core.controller.admin;

import com.winky.common.exception.BusinessException;
import com.winky.common.result.R;
import com.winky.common.result.ResponseEnum;
import com.winky.srb.core.pojo.entity.IntegralGrade;
import com.winky.srb.core.service.IntegralGradeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @auther Li Wenjie
 * @create 2022-02-20 19:12
 */
@Api(tags = "积分等级管理")
//@CrossOrigin
@RestController
@RequestMapping("/admin/core/integralGrade")
public class AdminIntegralGradeController {

    @Autowired
    private IntegralGradeService integralGradeService;

    @ApiOperation(value = "积分等级列表")
    @GetMapping("/list")
    public R listAll(){
        List<IntegralGrade> list = integralGradeService.list();
        return R.ok().data("list",list).message("获取列表成功");
    }

    @ApiOperation(value = "积分根据ID删除数据记录",notes = "逻辑删除记录")
    @DeleteMapping("/remove/{id}")
    public R removedById(@PathVariable("id") Long id){
        boolean b = integralGradeService.removeById(id);
        if(b){
            return R.ok().message("删除成功");
        }
        return R.error().message("删除失败");
    }

    @ApiOperation("新增积分等级")
    @PostMapping("/save")
    public R save(@ApiParam(value = "积分等级对象", required = true)
                      @RequestBody IntegralGrade integralGrade){
        if(integralGrade.getBorrowAmount() == null){
            throw new BusinessException(ResponseEnum.BORROW_AMOUNT_NULL_ERROR);
        }
        boolean result = integralGradeService.save(integralGrade);
        if(result){
            return R.ok().message("保存成功");
        }
        return R.error().message("保存失败");
    }


    @ApiOperation("根据id获取积分等级")
    @GetMapping("/get/{id}")
    public R getById(@ApiParam(value = "数据id", required = true, example = "1")
                        @PathVariable("id") Long id){
        IntegralGrade byId = integralGradeService.getById(id);
        if(byId != null){
            return R.ok().data("record",byId);
        }
        return R.error().message("数据不存在");
    }

    @ApiOperation("更新积分等级")
    @PutMapping("/update")
    public R updateById(@ApiParam(value = "积分等级对象", required = true)
        @RequestBody IntegralGrade integralGrade){
        boolean result = integralGradeService.updateById(integralGrade);
        if(result){
            return R.ok().message("修改成功");
        }else{
            return R.error().message("修改失败");
        }
    }
//
//    @ApiOperation("异常")
//    @PutMapping("/error")
//    public R errorThrow(){
//        int i = 10/0;
//        return R.ok();
//    }


}
