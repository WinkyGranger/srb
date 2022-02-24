package com.winky.srb.core.mapper;

import com.winky.srb.core.pojo.dto.ExcelDictDTO;
import com.winky.srb.core.pojo.entity.Dict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 数据字典 Mapper 接口
 * </p>
 *
 * @author Winky
 * @since 2022-02-20
 */
public interface DictMapper extends BaseMapper<Dict> {

    void insertBatch(List<ExcelDictDTO> list);

}
