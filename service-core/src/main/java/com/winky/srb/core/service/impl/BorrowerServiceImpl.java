package com.winky.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.winky.srb.core.enums.BorrowerStatusEnum;
import com.winky.srb.core.enums.IntegralEnum;
import com.winky.srb.core.mapper.BorrowerAttachMapper;
import com.winky.srb.core.mapper.UserInfoMapper;
import com.winky.srb.core.mapper.UserIntegralMapper;
import com.winky.srb.core.pojo.entity.Borrower;
import com.winky.srb.core.mapper.BorrowerMapper;
import com.winky.srb.core.pojo.entity.BorrowerAttach;
import com.winky.srb.core.pojo.entity.UserInfo;
import com.winky.srb.core.pojo.entity.UserIntegral;
import com.winky.srb.core.pojo.vo.BorrowerApprovalVO;
import com.winky.srb.core.pojo.vo.BorrowerAttachVO;
import com.winky.srb.core.pojo.vo.BorrowerDetailVO;
import com.winky.srb.core.pojo.vo.BorrowerVO;
import com.winky.srb.core.service.BorrowerAttachService;
import com.winky.srb.core.service.BorrowerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winky.srb.core.service.DictService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 借款人 服务实现类
 * </p>
 *
 * @author Winky
 * @since 2022-02-20
 */
@Service
public class BorrowerServiceImpl extends ServiceImpl<BorrowerMapper, Borrower> implements BorrowerService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private BorrowerAttachMapper borrowerAttachMapper;

    @Autowired
    private DictService dictService;

    @Autowired
    private BorrowerAttachService borrowerAttachService;

    @Autowired
    private UserIntegralMapper userIntegralMapper;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveBorrowerVOByUserId(BorrowerVO borrowerVO, Long userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);

        //保存借款人信息
        Borrower borrower = new Borrower();
        BeanUtils.copyProperties(borrowerVO, borrower);
        borrower.setUserId(userId);
        borrower.setName(userInfo.getName());
        borrower.setIdCard(userInfo.getIdCard());
        borrower.setMobile(userInfo.getMobile());
        borrower.setStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());//认证中
        baseMapper.insert(borrower);

        //保存附件
        List<BorrowerAttach> borrowerAttachList = borrowerVO.getBorrowerAttachList();
        borrowerAttachList.forEach(borrowerAttach -> {
            borrowerAttach.setBorrowerId(borrower.getId());
            borrowerAttachMapper.insert(borrowerAttach);
        });

        //更新会员状态，更新为认证中 user_info表中有字段borrow_auth_status需要更新
        userInfo.setBorrowAuthStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());
        userInfoMapper.updateById(userInfo);

    }

    @Override
    public Integer getStatusByUserId(Long userId) {
        QueryWrapper<Borrower> bqw = new QueryWrapper<>();
        bqw.select("status").eq("user_id",userId);
        List<Object> objects = baseMapper.selectObjs(bqw);

        if(objects.size() == 0){
            //借款人尚未提交信息
            return BorrowerStatusEnum.NO_AUTH.getStatus();
        }
        Integer status = (Integer)objects.get(0);
        return status;
    }

    @Override
    public IPage<Borrower> listPage(Page<Borrower> pageParam, String keyword) {
        if(StringUtils.isBlank(keyword)){
            return baseMapper.selectPage(pageParam,null);
        }
        QueryWrapper<Borrower> qw = new QueryWrapper<>();
        qw.like("name",keyword)
                .or()
                .like("id_card",keyword)
                .or()
                .like("mobile", keyword)
                .orderByDesc("id");
        return baseMapper.selectPage(pageParam,qw);

    }

    @Override
    public BorrowerDetailVO getBorrowerDetailVOById(Long id) {
        Borrower borrower = baseMapper.selectById(id);
        BorrowerDetailVO borrowerDetailVO = new BorrowerDetailVO();
        BeanUtils.copyProperties(borrower, borrowerDetailVO);
        //婚否
        borrowerDetailVO.setMarry(borrower.getMarry()?"是":"否");
        //性别
        borrowerDetailVO.setSex(borrower.getSex()==1?"男":"女");

        Integer education = borrower.getEducation();
        Integer industry = borrower.getIndustry();
        Integer income = borrower.getIncome();
        Integer returnSource = borrower.getReturnSource();
        Integer contactsRelation = borrower.getContactsRelation();

        //下拉列表
        borrowerDetailVO.setEducation(dictService.getNameByParentDictCodeAndValue("education",education));
        borrowerDetailVO.setIndustry(dictService.getNameByParentDictCodeAndValue("industry",industry));
        borrowerDetailVO.setIncome(dictService.getNameByParentDictCodeAndValue("income",income));
        borrowerDetailVO.setReturnSource(dictService.getNameByParentDictCodeAndValue("returnSource",returnSource));
        borrowerDetailVO.setContactsRelation(dictService.getNameByParentDictCodeAndValue("contactsRelation",contactsRelation));

        //附件列表
        //审批状态
        String status = BorrowerStatusEnum.getMsgByStatus(borrower.getStatus());
        borrowerDetailVO.setStatus(status);

        //获取附件VO列表
        List<BorrowerAttachVO> borrowerAttachVOList = borrowerAttachService.selectBorrowerAttachVOList(id);
        borrowerDetailVO.setBorrowerAttachVOList(borrowerAttachVOList);

        return borrowerDetailVO;
    }

    /**
     * 借款人审批
     * @param borrowerApprovalVO
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void approval(BorrowerApprovalVO borrowerApprovalVO) {
        Long borrowerId = borrowerApprovalVO.getBorrowerId();
        Borrower borrower = baseMapper.selectById(borrowerId);
        borrower.setStatus(borrowerApprovalVO.getStatus());
        baseMapper.updateById(borrower);

        Long userId = borrower.getUserId();
        UserInfo info = userInfoMapper.selectById(userId);

        //添加积分
        UserIntegral userIntegral = new UserIntegral();
        userIntegral.setUserId(userId);
        userIntegral.setIntegral(borrowerApprovalVO.getInfoIntegral());
        userIntegral.setContent("借款人基本信息");
        userIntegralMapper.insert(userIntegral);

        int curIntegral = info.getIntegral() + borrowerApprovalVO.getInfoIntegral();
        if(borrowerApprovalVO.getIsIdCardOk()) {
            curIntegral += IntegralEnum.BORROWER_IDCARD.getIntegral();
            userIntegral = new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_IDCARD.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_IDCARD.getMsg());
            userIntegralMapper.insert(userIntegral);
        }

        if(borrowerApprovalVO.getIsCarOk()) {
            curIntegral += IntegralEnum.BORROWER_CAR.getIntegral();
            userIntegral = new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_CAR.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_CAR.getMsg());
            userIntegralMapper.insert(userIntegral);
        }

        info.setIntegral(curIntegral);
        //修改审核状态
        info.setBorrowAuthStatus(borrowerApprovalVO.getStatus());
        userInfoMapper.updateById(info);


    }
}
