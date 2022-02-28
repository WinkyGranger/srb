package com.winky.srb.core.service;

import com.winky.srb.core.pojo.entity.UserBind;
import com.baomidou.mybatisplus.extension.service.IService;
import com.winky.srb.core.pojo.vo.UserBindVo;

/**
 * <p>
 * 用户绑定表 服务类
 * </p>
 *
 * @author Winky
 * @since 2022-02-20
 */
public interface UserBindService extends IService<UserBind> {
    //账户绑定提交到托管平台的数据
    String commitBindUser(UserBindVo userBindVo, Long userId);
}
