package com.winky.srb.core.controller.api;

import com.winky.common.result.R;
import com.winky.srb.core.pojo.entity.Dict;
import com.winky.srb.core.service.DictService;
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

/**
 * @auther Li Wenjie
 * @create 2022-03-01 10:59
 */
@RestController
@Api(tags = "数据字典")
@RequestMapping("/api/core/dict")
@Slf4j
public class DictController {
    @Autowired
    DictService dictService;

    @ApiOperation("根据dictCode获取下级节点")
    @GetMapping("/findByDictCode/{dictCode}")
    public R  findByDictCode( @ApiParam(value = "节点编码", required = true)
                                  @PathVariable String dictCode) {
        List<Dict> list = dictService.findByDictCode(dictCode);
        return R.ok().data("dictList", list);

    }
}
