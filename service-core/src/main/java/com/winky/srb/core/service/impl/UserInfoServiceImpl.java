package com.winky.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.winky.common.exception.Assert;
import com.winky.common.result.ResponseEnum;
import com.winky.common.util.MD5;
import com.winky.srb.core.mapper.UserAccountMapper;
import com.winky.srb.core.pojo.entity.UserAccount;
import com.winky.srb.core.pojo.entity.UserInfo;
import com.winky.srb.core.mapper.UserInfoMapper;
import com.winky.srb.core.pojo.vo.RegisterVo;
import com.winky.srb.core.service.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 用户基本信息 服务实现类
 * </p>
 *
 * @author Winky
 * @since 2022-02-20
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    private UserAccountMapper userAccountMapper;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void register(RegisterVo registerVo) {
        //判断是否已经被注册
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile",registerVo.getMobile());
        Integer integer = baseMapper.selectCount(wrapper);
        Assert.isTrue(integer == 0, ResponseEnum.MOBILE_EXIST_ERROR);
        //插入用户信息 user_info表
        UserInfo userInfo = new UserInfo();
        userInfo.setMobile(registerVo.getMobile());
        userInfo.setNickName(registerVo.getMobile());
        userInfo.setPassword(MD5.encrypt(registerVo.getPassword()));
        userInfo.setStatus(UserInfo.STATUS_NORMAL);//状态设置正常
        userInfo.setHeadImg(UserInfo.USER_AVATER);
        baseMapper.insert(userInfo);
        //插入用户账户记录 user_account表
        UserAccount userAccount = new UserAccount();
        userAccount.setUserId(userInfo.getId());
        userAccountMapper.insert(userAccount);

    }
}
