package com.winky.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.winky.srb.core.pojo.entity.UserLoginRecord;
import com.winky.srb.core.mapper.UserLoginRecordMapper;
import com.winky.srb.core.service.UserLoginRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户登录记录表 服务实现类
 * </p>
 *
 * @author Winky
 * @since 2022-02-20
 */
@Service
public class UserLoginRecordServiceImpl extends ServiceImpl<UserLoginRecordMapper, UserLoginRecord> implements UserLoginRecordService {

    @Override
    public List<UserLoginRecord> listTop50(Long userId) {
        QueryWrapper<UserLoginRecord> qw = new QueryWrapper<>();
        qw.eq("user_id",userId).orderByDesc("id").last("limit 50");
        List<UserLoginRecord> userLoginRecords = baseMapper.selectList(qw);
        return userLoginRecords;
    }
}
