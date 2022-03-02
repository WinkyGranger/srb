package com.winky.srb.core.service;

import com.winky.srb.core.pojo.entity.BorrowInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 借款信息表 服务类
 * </p>
 *
 * @author Winky
 * @since 2022-02-20
 */
public interface BorrowInfoService extends IService<BorrowInfo> {

    Integer getStatusByUserId(Long userId);

    BigDecimal getBorrowAmount(Long userId);

    void saveBorrowInfo(BorrowInfo borrowInfo, Long userId);

    List<BorrowInfo> selectList();
}
