package com.winky.srb.core.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.winky.common.result.R;
import com.winky.srb.core.pojo.entity.Borrower;
import com.winky.srb.core.pojo.vo.BorrowerApprovalVO;
import com.winky.srb.core.pojo.vo.BorrowerDetailVO;
import com.winky.srb.core.service.BorrowerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @auther Li Wenjie
 * @create 2022-03-01 19:10
 */
@RestController
@RequestMapping("/admin/core/borrower")
@Slf4j
@Api(tags = "借款人信息")
public class AdminBorrowerController {
    @Autowired
    BorrowerService borrowerService;

    @ApiOperation("获取借款人分页列表")
    @GetMapping("/list/{page}/{limit}")
    public R listPage(  @ApiParam(value = "当前页码", required = true)
                        @PathVariable Long page,
                        @ApiParam(value = "每页记录数", required = true)
                        @PathVariable Long limit,
                        @ApiParam(value = "查询关键字", required = false)
                        @RequestParam String keyword){
        Page<Borrower> pageParam = new Page<>(page,limit);
        IPage<Borrower> pageModel = borrowerService.listPage(pageParam,keyword);
        return R.ok().message("获取借款人分页列表成功").data("pageModel",pageModel);
    }

    @ApiOperation("获取借款人信息")
    @GetMapping("/show/{id}")
    public R show(
            @ApiParam(value = "借款人id", required = true)
            @PathVariable Long id) {
        BorrowerDetailVO borrowerDetailVO = borrowerService.getBorrowerDetailVOById(id);
        return R.ok().data("borrowerDetailVO", borrowerDetailVO);
    }

    @ApiOperation("借款额度审批")
    @PostMapping("/approval")
    public R approval(@RequestBody BorrowerApprovalVO borrowerApprovalVO) {
        borrowerService.approval(borrowerApprovalVO);
        return R.ok().message("审批完成");
    }

}
