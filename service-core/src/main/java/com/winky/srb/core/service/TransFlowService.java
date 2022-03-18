package com.winky.srb.core.service;

import com.winky.srb.core.pojo.BO.TransFlowBO;
import com.winky.srb.core.pojo.entity.TransFlow;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 交易流水表 服务类
 * </p>
 *
 * @author Winky
 * @since 2022-02-20
 */
public interface TransFlowService extends IService<TransFlow> {
    void saveTransFlow(TransFlowBO transFlowBO);
    //判斷是否已經存在流水号， 解决幂等问题
    boolean isSaveTransFlow(String agentBillNo);

}
