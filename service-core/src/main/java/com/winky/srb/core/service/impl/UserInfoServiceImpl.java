package com.winky.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.winky.common.exception.Assert;
import com.winky.common.result.ResponseEnum;
import com.winky.common.util.MD5;
import com.winky.srb.base.util.JwtUtils;
import com.winky.srb.core.mapper.UserAccountMapper;
import com.winky.srb.core.mapper.UserLoginRecordMapper;
import com.winky.srb.core.pojo.entity.UserAccount;
import com.winky.srb.core.pojo.entity.UserInfo;
import com.winky.srb.core.mapper.UserInfoMapper;
import com.winky.srb.core.pojo.entity.UserLoginRecord;
import com.winky.srb.core.pojo.query.UserInfoQuery;
import com.winky.srb.core.pojo.vo.LoginVo;
import com.winky.srb.core.pojo.vo.RegisterVo;
import com.winky.srb.core.pojo.vo.UserInfoVo;
import com.winky.srb.core.service.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 用户基本信息 服务实现类
 * </p>
 *
 * @author Winky
 * @since 2022-02-20
 */
@Service
@Slf4j
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    private UserAccountMapper userAccountMapper;
    @Autowired
    private UserLoginRecordMapper userLoginRecordMapper;


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
        userInfo.setUserType(registerVo.getUserType());
        baseMapper.insert(userInfo);
        //插入用户账户记录 user_account表
        UserAccount userAccount = new UserAccount();
        userAccount.setUserId(userInfo.getId());
        userAccountMapper.insert(userAccount);

    }

    @Transactional( rollbackFor = {Exception.class})
    @Override
    public UserInfoVo login(LoginVo loginVo, String ip) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        Integer userType = loginVo.getUserType();
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();

        wrapper.eq("mobile",mobile);
        wrapper.eq("user_type", userType);
        UserInfo info = baseMapper.selectOne(wrapper);
        //LOGIN_MOBILE_ERROR(208, "用户不存在"),
        Assert.notNull(info,ResponseEnum.LOGIN_MOBILE_ERROR);

        //校验密码
        //LOGIN_PASSWORD_ERROR(-209, "密码不正确"),
        Assert.equals(MD5.encrypt(password), info.getPassword(), ResponseEnum.LOGIN_PASSWORD_ERROR);
        //用户是否被禁用
        //LOGIN_DISABLED_ERROR(-210, "用户已被禁用"),
        Integer status = info.getStatus();
        Assert.equals(status,UserInfo.STATUS_NORMAL,ResponseEnum.LOGIN_LOKED_ERROR);

        //记录登录日志
        UserLoginRecord userLoginRecord = new UserLoginRecord();
        userLoginRecord.setUserId(info.getId());
        userLoginRecord.setIp(ip);
        userLoginRecordMapper.insert(userLoginRecord);
        //在登录历史中添加记录
        log.info("添加登录历史记录");

        //生成token
        String token = JwtUtils.createToken(info.getId(), info.getName());
        UserInfoVo userInfoVO = new UserInfoVo();
        userInfoVO.setToken(token);
        userInfoVO.setName(info.getName());
        userInfoVO.setNickName(info.getNickName());
        userInfoVO.setHeadImg(info.getHeadImg());
        userInfoVO.setMobile(info.getMobile());
        userInfoVO.setUserType(userType);
        return userInfoVO;
    }

    @Override
    public IPage<UserInfo> listPage(Page<UserInfo> userInfoPage, UserInfoQuery query) {
        if(query == null){
            return baseMapper.selectPage(userInfoPage,null);
        }
        String mobile = query.getMobile();
        Integer userType = query.getUserType();
        Integer status = query.getStatus();

        QueryWrapper<UserInfo> qw = new QueryWrapper<>();
        qw.eq(StringUtils.isNotBlank(mobile), "mobile", mobile)
                .eq(status != null, "status", status)
                .eq(userType != null, "user_type", userType);
        Page<UserInfo> userInfoPage1 = baseMapper.selectPage(userInfoPage, qw);
        return userInfoPage1;
    }

    @Override
    public void lock(Long id, Integer status) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userInfo.setStatus(status);
        baseMapper.updateById(userInfo);
    }
}
