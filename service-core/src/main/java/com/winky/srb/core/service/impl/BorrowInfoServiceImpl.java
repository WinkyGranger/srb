package com.winky.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.winky.common.exception.Assert;
import com.winky.common.result.ResponseEnum;
import com.winky.srb.core.enums.BorrowInfoStatusEnum;
import com.winky.srb.core.mapper.IntegralGradeMapper;
import com.winky.srb.core.mapper.UserInfoMapper;
import com.winky.srb.core.pojo.entity.BorrowInfo;
import com.winky.srb.core.mapper.BorrowInfoMapper;
import com.winky.srb.core.pojo.entity.IntegralGrade;
import com.winky.srb.core.pojo.entity.UserInfo;
import com.winky.srb.core.service.BorrowInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 借款信息表 服务实现类
 * </p>
 *
 * @author Winky
 * @since 2022-02-20
 */
@Service
public class BorrowInfoServiceImpl extends ServiceImpl<BorrowInfoMapper, BorrowInfo> implements BorrowInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private IntegralGradeMapper integralGradeMapper;

    @Override
    public Integer getStatusByUserId(Long userId) {
        QueryWrapper<BorrowInfo> borrowInfoQueryWrapper = new QueryWrapper<>();
        borrowInfoQueryWrapper.select("status").eq("user_id", userId);
        List<Object> objects = baseMapper.selectObjs(borrowInfoQueryWrapper);

        if(objects.size() == 0){
            //借款人尚未提交信息
            return BorrowInfoStatusEnum.NO_AUTH.getStatus();
        }
        Integer status = (Integer)objects.get(0);
        return status;
    }

    @Override
    public BigDecimal getBorrowAmount(Long userId) {
        //获取用户积分
        UserInfo info = userInfoMapper.selectById(userId);
        Assert.notNull(info, ResponseEnum.WEIXIN_FETCH_USERINFO_ERROR);
        Integer integral = info.getIntegral();
        //根据积分查额度
        QueryWrapper<IntegralGrade> qw = new QueryWrapper<>();
        qw.le("integral_start",integral)
                .ge("integral_end",integral);
        IntegralGrade integralGrade = integralGradeMapper.selectOne(qw);
        if(integralGrade == null){
            return new BigDecimal("0");
        }
        BigDecimal borrowAmount = integralGrade.getBorrowAmount();
        return borrowAmount;
    }
}
