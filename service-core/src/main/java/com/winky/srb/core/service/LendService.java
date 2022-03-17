package com.winky.srb.core.service;

import com.winky.srb.core.pojo.entity.BorrowInfo;
import com.winky.srb.core.pojo.entity.Lend;
import com.baomidou.mybatisplus.extension.service.IService;
import com.winky.srb.core.pojo.vo.BorrowInfoApprovalVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的准备表 服务类
 * </p>
 *
 * @author Winky
 * @since 2022-02-20
 */
public interface LendService extends IService<Lend> {

    void createLend(BorrowInfoApprovalVO borrowInfoApprovalVO, BorrowInfo borrowInfo);

    List<Lend> selectList();

    Map<String, Object> getLendDetail(Long id);
}
