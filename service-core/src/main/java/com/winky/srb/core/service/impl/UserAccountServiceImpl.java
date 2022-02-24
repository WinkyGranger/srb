package com.winky.srb.core.service.impl;

import com.winky.srb.core.pojo.entity.UserAccount;
import com.winky.srb.core.mapper.UserAccountMapper;
import com.winky.srb.core.service.UserAccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
