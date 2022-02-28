package com.winky.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.winky.common.exception.Assert;
import com.winky.common.result.ResponseEnum;
import com.winky.srb.core.enums.UserBindEnum;
import com.winky.srb.core.hfb.FormHelper;
import com.winky.srb.core.hfb.HfbConst;
import com.winky.srb.core.hfb.RequestHelper;
import com.winky.srb.core.pojo.entity.UserBind;
import com.winky.srb.core.mapper.UserBindMapper;
import com.winky.srb.core.pojo.vo.UserBindVo;
import com.winky.srb.core.service.UserBindService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户绑定表 服务实现类
 * </p>
 *
 * @author Winky
 * @since 2022-02-20
 */
@Service
public class UserBindServiceImpl extends ServiceImpl<UserBindMapper, UserBind> implements UserBindService {

    @Override
    public String commitBindUser(UserBindVo userBindVo, Long userId) {
        //查询身份证号码是否绑定
        String idCard = userBindVo.getIdCard();
        QueryWrapper qw = new QueryWrapper<>();
        qw.eq("id_card",idCard);
        UserBind userBind = baseMapper.selectOne(qw);

        //USER_BIND_IDCARD_EXIST_ERROR(-301, "身份证号码已绑定"),
        Assert.isNull(userBind, ResponseEnum.USER_BIND_IDCARD_EXIST_ERROR);

        //查询用户绑定信息
        qw = new QueryWrapper<>();
        qw.eq("user_id", userId);
        userBind = baseMapper.selectOne(qw);

        //判断是否有绑定记录
        if(userBind == null) {
            //如果未创建绑定记录，则创建一条记录
            userBind = new UserBind();
            BeanUtils.copyProperties(userBindVo, userBind);
            userBind.setUserId(userId);
            userBind.setStatus(UserBindEnum.NO_BIND.getStatus());
            baseMapper.insert(userBind);
        } else {
            //曾经跳转到托管平台，但是未操作完成，此时将用户最新填写的数据同步到userBind对象
            BeanUtils.copyProperties(userBindVo, userBind);
            baseMapper.updateById(userBind);
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("agentUserId", userId);
        paramMap.put("idCard",userBindVo.getIdCard());
        paramMap.put("personalName", userBindVo.getName());
        paramMap.put("bankType", userBindVo.getBankType());
        paramMap.put("bankNo", userBindVo.getBankNo());
        paramMap.put("mobile", userBindVo.getMobile());
        paramMap.put("returnUrl", HfbConst.USERBIND_RETURN_URL);
        paramMap.put("notifyUrl", HfbConst.USERBIND_NOTIFY_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        paramMap.put("sign", RequestHelper.getSign(paramMap));


        //构建充值自动提交表单
        String formStr = FormHelper.buildForm(HfbConst.USERBIND_URL, paramMap);
        return formStr;
    }
}
