package com.winky.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.winky.srb.core.mapper.UserInfoMapper;
import com.winky.srb.core.pojo.BO.TransFlowBO;
import com.winky.srb.core.pojo.entity.TransFlow;
import com.winky.srb.core.mapper.TransFlowMapper;
import com.winky.srb.core.pojo.entity.UserInfo;
import com.winky.srb.core.service.TransFlowService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 交易流水表 服务实现类
 * </p>
 *
 * @author Winky
 * @since 2022-02-20
 */
@Service
public class TransFlowServiceImpl extends ServiceImpl<TransFlowMapper, TransFlow> implements TransFlowService {

    @Autowired
    UserInfoMapper userInfoMapper;
    @Override
    public void saveTransFlow(TransFlowBO transFlowBO) {
        String bindCode = transFlowBO.getBindCode();
        QueryWrapper<UserInfo> qw = new QueryWrapper<>();
        qw.eq("bind_code",bindCode);
        UserInfo info = userInfoMapper.selectOne(qw);


        TransFlow transFlow = new TransFlow();
        transFlow.setTransAmount(transFlowBO.getAmount());
        transFlow.setMemo(transFlowBO.getMemo());
        transFlow.setTransTypeName(transFlowBO.getTransTypeEnum().getTransTypeName());
        transFlow.setTransType(transFlowBO.getTransTypeEnum().getTransType());
        transFlow.setTransNo(transFlowBO.getAgentBillNo());

        transFlow.setUserId(info.getId());
        transFlow.setUserName(info.getName());
        baseMapper.insert(transFlow);
    }

    @Override
    public boolean isSaveTransFlow(String agentBillNo) {
        QueryWrapper<TransFlow> qw = new QueryWrapper<>();
        qw.eq("trans_no",agentBillNo);
        Integer integer = baseMapper.selectCount(qw);
        return integer > 0;


    }
}
