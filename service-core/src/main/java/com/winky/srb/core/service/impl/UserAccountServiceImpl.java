package com.winky.srb.core.service.impl;

import com.winky.srb.core.enums.TransTypeEnum;
import com.winky.srb.core.hfb.FormHelper;
import com.winky.srb.core.hfb.HfbConst;
import com.winky.srb.core.hfb.RequestHelper;
import com.winky.srb.core.mapper.UserInfoMapper;
import com.winky.srb.core.pojo.BO.TransFlowBO;
import com.winky.srb.core.pojo.entity.UserAccount;
import com.winky.srb.core.mapper.UserAccountMapper;
import com.winky.srb.core.pojo.entity.UserInfo;
import com.winky.srb.core.service.TransFlowService;
import com.winky.srb.core.service.UserAccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winky.srb.core.util.LendNoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户账户 服务实现类
 * </p>
 *
 * @author Winky
 * @since 2022-02-20
 */
@Service
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {

    @Autowired
    private TransFlowService transFlowService;

    @Resource
    private  UserInfoMapper userInfoMapper;

    @Override
    public String commitCharge(BigDecimal chargeAmt, Long userId) {
        UserInfo info = userInfoMapper.selectById(userId);
        String bindCode = info.getBindCode();

        //按照汇付宝的要求进行参数上传
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("agentId", HfbConst.AGENT_ID);
        hashMap.put("agentBillNo", LendNoUtils.getNo());
        hashMap.put("bindCode", bindCode);
        hashMap.put("chargeAmt", chargeAmt);
        hashMap.put("feeAmt", new BigDecimal("0"));
        hashMap.put("notifyUrl", HfbConst.RECHARGE_NOTIFY_URL);
        hashMap.put("returnUrl",  HfbConst.RECHARGE_RETURN_URL);
        hashMap.put("timestamp", RequestHelper.getTimestamp());
        String sign = RequestHelper.getSign(hashMap);
        hashMap.put("sign", sign);

        //构建充值自动提交表单
        String formStr = FormHelper.buildForm(HfbConst.RECHARGE_URL, hashMap);
        return formStr;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String notify(Map<String, Object> paramMap) {
        //幂等性判断?标准是什么？判断流水
        boolean agentBillNo = transFlowService.isSaveTransFlow((String) paramMap.get("agentBillNo"));
        if(agentBillNo){
            log.warn("幂等返回");
            return "success";

        }
        //账户处理
        String bindCode = (String)paramMap.get("bindCode");
        String chargeAmt = (String)paramMap.get("chargeAmt");
        baseMapper.updateAccount(bindCode,new BigDecimal(chargeAmt),new BigDecimal("0"));

        //记录流水
        TransFlowBO transFlowBO = new TransFlowBO(
                (String)paramMap.get("agentBillNo"),
                bindCode,
                new BigDecimal(chargeAmt),
                TransTypeEnum.RECHARGE,"充值啦");

        transFlowService.saveTransFlow(transFlowBO);

        return "success";
    }
}
