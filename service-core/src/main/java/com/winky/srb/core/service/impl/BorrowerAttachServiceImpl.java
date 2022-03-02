package com.winky.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.winky.srb.core.pojo.entity.BorrowerAttach;
import com.winky.srb.core.mapper.BorrowerAttachMapper;
import com.winky.srb.core.pojo.vo.BorrowerAttachVO;
import com.winky.srb.core.service.BorrowerAttachService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 借款人上传资源表 服务实现类
 * </p>
 *
 * @author Winky
 * @since 2022-02-20
 */
@Service
public class BorrowerAttachServiceImpl extends ServiceImpl<BorrowerAttachMapper, BorrowerAttach> implements BorrowerAttachService {

    @Override
    public List<BorrowerAttachVO> selectBorrowerAttachVOList(Long id) {
        QueryWrapper<BorrowerAttach> qw = new QueryWrapper<>();
        qw.eq("borrower_id",id);
        List<BorrowerAttach> borrowerAttaches = baseMapper.selectList(qw);
        List<BorrowerAttachVO> borrowerAttachVOS = new ArrayList<>();

        borrowerAttaches.forEach(borrowerAttach -> {
            BorrowerAttachVO borrowerAttachVO = new BorrowerAttachVO();
            borrowerAttachVO.setImageUrl(borrowerAttach.getImageUrl());
            borrowerAttachVO.setImageType(borrowerAttach.getImageType());
            borrowerAttachVOS.add(borrowerAttachVO);
        });
        return borrowerAttachVOS;
    }
}
