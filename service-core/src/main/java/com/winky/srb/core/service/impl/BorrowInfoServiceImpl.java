package com.winky.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.winky.common.exception.Assert;
import com.winky.common.result.ResponseEnum;
import com.winky.srb.core.enums.BorrowAuthEnum;
import com.winky.srb.core.enums.BorrowInfoStatusEnum;
import com.winky.srb.core.enums.BorrowerStatusEnum;
import com.winky.srb.core.enums.UserBindEnum;
import com.winky.srb.core.mapper.BorrowerMapper;
import com.winky.srb.core.mapper.IntegralGradeMapper;
import com.winky.srb.core.mapper.UserInfoMapper;
import com.winky.srb.core.pojo.entity.BorrowInfo;
import com.winky.srb.core.mapper.BorrowInfoMapper;
import com.winky.srb.core.pojo.entity.Borrower;
import com.winky.srb.core.pojo.entity.IntegralGrade;
import com.winky.srb.core.pojo.entity.UserInfo;
import com.winky.srb.core.pojo.vo.BorrowInfoApprovalVO;
import com.winky.srb.core.pojo.vo.BorrowerDetailVO;
import com.winky.srb.core.service.BorrowInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winky.srb.core.service.BorrowerService;
import com.winky.srb.core.service.DictService;
import com.winky.srb.core.service.LendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private DictService dictService;
    @Autowired
    private  BorrowInfoMapper borrowInfoMapper;
    @Autowired
    private BorrowerMapper borrowerMapper;
    @Autowired
    private BorrowerService borrowerService;
    @Autowired
    private LendService lendService;

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

    @Override
    public void saveBorrowInfo(BorrowInfo borrowInfo, Long userId) {
        UserInfo info = userInfoMapper.selectById(userId);
        int status =info.getStatus().intValue();
        int bindStatus = info.getBindStatus().intValue();
        int borrowAuthStatus = info.getBorrowAuthStatus().intValue();

        //判断用户绑定状态
        Assert.isTrue(bindStatus==UserBindEnum.BIND_OK.getStatus().intValue(),ResponseEnum.USER_NO_BIND_ERROR);
        //判断额度申请状态
        Assert.isTrue(borrowAuthStatus== BorrowerStatusEnum.AUTH_OK.getStatus(),ResponseEnum.USER_NO_AMOUNT_ERROR);
        //借款人申请额度是否充足
        //判断借款额度是否足够
        BigDecimal borrowAmount = this.getBorrowAmount(userId);
        Assert.isTrue(
                borrowInfo.getAmount().doubleValue() <= borrowAmount.doubleValue(),
                ResponseEnum.USER_AMOUNT_LESS_ERROR);
        //储存borrowInfo数据
        borrowInfo.setUserId(userId);
        //百分比转成小数
        borrowInfo.setBorrowYearRate( borrowInfo.getBorrowYearRate().divide(new BigDecimal(100)));
        borrowInfo.setStatus(BorrowInfoStatusEnum.CHECK_RUN.getStatus());
        baseMapper.insert(borrowInfo);
    }

    public void setMap(BorrowInfo borrowInfo){
        String returnMethod = dictService.getNameByParentDictCodeAndValue("returnMethod", borrowInfo.getReturnMethod());
        String moneyUse = dictService.getNameByParentDictCodeAndValue("moneyUse", borrowInfo.getMoneyUse());
        String status = BorrowInfoStatusEnum.getMsgByStatus(borrowInfo.getStatus());
        borrowInfo.getParam().put("returnMethod", returnMethod);
        borrowInfo.getParam().put("moneyUse", moneyUse);
        borrowInfo.getParam().put("status", status);
    }
    @Override
    public List<BorrowInfo> selectList() {
        List<BorrowInfo> borrowInfoList = baseMapper.selectBorrowInfoList();
        borrowInfoList.forEach(borrowInfo -> {
            this.setMap(borrowInfo);
        });

        return borrowInfoList;
    }

    @Override
    public Map<String, Object> getBorrowInfoDetail(Long id) {
        //查询借款对象 BorrowInfo
        BorrowInfo borrowInfo = borrowInfoMapper.selectById(id);
        this.setMap(borrowInfo);
        //根据user_id获取借款人对象
        QueryWrapper<Borrower> borrowerQueryWrapper = new QueryWrapper<Borrower>();
        borrowerQueryWrapper.eq("user_id", borrowInfo.getUserId());
        Borrower borrower = borrowerMapper.selectOne(borrowerQueryWrapper);
        //组装借款人对象
        BorrowerDetailVO borrowerDetailVO = borrowerService.getBorrowerDetailVOById(borrower.getId());

        //组装数据
        Map<String, Object> result = new HashMap<>();
        result.put("borrowInfo", borrowInfo);
        result.put("borrower", borrowerDetailVO);
        return result;
    }

    @Override
    public void approval(BorrowInfoApprovalVO borrowInfoApprovalVO) {
        //修改借款信息状态
        Long borrowInfoId = borrowInfoApprovalVO.getId();
        BorrowInfo borrowInfo = baseMapper.selectById(borrowInfoId);
        borrowInfo.setStatus(borrowInfoApprovalVO.getStatus());
        baseMapper.updateById(borrowInfo);

        //审核通过则创建标的
        if (borrowInfoApprovalVO.getStatus().intValue() == BorrowInfoStatusEnum.CHECK_OK.getStatus().intValue()) {
            //创建标的
            lendService.createLend(borrowInfoApprovalVO,borrowInfo);
        }
    }
}
