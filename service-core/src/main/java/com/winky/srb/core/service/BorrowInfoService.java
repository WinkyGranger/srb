package com.winky.srb.core.service;

import com.winky.srb.core.pojo.entity.BorrowInfo;
import com.baomidou.mybatisplus.extension.service.IService;

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
}
