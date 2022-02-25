package com.winky.srb.core.service;

import com.winky.srb.core.pojo.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.winky.srb.core.pojo.vo.LoginVo;
import com.winky.srb.core.pojo.vo.RegisterVo;
import com.winky.srb.core.pojo.vo.UserInfoVo;

/**
 * <p>
 * 用户基本信息 服务类
 * </p>
 *
 * @author Winky
 * @since 2022-02-20
 */
public interface UserInfoService extends IService<UserInfo> {

    void register(RegisterVo registerVo);

    UserInfoVo login(LoginVo loginVo, String ip);

    //根据查询条件和分页条件查询用户信息
}
