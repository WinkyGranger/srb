package com.winky.srb.core.service;

import com.winky.srb.core.pojo.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.winky.srb.core.pojo.vo.RegisterVo;

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
}
